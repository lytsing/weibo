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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.weibo.sdk.android.Oauth2AccessToken;

import org.lytsing.android.weibo.util.Preferences;

/**
 * 该类用于保存Oauth2AccessToken到sharepreference，并提供读取功能
 * 
 * @author xiaowei6@staff.sina.com.cn
 */
public class AccessTokenKeeper {
    /**
     * 保存accesstoken到SharedPreferences
     * 
     * @param context Activity 上下文环境
     * @param token Oauth2AccessToken
     */
    public static void keepAccessToken(Context context, Oauth2AccessToken token) {
        SharedPreferences pref = Preferences.get(context);
        Editor editor = pref.edit();
        editor.putString(Preferences.ACCESS_TOKEN, token.getToken());
        editor.putLong(Preferences.EXPIRES_IN, token.getExpiresTime());
        editor.commit();
    }

    /**
     * 清空sharepreference
     * 
     * @param context
     */
    public static void clear(Context context) {
        SharedPreferences pref = Preferences.get(context);
        Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 从SharedPreferences读取accessstoken
     * 
     * @param context
     * @return Oauth2AccessToken
     */
    public static Oauth2AccessToken readAccessToken(Context context) {
        Oauth2AccessToken token = new Oauth2AccessToken();
        SharedPreferences pref = Preferences.get(context);
        token.setToken(pref.getString(Preferences.ACCESS_TOKEN, ""));
        token.setExpiresTime(pref.getLong(Preferences.EXPIRES_IN, 0));
        return token;
    }
}
