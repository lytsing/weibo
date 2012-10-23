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

package org.lytsing.android.weibo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.lytsing.android.weibo.model.Statuses;
import org.lytsing.android.weibo.util.DateTimeUtils;
import org.lytsing.android.weibo.util.Log;
import org.lytsing.android.weibo.util.Util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.weibo.net.WeiboException;

public class StatusItemAdapter extends BaseAdapter {
    
    private Context mContext;

    private List<Statuses> mStatuses;
    
    protected Handler mHandler;

    public StatusItemAdapter(Context context) {
        mContext = context;
        mStatuses = new ArrayList<Statuses>();
        mHandler = new Handler();
    }

    @Override
    public int getCount() {
        return mStatuses.size();
    }

    @Override
    public Object getItem(int position) {
        return mStatuses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        ImageView userImage;
        TextView userName;
        ImageView gps;
        ImageView pic;
        TextView time;
        TextView content;
        ImageView thumbnailPic;
        TextView tweetForm;
        ImageView tweetRedirectPic;
        TextView tweetRedirect;
        ImageView tweetCommentPic;
        TextView tweetComment;
        TextView retweetedText;
        View subLayout;
        ImageView tweetUploadPic2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        Statuses statuses = null;
        ViewHolder holder;
        
        //Log.d("getView:" + position);
        
        if (position < mStatuses.size()) {
            statuses = mStatuses.get(position);
        }
        
        //if (convertView == null) {
            convertView = Util.inflateView(R.layout.list_item_status, mContext);
            holder = new ViewHolder();
            holder.userImage = (ImageView) convertView.findViewById(R.id.ivItemPortrait);
            holder.userName = (TextView) convertView.findViewById(R.id.tvItemName);
            holder.gps = (ImageView) convertView.findViewById(R.id.ivItemGps);
            holder.pic = (ImageView) convertView.findViewById(R.id.ivItemPic);
            holder.time = (TextView) convertView.findViewById(R.id.tvItemDate);
            holder.content = (TextView) convertView.findViewById(R.id.tvItemContent);
            holder.thumbnailPic = (ImageView) convertView.findViewById(R.id.tweet_upload_pic1);
            holder.tweetForm = (TextView) convertView.findViewById(R.id.tweet_form);
            holder.tweetRedirectPic = (ImageView) convertView.findViewById(R.id.tweet_redirect_pic);
            holder.tweetRedirect = (TextView) convertView.findViewById(R.id.tweet_redirect);
            holder.tweetCommentPic = (ImageView) convertView.findViewById(R.id.tweet_comment_pic);
            holder.tweetComment = (TextView) convertView.findViewById(R.id.tweet_comment);
            holder.subLayout = convertView.findViewById(R.id.subLayout);
            holder.retweetedText = (TextView) convertView.findViewById(R.id.tvItemSubContent);
            holder.tweetUploadPic2 = (ImageView) convertView.findViewById(R.id.tweet_upload_pic2);
       
            /*
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        */
        AQuery aq = new AQuery(convertView);
        
        holder.userName.setText(statuses.user.name);

        if (statuses.geo != null) {
            holder.gps.setVisibility(View.VISIBLE);
            Log.d(statuses.geo.toString());
        }
        
        String time = "";
        
        try {
            Date date = Util.parseDate(statuses.created_at);
            if (date != null) {
                time = DateTimeUtils.getInstance(mContext).getTimeDiffString(
                        date.getTime());
            }
        } catch (WeiboException e) {
            Log.e("StatusCode:" + e.getStatusCode() + " " + e.getMessage());
        }
        
        holder.time.setText(time);
        holder.content.setText(statuses.text, TextView.BufferType.SPANNABLE);
        Util.textHighlight(holder.content, "#","#");
        //Util.textHighlight(holder.content, "@",":");
        Util.textHighlight(holder.content, "http://"," ");
        
        holder.tweetForm.setText("来自:" + Html.fromHtml(statuses.source));

        if (statuses.reposts_count > 0) {
            aq.id(R.id.tweet_redirect_pic).visible();
            holder.tweetRedirectPic.setVisibility(View.VISIBLE);
            holder.tweetRedirect.setText(String.valueOf(statuses.reposts_count));
            holder.tweetRedirect.setVisibility(View.VISIBLE);
        }

        if (statuses.comments_count > 0) {
            holder.tweetCommentPic.setVisibility(View.VISIBLE);
            holder.tweetComment.setText(String.valueOf(statuses.comments_count));
            holder.tweetComment.setVisibility(View.VISIBLE);
        }

        if (statuses.thumbnail_pic != null) {
            holder.pic.setVisibility(View.VISIBLE);
            
            final String middleImageUrl = statuses.bmiddle_pic;
            final String originalPicUrl = statuses.original_pic;
            
            Bitmap loadingImg = aq.getCachedImage(R.drawable.chat_pic_loading);
            if (aq.shouldDelay(position, convertView, parent, statuses.thumbnail_pic)) {
                aq.id(holder.thumbnailPic).image(loadingImg, 1.0f).visible();
            } else {
                aq.id(holder.thumbnailPic).image(statuses.thumbnail_pic, true, true, 0, 0, loadingImg,
                        0, 1.0f).visible();
            }
            
            aq.id(holder.thumbnailPic).clicked(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent("org.lytsing.android.weibo.action.showimagevieweractivity");
                    intent.putExtra(Consts.MIDDLE_IMAGE_URL_KEY, middleImageUrl);
                    intent.putExtra(Consts.ORIGINAL_PIC_URL_KEY, originalPicUrl);
                    
                    mContext.startActivity(intent);
                }
                
            }) ;
        }

        Bitmap placeholder = aq.getCachedImage(R.drawable.portrait_image_empty);

        if (aq.shouldDelay(position, convertView, parent, statuses.user.profile_image_url)) {
            aq.id(holder.userImage).image(placeholder, 1.0f);
        } else {
            aq.id(holder.userImage).image(statuses.user.profile_image_url, true, true, 0, 0, placeholder, 0,
                    1.0f);
        }
        
        if (statuses.retweeted_status != null) {
            holder.subLayout.setVisibility(View.VISIBLE);
            
            String text = "";
            if (statuses.retweeted_status.user != null) {
                text = "@" + statuses.retweeted_status.user.name + ":";
            }
            
            text += statuses.retweeted_status.text;;
            
            holder.retweetedText.setText(text, TextView.BufferType.SPANNABLE);
            Util.textHighlight(holder.retweetedText, "#","#");
            //Util.textHighlight(holder.retweetedText, "@",":");
            Util.textHighlight(holder.retweetedText, "http://"," ");

            if (statuses.retweeted_status.thumbnail_pic != null) {
                final String bmiddlePicUrl = statuses.retweeted_status.bmiddle_pic;
                final String originalPicUrl = statuses.retweeted_status.original_pic;

                aq.id(holder.tweetUploadPic2)
                        .image(statuses.retweeted_status.thumbnail_pic)
                        .visible().clicked(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Consts.ACTION_SHOW_IMAGE_VIWVER);
                                intent.putExtra(Consts.MIDDLE_IMAGE_URL_KEY, bmiddlePicUrl);
                                intent.putExtra(Consts.ORIGINAL_PIC_URL_KEY, originalPicUrl);
                                mContext.startActivity(intent);
                            }
                        });
            }
        }
        
        return convertView;
    }
    
    public void addStatuses(Statuses status) {
        mStatuses.add(status);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }
    
    public void addNewestStatuses(List<Statuses> statuses) {
        mStatuses.addAll(0, statuses);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }
}

