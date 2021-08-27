/*
 * Copyright (C) 2012 http://lytsing.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lytsing.android.weibo.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.androidquery.AQuery;
import com.costum.android.widget.PullAndLoadListView;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import net.simonvt.menudrawer.MenuDrawer;
import org.json.JSONException;
import org.json.JSONObject;
import org.lytsing.android.weibo.Consts;
import org.lytsing.android.weibo.R;
import org.lytsing.android.weibo.WeiboApplication;
import org.lytsing.android.weibo.adapters.StatusItemAdapter;
import org.lytsing.android.weibo.core.Configuration;
import org.lytsing.android.weibo.core.models.Statuses;
import org.lytsing.android.weibo.core.models.WeiboObject;
import org.lytsing.android.weibo.toolbox.GsonRequest;
import org.lytsing.android.weibo.toolbox.VolleyErrorHelper;
import org.lytsing.android.weibo.util.Preferences;
import org.lytsing.android.weibo.util.Utils;

/**
 * @author Liqing Huang
 */
public class TimelineActivity extends BaseActivity {

    private final int ON_SUCC_RESPONSE = 0;

    private final int ON_ERROR_RESPONSE = 1;

    private final int ERROR_CODE_RESPONSE = 2;

    private final int PER_REQUEST_COUNT = 20;

    private StatusItemAdapter mAdapter = null;

    private PullAndLoadListView mListView = null;

    private MenuDrawer mMenuDrawer;

    private Menu mOptionsMenu;

    protected long mSinceId = 0;

    protected long mMaxId = 0;

