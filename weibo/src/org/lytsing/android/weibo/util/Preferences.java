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

package org.lytsing.android.weibo.util;

import android.content.Context;
import android.content.SharedPreferences;

public final class Preferences {

    public static final String PREFERENCES_NAME = "weibo_preferences";
    
    public static final String ACCESS_TOKEN = "access_token";
    
    public static final String EXPIRES_IN = "expires_in";

    public static final String PREF_LAST_SYNC_TIME = "last_sync_time";
    
    public static final String USER_ID = "user_id";
    
	
    public static SharedPreferences get(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
    }
}

