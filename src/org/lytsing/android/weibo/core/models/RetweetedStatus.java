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

package org.lytsing.android.weibo.core.models;


import java.io.Serializable;
import java.util.List;

public class RetweetedStatus implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 3731983092212626968L;

    /** 创建时间 */
    public String created_at;

    /** 创建时间 */
    public long id;

    /** 字符串型的微博ID */
    public String idstr;

    /** 微博信息内容 */
    public String text;

    /** 微博来源 */
    public String source;

    /** 是否已收藏 */
    public boolean favorited;

    /** 是否被截断 */
    public boolean truncated;

    /** 回复ID */
    public String in_reply_to_status_id;

    /** 回复人UID */
    public String in_reply_to_user_id;

    /** 回复人昵称 */
    public String in_reply_to_screen_name;

    /** 缩略图 */
    public String thumbnail_pic;

    /** 中型图片 */
    public String bmiddle_pic;

    /** 原始图片 */
    public String original_pic;

    public List<Geo> geos;

    public String mid;

    /** 转发数 */
    public int reposts_count;

    /** 评论数 */
    public int comments_count;

    public int melvel;

    public Visible visible;

    public User user;
}

