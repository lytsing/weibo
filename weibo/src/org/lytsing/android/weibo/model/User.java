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


package org.lytsing.android.weibo.model;

import java.io.Serializable;

public class User implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -7112440254024632167L;

    /** 用户UID */
    public long id;
    
    /** 用户昵称 */
    public String screen_name;
    
    /** 友好显示名称 */
    public String name;

    public String domain;
    
    /** 用户所在地区ID */
    public String province;
    
    /** 用户所在城市ID */
    public String city;
    
    /** 用户所在地 */
    public String location;
    
    /** 用户描述 */
    public String description;
    
    /** 用户博客地址 */
    public String url;
    
    /** 用户头像地址 */
    public String profile_image_url;
    
    /** 用户的个性化域名 */
    public String user_domain;
    
    /** 性别，m：男、f：女、n：未知 */
    public String gender;

    public String weihao;
    
    /** 粉丝数 */
    public int followers_count;
    
    /** 关注数 */
    public int friends_count;
    
    /** 微博数 */
    public int statuses_count;
    
    /** 收藏数 */
    public int favourites_count;
    
    /** 创建时间 */
    public String created_at;
    
    /** 当前登录用户是否已关注该用户 */
    public boolean following;
    
    /** 是否允许所有人给我发私信 */
    public boolean allow_all_act_msg;
    
    
    public String remark;
    
    /** 是否允许带有地理信息 */
    public boolean geo_enabled;
    
    /** 是否是微博认证用户，即带V用户 */
    public boolean verified;
    
    /** 是否允许所有人对我的微博进行评论 */
    public boolean allow_all_comment;
    
    /** 用户大头像地址 */
    public String avatar_large;
    
    /** 认证原因 */
    public String verified_reason;
    
    /** 该用户是否关注当前登录用户 */
    public boolean follow_me;
    
    /** 用户的在线状态，0：不在线、1：在线 */
    public int online_status;
    
    /** 用户的互粉数 */
    public int bi_followers_count;
}