    private AQuery mAq;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.timeline);
        mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.OVERLAY);
        mMenuDrawer.setMenuView(R.layout.menu);

        if (mAccessToken.isSessionValid()) {
            initView();
        } else {
            Intent intent = new Intent(this, AuthenticatedActivity.class);
            startActivity(intent);
        }
    }

    private Intent createComposeIntent() {
        Intent intent = new Intent(this, ComposeActivity.class);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        mOptionsMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mMenuDrawer.toggleMenu();
                break;
            case R.id.menu_compose:
                startActivity(createComposeIntent());
                break;
            case R.id.menu_refresh:
                refreshStatuses(mSinceId);
                break;
            default:
                break;
        }

        return true;
    }

    private void initView() {

        MenuFragment menu = (MenuFragment) getSupportFragmentManager().findFragmentById(R.id.left_menu);
        menu.getListView().setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 1) {
                    Intent intent = new Intent(TimelineActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }

                mMenuDrawer.setActiveView(view);
                mMenuDrawer.closeMenu();
            }
        });

        mAq = new AQuery(this);

        mListView = findViewById(R.id.msg_list_item);

        // Set a listener to be invoked when the list should be refreshed.
        mListView.setOnRefreshListener(() -> {
            // Do work to refresh the list here.
            refreshStatuses(mSinceId);
        });

        mListView.setOnLoadMoreListener(() -> loadMoreData(mMaxId));

        mListView.setLastUpdated(getLastSyncTime(Preferences.PREF_LAST_SYNC_TIME));

        mAdapter = new StatusItemAdapter(this, getWeiboApplication().getImageLoader());


        requestFriendsTimeline();

        mListView.setOnItemClickListener((parent, view, position, id) -> {

            // see: How to determine onItemClick for pulltorefreshlistview
            // http://stackoverflow.com/questions/10959030/how-to-determine-onitemclick-for-pulltorefreshlistview
            Statuses status = (Statuses) mListView
                    .getItemAtPosition(position);

            if (status == null) {
                return;
            } else {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Consts.STATUSES_KEY, status);

                Intent intent = new Intent(TimelineActivity.this, StatusDetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
        //mMenuDrawer.restoreState(inState.getParcelable(STATE_MENUDRAWER));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO:
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        final int drawerState = mMenuDrawer.getDrawerState();
        if (drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING) {
            mMenuDrawer.closeMenu();
            return;
        }

        super.onBackPressed();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ON_SUCC_RESPONSE:
                    setLastSyncTime(Utils.getNowLocaleTime());
                    mAdapter.notifyDataSetChanged();
                    mListView.onLoadMoreComplete();
                    break;
                case ERROR_CODE_RESPONSE:
                case ON_ERROR_RESPONSE:
                    displayToast("Error:" + msg.obj);
                    mListView.onLoadMoreComplete();
                    break;
                default:
                    break;
            }
        }
    };

    private void showLoadingIndicator() {
        mAq.id(R.id.placeholder_loading).visible();
        setRefreshActionButtonState(true);
    }

    private void hideLoadingIndicator() {
        mAq.id(R.id.placeholder_loading).gone();
        setRefreshActionButtonState(false);
    }

    private void showErrorIndicator() {
        mAq.id(R.id.placeholder_error).visible();
    }

    private void hideErrorIndicator() {
        mAq.id(R.id.placeholder_error).gone();
    }

    private String getLastSyncTime(String pre) {
        SharedPreferences prefs = Preferences.get(this);
        String time = prefs.getString(pre, "");
        return time;
    }

    private void setLastSyncTime(String time) {
        SharedPreferences.Editor editor = Preferences.get(this).edit();
        editor.putString(Preferences.PREF_LAST_SYNC_TIME, time);
        editor.commit();
    }

    private void refreshStatuses(long sinceId) {
        setRefreshActionButtonState(true);

        String url = Configuration.API_SERVER + "/statuses/friends_timeline.json";
        WeiboParameters  params = new WeiboParameters();
        params.put("access_token", mAccessToken.getToken());
        params.put("since_id", sinceId);
        params.put("max_id", 0);
        params.put("count", PER_REQUEST_COUNT);
        params.put("page", 1);
        params.put("base_app", 0);
        params.put("feature", 0);

        url = url + "?" + params.encodeUrl();

        GsonRequest<WeiboObject> refreshRequest = new GsonRequest<WeiboObject>(Method.GET, url,
                null,
                WeiboObject.class,
                response -> {
                    final int refreshCount = response.statuses.size();
                    Logger.d("newsMsgLists length == " + refreshCount);
                    if (refreshCount > 0) {
                        mSinceId = response.statuses.get(0).id;
                        mAdapter.addNewestStatuses(response.statuses);
                    }

                    mAdapter.notifyDataSetChanged();
                    // Call onRefreshComplete when the list has been
                    // refreshed.
                    mListView.onRefreshComplete();
                    mListView.setLastUpdated(getLastSyncTime(Preferences.PREF_LAST_SYNC_TIME));

                    setLastSyncTime(Utils.getNowLocaleTime());

                    if (refreshCount > 0) {
                        displayToast(String
                                .format(getResources().getString(R.string.new_blog_toast),
                                        refreshCount));
                    } else {
                        displayToast(R.string.no_new_blog_toast);
                    }

                    setRefreshActionButtonState(false);
                },
                e -> {
                    Utils.showToast(TimelineActivity.this, "Error:" + e.getMessage());
                    setRefreshActionButtonState(false);
                });

        WeiboApplication.getWeiboApplication().addToRequestQueue(refreshRequest);
    }

    private void requestFriendsTimeline() {

        hideErrorIndicator();
        showLoadingIndicator();

        String url = Configuration.API_SERVER + "/statuses/friends_timeline.json";
        WeiboParameters  params = new WeiboParameters();
        params.put("access_token", mAccessToken.getToken());
        params.put("count", PER_REQUEST_COUNT);
        params.put("page", 1);
        params.put("base_app", 0);
        params.put("feature", 0);

        url = url + "?" + params.encodeUrl();

        GsonRequest<WeiboObject> timelineRequest = new GsonRequest<WeiboObject>(Method.GET, url,
                null,
                WeiboObject.class,
                createMyReqSuccessListener(),
                createMyReqErrorListener());

        WeiboApplication.getWeiboApplication().addToRequestQueue(timelineRequest);
    }

    private Response.Listener<WeiboObject> createMyReqSuccessListener() {
        return info -> {
            for (Statuses status : info.statuses) {
                mAdapter.addStatuses(status);
                mMaxId = status.id - 1;
            }

            if (info.statuses.size() > 0) {
                mSinceId = info.statuses.get(0).id;
            }

            hideLoadingIndicator();
            mAq.id(R.id.placeholder_error).gone();

            showContents();
            mAdapter.notifyDataSetChanged();
            setLastSyncTime(Utils.getNowLocaleTime());
        };
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return error -> {
            Logger.e(error.getMessage());
            String errorMsg = VolleyErrorHelper.getMessage(error, getApplicationContext());

            hideLoadingIndicator();
            showErrorIndicator();
            mAq.id(R.id.error_msg).text(errorMsg);
            mAq.id(R.id.retry_button).clicked(v -> requestFriendsTimeline());
        };
    }

    public void setRefreshActionButtonState(boolean refreshing) {
        if (mOptionsMenu == null) {
            return;
        }

        final MenuItem refreshItem = mOptionsMenu.findItem(R.id.menu_refresh);
        if (refreshItem != null) {
            if (refreshing) {
                refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
            } else {
                refreshItem.setActionView(null);
            }
        }
    }

    private void loadMoreData(final long maxId) {
        StatusesAPI statusAPI = new StatusesAPI(mAccessToken);
        statusAPI.friendsTimeline(0, maxId, PER_REQUEST_COUNT, 1, false, StatusesAPI.FEATURE_ALL,
                false,
                new RequestListener() {

                    @Override
                    public void onComplete(String result) {
                        try {
                            Message msg = Message.obtain();
                            if (TextUtils.isEmpty(result) || result.contains("error_code")) {
                                msg.what = ERROR_CODE_RESPONSE;
                                JSONObject obj = new JSONObject(result);
                                msg.obj = obj.getString("error");
                            } else {
                                msg.what = ON_SUCC_RESPONSE;
                                Gson gson = new Gson();
                                WeiboObject response = gson.fromJson(result, WeiboObject.class);

                                for (Statuses status : response.statuses) {
                                    mAdapter.addStatuses(status);
                                    mMaxId = status.id - 1;
                                }

                                if (maxId == 0 && response.statuses.size() > 0) {
                                    mSinceId = response.statuses.get(0).id;
                                }
                            }

                            mHandler.sendMessage(msg);
                        } catch (JSONException e) {
                            Logger.e(e.getMessage());
                        }
                    }

                    @Override
                    public void onWeiboException(final WeiboException e) {
                        Message msg = Message.obtain();
                        msg.what = ON_ERROR_RESPONSE;
                        msg.obj = e.getMessage();

                        mHandler.sendMessage(msg);
                    }
                });
    }

    private void showContents() {
        mAq.id(R.id.timelist_list).visible();

        // FIXME: put it here, else will pop up "Tap to Refresh"
        mListView.setAdapter(mAdapter);
    }
}

