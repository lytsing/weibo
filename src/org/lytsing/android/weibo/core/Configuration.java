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

package org.lytsing.android.weibo.core;

/**
 * Configuration for weibo sdk api.
 */
public interface Configuration {

    String CONSUMER_KEY = "2357253761";

    String REDIRECT_CALLBACK_URL = "http://www.sina.com";

    public static final String SCOPE =
            "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";

    String API_SERVER  = "https://api.weibo.com/2";
}
