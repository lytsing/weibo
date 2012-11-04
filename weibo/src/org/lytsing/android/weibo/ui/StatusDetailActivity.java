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
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lytsing.android.weibo.CommentsAdapter;
import org.lytsing.android.weibo.Consts;
import org.lytsing.android.weibo.R;
import org.lytsing.android.weibo.model.Comment;
import org.lytsing.android.weibo.model.Statuses;
import org.lytsing.android.weibo.util.Log;
import org.lytsing.android.weibo.util.Util;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.androidquery.AQuery;
import com.commonsware.cwac.merge.MergeAdapter;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.weibo.net.AsyncWeiboRunner;
import com.weibo.net.AsyncWeiboRunner.RequestListener;
import com.weibo.net.Utility;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;

public class StatusDetailActivity extends BaseActivity implements RequestListener {

    private Statuses mStatus;

    private ActionBar mActionBar;
            
    private MergeAdapter mAdapter = null;
    
    private ListView mListView;
    
    private CommentsAdapter mCommentsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail);
        
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy); 
        
        mActionBar = (ActionBar) findViewById(R.id.actionbar);

        mActionBar.setHomeAction(new IntentAction(this, TimelineActivity.createIntent(this), R.drawable.ic_title_home_default));
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.addAction(new RefreshAction());

        Intent i = this.getIntent();
        
        mStatus = (Statuses) i.getSerializableExtra(Consts.STATUSES_KEY);
        
        initView();

        new GetCommentTask().execute();
    }

    private class RefreshAction extends AbstractAction {

        public RefreshAction() {
            super(R.drawable.ic_action_refresh);
        }

        @Override
        public void performAction(View view) {
            displayToast("refresh action");
        }
    }

    private class GetCommentTask extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {
            mActionBar.setProgressBarVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            String error = loadCommentData();
            
            return error;
        }

        protected void onPostExecute(String result) {
            if (!"OK".equals(result)) {
                displayToast("Error:" + result);
            }
            
            mActionBar.setProgressBarVisibility(View.GONE);
        }
    }


    private void initView() {
        
        mCommentsAdapter = new CommentsAdapter(this);
        mListView = (ListView) findViewById(R.id.list_view);
        
        View view = Util.inflateView(R.layout.list_item_stream_activity, this, null);
        
        mAdapter = new MergeAdapter();
        mAdapter.addView(view);
        mAdapter.addAdapter(mCommentsAdapter);
        mListView.setAdapter(mAdapter);
        
        AQuery aq = new AQuery(view);
                
        aq.id(R.id.stream_user_name).text(mStatus.user.name);
        
        aq.id(R.id.stream_content).text(mStatus.text).visible();
        
        aq.id(R.id.stream_user_image).image(mStatus.user.profile_image_url);
        
        if (mStatus.bmiddle_pic != null) {
            aq.id(R.id.stream_media_1_1).image(mStatus.bmiddle_pic);
        }
        
        if (mStatus.retweeted_status != null) {
            aq.id(R.id.stream_reshare_line).visible();
            String retweetedText = "";
            if (mStatus.retweeted_status.user != null) {
                retweetedText = "@" + mStatus.retweeted_status.user.name + ":";
            }
            retweetedText += mStatus.retweeted_status.text;
            aq.id(R.id.stream_detail_text).text(retweetedText).textColor(Color.rgb(51, 181, 229)).visible();
        }
        
        // The last line
        /*
        aq.id(R.id.tweet_form).text("来自:" + Html.fromHtml(mStatus.source));
        */
        
        if (mStatus.reposts_count > 0) {
            aq.id(R.id.tweet_redirect_pic).visible();
            aq.id(R.id.tweet_redirect).text(String.valueOf(mStatus.reposts_count)).visible();
        }
        
        if (mStatus.comments_count > 0) {
            aq.id(R.id.tweet_comment_pic).visible();
            aq.id(R.id.tweet_comment).text(String.valueOf(mStatus.comments_count)).visible();
        }        
    }

    private String loadCommentData() {
        String error = "OK";
        String string = null;
        try {
            string = getComment(mWeibo, mStatus.idstr);
            //Log.d(string);
        } catch (MalformedURLException e) {
            error = e.getMessage();
            Log.e("MalformedURLException", e);
        } catch (IOException e) {
            error = e.getMessage();
            Log.e("IOException", e);
        } catch (WeiboException e) {
            error = e.getMessage();
            // BUGS: javax.net.ssl.SSLException: Connection closed by peer
            Log.e("WeiboException:" + e.getStatusCode(), e);
        }
        
        if (!"OK".equals(error)) return error;

        try {
            JSONObject data = new JSONObject(string);
            if (data != null) {
                JSONArray c = data.getJSONArray("comments");

                for (int i = 0; i < c.length(); i++) {
                    JSONObject d = c.getJSONObject(i);
                    String commentBody = d.getString("text");
                    String createdAt = d.getString("created_at");
                    Date date = Util.parseDate(createdAt);
                    Comment comment = new Comment();
                    comment.setBody(commentBody);
                    comment.setCreationTime(DateFormat.getDateFormat(getApplicationContext()).format(date));
                    
                    // Just extract the username, it's enough.
                    JSONObject u = d.getJSONObject("user");
                    String author = u.getString("name");
                    comment.setAuthor(author);
                    
                    mCommentsAdapter.addComment(comment);
                }
            }
        } catch (JSONException e) {
            error = e.getMessage();
            Log.e("JSONException", e);
        } catch (WeiboException e) {
            error = e.getMessage();
            Log.e("WeiboException", e);
        }
        
        return error;
    }

    private String getComment(Weibo weibo, String id) throws MalformedURLException,
            IOException, WeiboException {
        String url = Weibo.SERVER + "comments/show.json";
        WeiboParameters bundle = new WeiboParameters();
        bundle.add("source", Weibo.getAppKey());
        bundle.add("id", id);
        String result = weibo.request(this, url, bundle, Utility.HTTPMETHOD_GET, weibo.getAccessToken());
        return result;
    }

    private String addComment(Weibo weibo, String id, String comment)
            throws MalformedURLException, IOException, WeiboException {
        String url = Weibo.SERVER + "comments/create.json";
        WeiboParameters bundle = new WeiboParameters();
        bundle.add("source", Weibo.getAppKey());
        bundle.add("id", id);
        bundle.add("comment", comment);

        AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner(weibo);
        weiboRunner.request(this, url, bundle, Utility.HTTPMETHOD_POST, this);

        return "";
    }
    
    private String shortUrlExpand(Weibo weibo, String url_short) throws WeiboException {
        String url = Weibo.SERVER + "short_url/expand.json";
        WeiboParameters bundle = new WeiboParameters();
        bundle.add("source", Weibo.getAppKey());
        bundle.add("url_short", url_short);
        String result = weibo.request(this, url, bundle, Utility.HTTPMETHOD_GET, weibo.getAccessToken());
        return result;
    }
     
    private String rePost(Weibo weibo, String id, String comment, int is_comment)
            throws MalformedURLException, IOException, WeiboException {
        String url = Weibo.SERVER + "statuses/repost.json";
        WeiboParameters bundle = new WeiboParameters();
        bundle.add("source", Weibo.getAppKey());
        bundle.add("id", id);
        bundle.add("status", comment);
        bundle.add("is_comment", String.valueOf(is_comment));
        
        AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner(weibo);
        weiboRunner.request(this, url, bundle, Utility.HTTPMETHOD_POST, this);

        return "";
    }
    
    
    
    private String addFavorites(Weibo weibo, String id)
            throws MalformedURLException, IOException, WeiboException {
        String url = Weibo.SERVER + "favorites/create.json";
        WeiboParameters bundle = new WeiboParameters();
        bundle.add("source", Weibo.getAppKey());
        bundle.add("id", id);
        
        AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner(weibo);
        weiboRunner.request(this, url, bundle, Utility.HTTPMETHOD_POST, this);

        return "";
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.repost, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.comment_menu_item:
                doPostComment();
                break;
            case R.id.repost_menu_item:
                doRePost();
                break;
            case R.id.favorites_menu_item:
                try {
                    addFavorites(mWeibo, mStatus.idstr);
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (WeiboException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }

        return false;
    }
    
    private void doPostComment() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.write_review, null);
        final EditText commentText;
        commentText = (EditText)textEntryView.findViewById(R.id.comment_box);
        
        new AlertDialog.Builder(StatusDetailActivity.this)
                .setTitle(R.string.add_comment)
                .setView(textEntryView)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                String comment = commentText.getText().toString();
                                if (comment.trim().length() > 0) {
                                    try {
                                        addComment(mWeibo, mStatus.idstr, comment);
                                    } catch (MalformedURLException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    } catch (WeiboException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }

                                /* User clicked OK so do some stuff */
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {

                            }
                        }).create().show();
    }
    
    
    private void doRePost() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.write_review, null);
        final EditText commentText;
        commentText = (EditText)textEntryView.findViewById(R.id.comment_box);
        
        new AlertDialog.Builder(StatusDetailActivity.this)
                .setTitle(R.string.repost)
                .setView(textEntryView)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                String comment = commentText.getText().toString(); // TODO: neet to URLencode
                                int is_comment = (comment.trim().length() > 0 ? 1 : 0);
                                    try {
                                        rePost(mWeibo, mStatus.idstr, comment, is_comment);
                                    } catch (MalformedURLException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    } catch (WeiboException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {

                            }
                        }).create().show();
    }

    @Override
    public void onComplete(String response) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                displayToast(R.string.comment_sucess);
            }
        });
    }

    @Override
    public void onIOException(final IOException e) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                displayToast(e.getMessage());
            }
        });
    }

    @Override
    public void onError(final WeiboException e) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                displayToast(e.getMessage());
            }
        });
    }
}

