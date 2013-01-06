/*
 * Copyright (C) 2012 lytsing.org
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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.androidquery.AQuery;
import com.costum.android.widget.PullAndLoadListView;
import com.google.gson.Gson;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.WeiboAPI.FEATURE;
import com.weibo.sdk.android.net.RequestListener;

import org.lytsing.android.weibo.Consts;
import org.lytsing.android.weibo.R;
import org.lytsing.android.weibo.StatusItemAdapter;
import org.lytsing.android.weibo.model.Statuses;
import org.lytsing.android.weibo.model.WeiboObject;
import org.lytsing.android.weibo.util.Log;
import org.lytsing.android.weibo.util.Preferences;
import org.lytsing.android.weibo.util.Util;

import java.io.IOException;

public class TimelineActivity extends BaseActivity {

    private StatusItemAdapter mAdapter = null;

    private PullAndLoadListView mListView = null;

    protected long mSinceId = 0;

    protected long mMaxId = 0;

    private ActionBar mActionBar;

    private AQuery aq;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (hasAccessToken()) {
            initView();
        } else {
            Intent intent = new Intent(this, AuthenticatedActivity.class);
            startActivity(intent);
        }
    }
    
    private boolean hasAccessToken() {
        SharedPreferences prefs = Preferences.get(this);
        String token = prefs.getString(Preferences.ACCESS_TOKEN, null);
        String expires_in = String.valueOf(prefs.getLong(Preferences.EXPIRES_IN, 0));

        return (token != null && expires_in != null);
    }

    private Intent createComposeIntent() {
        Intent intent = new Intent(this, ComposeActivity.class);
        return intent;
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, TimelineActivity.class);
        // i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        return intent;
    }

    private void initView() {
        setContentView(R.layout.timeline);

        aq = new AQuery(this);

        mActionBar = (ActionBar) findViewById(R.id.actionbar);
        mActionBar.setHomeAction(new IntentAction(this, createIntent(this), R.drawable.logo_h));

        final Action composeAction = new IntentAction(this, createComposeIntent(),
                R.drawable.ic_action_compose);
        mActionBar.addAction(composeAction);

        mActionBar.addAction(new Action() {
            @Override
            public void performAction(View view) {
                refreshStatus(mSinceId);
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_action_refresh;
            }
        });

        mListView = ((PullAndLoadListView) findViewById(R.id.msg_list_item));

        // Set a listener to be invoked when the list should be refreshed.
        mListView.setOnRefreshListener(new PullAndLoadListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Do work to refresh the list here.
                refreshStatus(mSinceId);
            }
        });

        mListView.setOnLoadMoreListener(new PullAndLoadListView.OnLoadMoreListener() {

            public void onLoadMore() {
                loadMoreData(mMaxId);
            }
        });

        mListView.setLastUpdated(getLastSyncTime(Preferences.PREF_LAST_SYNC_TIME));

        mAdapter = new StatusItemAdapter(this);

        getFriendsTimeline(0, 0);

        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {

                // see: How to determine onItemClick for pulltorefreshlistview
                // http://stackoverflow.com/questions/10959030/how-to-determine-onitemclick-for-pulltorefreshlistview
                Statuses status = (Statuses) mListView
                        .getItemAtPosition(position);

                if (status == null) {
                    return;
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Consts.STATUSES_KEY, status);

                    Intent intent = new Intent(TimelineActivity.this,
                            StatusDetailActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }

    private void showLoadingIndicator() {
        aq.id(R.id.placeholder_loading).visible();
    }

    private void hideLoadingIndicator() {
        aq.id(R.id.placeholder_loading).gone();
    }
    
    private void showErrorIndicator() {
        aq.id(R.id.placeholder_error).visible();
    }
    
    private void hideErrorIndicator() {
        aq.id(R.id.placeholder_error).gone();
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

    private void refreshStatus(long sinceId) {
        mActionBar.setProgressBarVisibility(View.VISIBLE);

        StatusesAPI statusAPI = new StatusesAPI(mAccessToken);
        statusAPI.friendsTimeline(sinceId, 0, 20, 1, false, FEATURE.ALL, false,
                new RequestListener() {

                    @Override
                    public void onComplete(String result) {
                        Gson gson = new Gson();
                        WeiboObject response = gson.fromJson(result, WeiboObject.class);

                        final int refreshCount = response.statuses.size();
                        Log.d("newsMsgLists length == " + refreshCount);
                        if (refreshCount > 0) {
                            mSinceId = response.statuses.get(0).id;
                            mAdapter.addNewestStatuses(response.statuses);
                        }

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                mActionBar.setProgressBarVisibility(View.GONE);
                                mAdapter.notifyDataSetChanged();
                                // Call onRefreshComplete when the list has been
                                // refreshed.
                                mListView.onRefreshComplete();
                                mListView
                                        .setLastUpdated(getLastSyncTime(Preferences.PREF_LAST_SYNC_TIME));

                                setLastSyncTime(Util.getNowLocaleTime());

                                if (refreshCount > 0) {
                                    displayToast(String.format(getResources().getString(
                                            R.string.new_blog_toast), refreshCount));
                                } else {
                                    displayToast(R.string.no_new_blog_toast);
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(final WeiboException e) {
                        Util.showToast(TimelineActivity.this, "Error:" + e.getMessage());
                    }

                    @Override
                    public void onIOException(IOException e) {
                        // TODO Auto-generated method stub

                    }

                });
    }

    private void getFriendsTimeline(final long sinceId, final long maxId) {

        hideErrorIndicator();
        showLoadingIndicator();

        StatusesAPI statusAPI = new StatusesAPI(mAccessToken);
        statusAPI.friendsTimeline(sinceId, maxId, 20, 1, false, FEATURE.ALL, false,
                new RequestListener() {

                    @Override
                    public void onComplete(String result) {

                        //Util.writeUpdateInfo(result);
                        Gson gson = new Gson();
                        WeiboObject response = gson.fromJson(result, WeiboObject.class);

                        for (Statuses status : response.statuses) {
                            mAdapter.addStatuses(status);

                            if (sinceId == 0) {
                                mMaxId = status.id - 1;
                            }
                        }

                        if (maxId == 0 && response.statuses.size() > 0) {
                            mSinceId = response.statuses.get(0).id;
                        }

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                hideLoadingIndicator();
                                aq.id(R.id.placeholder_error).gone();

                                showContents();
                                mAdapter.notifyDataSetChanged();
                                setLastSyncTime(Util.getNowLocaleTime());
                            }
                        });
                    }

                    @Override
                    public void onError(final WeiboException e) {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                showErrorIndicator();
                                aq.id(R.id.error_msg).text(e.getMessage());
                                aq.id(R.id.retry_button).clicked(new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        getFriendsTimeline(sinceId, maxId);
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onIOException(IOException e) {

                    }

                });

    }

    private void loadMoreData(final long maxId) {
        StatusesAPI statusAPI = new StatusesAPI(mAccessToken);
        statusAPI.friendsTimeline(0, maxId, 20, 1, false, FEATURE.ALL, false,
                new RequestListener() {

                    @Override
                    public void onComplete(String result) {

                        Gson gson = new Gson();
                        WeiboObject response = gson.fromJson(result, WeiboObject.class);

                        for (Statuses status : response.statuses) {
                            mAdapter.addStatuses(status);
                            mMaxId = status.id -1;
                        }

                        if (maxId == 0 && response.statuses.size() > 0) {
                            mSinceId = response.statuses.get(0).id;
                        }

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                setLastSyncTime(Util.getNowLocaleTime());
                                mAdapter.notifyDataSetChanged();
                                // Call onLoadMoreComplete when the LoadMore
                                // task, has finished
                                mListView.onLoadMoreComplete();
                            }
                        });
                    }

                    @Override
                    public void onError(final WeiboException e) {
                        Util.showToast(TimelineActivity.this, "Error:" + e.getMessage());
                    }

                    @Override
                    public void onIOException(IOException e) {
                        // TODO Auto-generated method stub

                    }
                });
    }

    private void showContents() {
        aq.id(R.id.timelist_list).visible();

        // FIXME: put it here, else will pop up "Tap to Refresh"
        mListView.setAdapter(mAdapter);
    }
}
