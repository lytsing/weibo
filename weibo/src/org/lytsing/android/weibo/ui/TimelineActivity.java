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

import java.io.IOException;
import java.net.MalformedURLException;

import org.lytsing.android.weibo.Configuration;
import org.lytsing.android.weibo.Consts;
import org.lytsing.android.weibo.R;
import org.lytsing.android.weibo.StatusItemAdapter;
import org.lytsing.android.weibo.model.Statuses;
import org.lytsing.android.weibo.model.WeiboObject;
import org.lytsing.android.weibo.util.Log;
import org.lytsing.android.weibo.util.Preferences;
import org.lytsing.android.weibo.util.Util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.androidquery.AQuery;
import com.costum.android.widget.PullAndLoadListView;
import com.google.gson.Gson;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.weibo.net.AccessToken;
import com.weibo.net.DialogError;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboDialogListener;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;

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

        if (hasLogin()) {
            initView();
        } else {
            mWeibo.authorize(TimelineActivity.this, new AuthDialogListener());
        }
    }

    private boolean hasLogin() {
        SharedPreferences prefs = Preferences.get(this);

        String token = prefs.getString(Preferences.ACCESS_TOKEN, null);
        String expires_in = prefs.getString(Preferences.EXPIRES_IN, null);

        return (token != null && expires_in != null);
    }

    private Intent createComposeIntent() {
        
        String accessToken = mWeibo.getAccessToken().getToken();
        String tokenSecret = mWeibo.getAccessToken().getSecret();
        
        if (TextUtils.isEmpty(accessToken)) {
            Log.e("token can not be null!");
        } else if (TextUtils.isEmpty(tokenSecret)) {
            Log.e("secret can not be null!");
        }
  
        Intent intent = new Intent(this, ComposeActivity.class);
        
        return intent;
    }
    
    public static Intent createIntent(Context context) {
        Intent i = new Intent(context, TimelineActivity.class);
        //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        return i;
    }

    private void initView() {
        setContentView(R.layout.timeline);
        
        aq = new AQuery(this);
        
        mActionBar = (ActionBar) findViewById(R.id.actionbar);
        mActionBar.setHomeAction(new IntentAction(this, createIntent(this), R.drawable.logo_h));

        final Action composeAction = new IntentAction(this, createComposeIntent(), R.drawable.ic_action_compose);
        mActionBar.addAction(composeAction);
        
        mActionBar.addAction(new Action() {
            @Override
            public void performAction(View view) {
                new RefreshStatusTask().execute(mSinceId);
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
                new RefreshStatusTask().execute(mSinceId);
            }
        });
        
        mListView.setOnLoadMoreListener(new PullAndLoadListView.OnLoadMoreListener() {

            public void onLoadMore() {
                new LoadMoreDataTask().execute(mMaxId);
            }
        });
        
        
        mListView.setLastUpdated(getLastSyncTime(Preferences.PREF_LAST_SYNC_TIME));
        
        mAdapter = new StatusItemAdapter(this);
 
        new GetStatusTask().execute();
                         
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
    
    private class RefreshStatusTask extends AsyncTask<Long, Void, String> {

        private int refreshCount;

        @Override
        protected void onPreExecute() {
            mActionBar.setProgressBarVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            
            if ("OK".equals(result)) {
                mActionBar.setProgressBarVisibility(View.GONE);
                // Call onRefreshComplete when the list has been refreshed.
                mListView.onRefreshComplete();
                mListView.setLastUpdated(getLastSyncTime(Preferences.PREF_LAST_SYNC_TIME));

                setLastSyncTime(Util.getNowLocaleTime());
            } else {
                displayToast("Error:" + result);
            }
            
            if (refreshCount > 0) {
                displayToast(String.format(getResources().getString(
                        R.string.new_blog_toast),refreshCount));
            } else {
                displayToast(R.string.no_new_blog_toast);
            }
        }

        @Override
        protected String doInBackground(Long... params) {

            // check if task was cancelled during long api call
            if (isCancelled()) {
                return null;
            }

            long sinceId = params[0];

            String error = "OK";
            String str = null;
            try {
                str = getHomeTimeline(mWeibo, sinceId, 0);
                //Util.writeUpdateInfo(str);
                Log.d("str lenght: " + str.length());
            } catch (MalformedURLException e) {
                error = e.getMessage();
                Log.e("MalformedURLException", e);
            } catch (IOException e) {
                error = e.getMessage();
                Log.e("IOException", e);
            } catch (WeiboException e) {
                Log.e("WeiboException:" + e.getStatusCode(), e);
                error = e.getMessage();
            }

            if (str == null) {
                return error;
            }
            
            Gson gson = new Gson();   
            WeiboObject response = gson.fromJson(str, WeiboObject.class);
            
            refreshCount = response.statuses.size();
            Log.d("newsMsgLists length == " + refreshCount);
            if (refreshCount > 0) {
                mSinceId = response.statuses.get(0).id;
                mAdapter.addNewestStatuses(response.statuses);
            }

            return error;
        }
    }

    private String getHomeTimeline(Weibo weibo, long sinceId, long maxId) throws MalformedURLException,
            IOException, WeiboException {
        String url = Weibo.SERVER + "statuses/home_timeline.json";
        WeiboParameters bundle = new WeiboParameters();
        bundle.add("source", Weibo.getAppKey());
        bundle.add("max_id", String.valueOf(maxId));
        bundle.add("since_id", String.valueOf(sinceId));
        String rlt = weibo.request(this, url, bundle, "GET", weibo.getAccessToken());
        return rlt;
    }

    private String loadStatusList(long sinceId, long maxId) {

        String error = "OK";
        String str = null;
        try {
            str = getHomeTimeline(mWeibo, sinceId, maxId);
            //Util.writeUpdateInfo(str);
            Log.d("str lenght: " + str.length());
        } catch (MalformedURLException e) {
            error = e.getMessage();
            Log.e("MalformedURLException", e);
        } catch (IOException e) {
            error = e.getMessage();
            Log.e("IOException", e);
        } catch (WeiboException e) {
            Log.e("WeiboException:" + e.getStatusCode(), e);
            error = e.getMessage();
            
            if ("expired_token".equals(error)) {
                // delete token && expires_in
                SharedPreferences.Editor editor = Preferences.get(TimelineActivity.this).edit();
                editor.remove(Preferences.ACCESS_TOKEN);
                editor.remove(Preferences.EXPIRES_IN);
                editor.commit();
            }
        }

        if (str == null) {
            return error;
        }
        
        Gson gson = new Gson();   
        WeiboObject response = gson.fromJson(str, WeiboObject.class);
        
        for (Statuses status : response.statuses) {
            mAdapter.addStatuses(status);
            
            if (sinceId == 0) {
                mMaxId = status.id -1;
            }
        }
        
        if (maxId == 0 && response.statuses.size() > 0) {
            mSinceId = response.statuses.get(0).id;
        }
        
        return error;
    }
    
    private class GetStatusTask extends AsyncTask<Void, Void, String> {
        
        @Override
        protected String doInBackground(Void... params) {
            if (isCancelled()) {
                return null;
            }

            String error = loadStatusList(0, 0);
            return error;
        }
        
        protected void onPostExecute(String result) {
            if ("OK".equals(result)) {
                showContents();
                setLastSyncTime(Util.getNowLocaleTime());
            } else {
                aq.id(R.id.fullscreen_loading_indicator).gone();
                displayToast("Error:" + result);
            }
        }
    }
    
    private class LoadMoreDataTask extends AsyncTask<Long, Void, String> {

        @Override
        protected String doInBackground(Long... params) {
            if (isCancelled()) {
                return null;
            }

            long maxId = params[0];
            String error = loadStatusList(0, maxId);
            return error;
        }

        @Override
        protected void onPostExecute(String result) {
            if ("OK".equals(result)) {
                setLastSyncTime(Util.getNowLocaleTime());
            } else {
                displayToast("Error:" + result);
            }

            // Call onLoadMoreComplete when the LoadMore task, has finished
            mListView.onLoadMoreComplete();

            super.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            // Notify the loading more operation has finished
            mListView.onLoadMoreComplete();
        }
    }

    private void showContents() {
        aq.id(R.id.fullscreen_loading_indicator).gone();
        aq.id(R.id.timelist_list).visible();
        
        // FIXME: put it here, else will pop up "Tap to Refresh"
        mListView.setAdapter(mAdapter);
    }
    
    class AuthDialogListener implements WeiboDialogListener {

        @Override
        public void onComplete(Bundle values) {
            String token = values.getString(Preferences.ACCESS_TOKEN);
            String expires_in = values.getString(Preferences.EXPIRES_IN);

            AccessToken accessToken = new AccessToken(token, Configuration.CONSUMER_SECRET);
            accessToken.setExpiresIn(expires_in);
            Weibo.getInstance().setAccessToken(accessToken);
            
            // Save token && expires_in
            SharedPreferences.Editor editor = Preferences.get(TimelineActivity.this).edit();
            editor.putString(Preferences.ACCESS_TOKEN, token);
            editor.putString(Preferences.EXPIRES_IN, expires_in);
            editor.commit();
            
            Intent intent = new Intent(TimelineActivity.this, TimelineActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        @Override
        public void onError(DialogError e) {
            displayToast("Auth error : " + e.getMessage());
        }

        @Override
        public void onCancel() {
            displayToast("Auth cancel");
        }

        @Override
        public void onWeiboException(WeiboException e) {
            displayToast("Auth exception : " + e.getMessage());
        }
    }
}

