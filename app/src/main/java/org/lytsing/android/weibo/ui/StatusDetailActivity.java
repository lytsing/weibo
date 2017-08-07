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


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.android.volley.toolbox.NetworkImageView;
import com.androidquery.AQuery;
import com.commonsware.cwac.merge.MergeAdapter;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.legacy.FavoritesAPI;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lytsing.android.weibo.Consts;
import org.lytsing.android.weibo.R;
import org.lytsing.android.weibo.adapters.CommentsAdapter;
import org.lytsing.android.weibo.core.models.Comment;
import org.lytsing.android.weibo.core.models.Statuses;
import org.lytsing.android.weibo.util.Log;
import org.lytsing.android.weibo.util.Util;

public class StatusDetailActivity extends BaseActivity implements RequestListener {

    private Statuses mStatus;

    private MergeAdapter mAdapter = null;

    private ListView mListView;

    private CommentsAdapter mCommentsAdapter;

    private Menu mOptionsMenu;

    private AQuery aq = null;

    private CommentsAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail);
        api = new CommentsAPI(mAccessToken);
        Intent i = this.getIntent();
        mStatus = (Statuses) i.getSerializableExtra(Consts.STATUSES_KEY);
        initView();
    }

    private String getGeoGoogleMapUrl() {
        StringBuilder sb = new StringBuilder()
                .append("http://maps.google.cn/maps/api/staticmap?center=")
                .append(mStatus.geo.coordinates.get(0))
                .append(",")
                .append(mStatus.geo.coordinates.get(1))
                .append("&zoom=12&size=")
                .append(getResources().getDimensionPixelSize(R.dimen.map_width))
                .append("x")
                .append(getResources().getDimensionPixelSize(R.dimen.map_height))
                .append("&maptype=roadmap&markers=markerStyles|color:red|")
                .append(mStatus.geo.coordinates.get(0))
                .append(",")
                .append(mStatus.geo.coordinates.get(1))
                .append("&sensor=true");

        return sb.toString();
    }

    private void addComment(String content, boolean comment_ori) {
        api.create(content, mStatus.id, comment_ori, new RequestListener() {

            @Override
            public void onComplete(String result) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        displayToast(R.string.comment_success);
                        aq.id(R.id.embedded_text_editor).getEditText().setText("");
                    }
                });
            }

            @Override
            public void onWeiboException(WeiboException e) {
                // TODO Auto-generated method stub

            }
        });
    }

    private void addRepost(String content, boolean comment_ori) {
        StatusesAPI api = new StatusesAPI(mAccessToken);
        api.repost(mStatus.id, content, comment_ori ? StatusesAPI.COMMENTS_RIGAL_STATUSES
                : StatusesAPI.COMMENTS_CUR_STATUSES, new RequestListener() {

            @Override
            public void onComplete(String result) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        displayToast(R.string.repost_success);
                    }
                });
            }

            @Override
            public void onWeiboException(WeiboException e) {
                // TODO Auto-generated method stub

            }

        });
    }


    private void initView() {

        mCommentsAdapter = new CommentsAdapter(this);
        mListView = (ListView) findViewById(R.id.list_view);

        View view = Util.inflateView(R.layout.list_item_stream_activity, this, null);

        mAdapter = new MergeAdapter();
        mAdapter.addView(view);
        mAdapter.addAdapter(mCommentsAdapter);
        mListView.setAdapter(mAdapter);

        aq = new AQuery(view);

        aq.id(R.id.stream_user_name).text(mStatus.user.name);
        aq.id(R.id.stream_content).text(mStatus.text).visible();

        ((NetworkImageView) view.findViewById(R.id.stream_user_image)).setImageUrl(
                mStatus.user.profile_image_url, getWeiboApplication().getImageLoader());

        if (mStatus.bmiddle_pic != null) {
            NetworkImageView bmiddlePic = ((NetworkImageView) view.findViewById(
                    R.id.stream_media_1_1));
            bmiddlePic.setVisibility(View.VISIBLE);
            bmiddlePic.setImageUrl(mStatus.bmiddle_pic, getWeiboApplication().getImageLoader());
        }

        if (mStatus.retweeted_status != null) {
            aq.id(R.id.stream_reshare_line).visible();
            String retweetedText = "";
            if (mStatus.retweeted_status.user != null) {
                retweetedText = "@" + mStatus.retweeted_status.user.name + ":";
            }
            retweetedText += mStatus.retweeted_status.text;
            aq.id(R.id.stream_detail_text).text(retweetedText).textColor(Color.rgb(51, 181, 229))
                    .visible();
        }


        if (mStatus.reposts_count > 0) {
            aq.id(R.id.tweet_redirect_pic).visible();
            aq.id(R.id.tweet_redirect).text(String.valueOf(mStatus.reposts_count)).visible();
        }

        if (mStatus.geo != null) {
            aq.id(R.id.maps_layout).visible();
            NetworkImageView mapView = (NetworkImageView) view.findViewById(R.id.pic_preview_iv);
            mapView.setImageUrl(getGeoGoogleMapUrl(), getWeiboApplication().getImageLoader());
        }

        if (mStatus.comments_count > 0) {
            aq.id(R.id.tweet_comment_pic).visible();
            aq.id(R.id.tweet_comment).text(String.valueOf(mStatus.comments_count)).visible();
            loadCommentData();
        } else {
            mCommentsAdapter.refresh();
        }

        aq = new AQuery(this);
        aq.id(R.id.send_button_comment).clicked(this, "buttonClickedQuickPost");
    }

    public void buttonClickedQuickPost(View button) {

        final String content = aq.id(R.id.embedded_text_editor).getText().toString();

        if (content != null && content.length() > 0) {
            addComment(content, false);
        }
    }

    private void loadCommentData() {
        setSupportProgressBarIndeterminateVisibility(true);

        api.show(mStatus.id, 0, 0, 50, 1, CommentsAPI.AUTHOR_FILTER_ALL,
                new RequestListener() {

                    @Override
                    public void onComplete(String result) {
                        try {
                            JSONObject data = new JSONObject(result);
                            if (data != null) {
                                JSONArray c = data.getJSONArray("comments");

                                for (int i = 0; i < c.length(); i++) {
                                    JSONObject d = c.getJSONObject(i);
                                    String commentBody = d.getString("text");
                                    String createdAt = d.getString("created_at");
                                    Date date = Util.parseDate(createdAt);
                                    Comment comment = new Comment();
                                    comment.setBody(commentBody);
                                    comment.setCreationTime(DateFormat.getDateFormat(
                                            getApplicationContext()).format(date));

                                    // Just extract the username, it's enough.
                                    JSONObject u = d.getJSONObject("user");
                                    String author = u.getString("name");
                                    comment.setAuthor(author);

                                    mCommentsAdapter.addComment(comment);
                                }

                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        //setRefreshActionButtonState(false);
                                        setSupportProgressBarIndeterminateVisibility(false);

                                        mCommentsAdapter.refresh();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            Log.e("JSONException", e);
                        } catch (WeiboException e) {
                            Log.e("WeiboException", e);
                        }
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onWeiboException(WeiboException e) {
                        // TODO Auto-generated method stub

                    }
                });
    }

    private void addFavorites(long id) {

        FavoritesAPI api = new FavoritesAPI(mAccessToken);
        api.create(id, new RequestListener() {

            @Override
            public void onComplete(String result) {
                Util.showToast(StatusDetailActivity.this, R.string.favorites);
            }

            @Override
            public void onWeiboException(WeiboException e) {
                // TODO Auto-generated method stub

            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.repost, menu);
        mOptionsMenu = menu;

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
                addFavorites(mStatus.id);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void doPostComment() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.write_review, null);
        final EditText commentText;
        commentText = (EditText) textEntryView.findViewById(R.id.comment_box);

        new AlertDialog.Builder(StatusDetailActivity.this)
                .setTitle(R.string.add_comment)
                .setView(textEntryView)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                final String comment = commentText.getText().toString();
                                if (comment != null && comment.trim().length() > 0) {
                                    addComment(comment, false);
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

    private void doRePost() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.write_review, null);
        final EditText commentText;
        commentText = (EditText) textEntryView.findViewById(R.id.comment_box);

        new AlertDialog.Builder(StatusDetailActivity.this)
                .setTitle(R.string.repost)
                .setView(textEntryView)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                final String comment = commentText.getText().toString();
                                final boolean comment_ori = (comment.trim().length() > 0 ? true
                                        : false);

                                addRepost(comment, comment_ori);
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
        Util.showToast(this, R.string.comment_success);
    }

    @Override
    public void onWeiboException(final WeiboException e) {
        Util.showToast(this, e.getMessage());
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
}
