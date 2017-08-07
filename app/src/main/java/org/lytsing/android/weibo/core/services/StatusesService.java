/*
 * Copyright (C) 2014-2017 http://lytsing.org
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

package org.lytsing.android.weibo.core.services;

import org.lytsing.android.weibo.core.models.Statuses;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

import java.util.List;

public abstract interface StatusesService {
    /**
     * Returns most recent mentions (tweets containing a user's @screen_name) for the
     * authenticating user, by default returns 20 tweets.
     * The timeline returned is the equivalent of the one seen when you view your mentions on
     * twitter.com.
     * The Twitter REST API goes back up to 800 tweets.
     * @param count (optional) Specifies the number of tweets to try and retrieve, up to a maximum of 200. The value of count is best thought of as a limit to the number of tweets to return because suspended or deleted content is removed after the count has been applied. We include retweets in the count, even if include_rts is not supplied. It is recommended you always send include_rts=1 when using this API method.
     * @param sinceId
     * @param maxId
     * @param trimUser
     * @param contributeDetails
     * @param includeEntities
     * @param cb
     */
    @GET("/1.1/statuses/mentions_timeline.json")
    public abstract void mentionsTimeline(
            @Query("count")Integer count,
            @Query("since_id")Long sinceId,
            @Query("max_id")Long maxId,
            @Query("trim_user")Boolean trimUser,
            @Query("contributor_details")Boolean contributeDetails,
            @Query("include_entities")Boolean includeEntities,
            Callback<List<Statuses>> cb);


    @GET("/1.1/statuses/user_timeline.json")
    public abstract void userTimeline(
            @Query("user_id")Long userId,
            @Query("screen_name")String screenName,
            @Query("count")Integer count,
            @Query("since_id")Long sinceId,
            @Query("max_id")Long maxId,
            @Query("trim_user")Boolean trimUser,
            @Query("exclude_replies")Boolean excludeReplies,
            @Query("contributor_details")Boolean contributeDetails,
            @Query("include_rts")Boolean includeRetweets,
            Callback<List<Statuses>> cb);
}
