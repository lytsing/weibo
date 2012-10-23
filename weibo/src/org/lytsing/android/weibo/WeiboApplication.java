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

import org.lytsing.android.weibo.util.Preferences;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.BitmapAjaxCallback;
import com.weibo.net.AccessToken;
import com.weibo.net.Oauth2AccessTokenHeader;
import com.weibo.net.Utility;
import com.weibo.net.Weibo;

public class WeiboApplication extends Application {


    private Context mContext;

    private static WeiboApplication sWeiboApplication;

    private static Weibo sWeibo = Weibo.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();

        sWeiboApplication = this;
        mContext = this;

        initWeibo();

        // set the max number of concurrent network connections, default is 4
        AjaxCallback.setNetworkLimit(8);

        // set the max number of icons (image width <= 50) to be cached in
        // memory, default is 20
        BitmapAjaxCallback.setIconCacheLimit(20);

        // set the max number of images (image width > 50) to be cached in
        // memory, default is 20
        BitmapAjaxCallback.setCacheLimit(40);

        // set the max size of an image to be cached in memory, default is 1600
        // pixels (ie. 400x400)
        BitmapAjaxCallback.setPixelLimit(400 * 400);

        // set the max size of the memory cache, default is 1M pixels (4MB)
        BitmapAjaxCallback.setMaxPixelLimit(2000000);

    }
    
    @Override
    public void onLowMemory(){  

        //clear all memory cached images when system is in low memory
        //note that you can configure the max image cache count, see CONFIGURATION
        BitmapAjaxCallback.clearCache();
    }

    public static WeiboApplication getWeiboApplication() {
        return sWeiboApplication;
    }

    private void initWeibo() {

        sWeibo.setupConsumerConfig(Configuration.CONSUMER_KEY, Configuration.CONSUMER_SECRET);
        sWeibo.setRedirectUrl(Configuration.REDIRECT_CALLBACK_URL);

        SharedPreferences prefs = Preferences.get(mContext);

        String token = prefs.getString(Preferences.ACCESS_TOKEN, null);
        String expires_in = prefs.getString(Preferences.EXPIRES_IN, null);

        if (token != null && expires_in != null) {
            AccessToken accessToken = new AccessToken(token,
                    Configuration.CONSUMER_SECRET);
            accessToken.setExpiresIn(expires_in);
            Utility.setAuthorization(new Oauth2AccessTokenHeader());
            sWeibo.setAccessToken(accessToken);
            Weibo.setSERVER("https://api.weibo.com/2/");
        }
    }

    public Weibo getWeibo() {
        return sWeibo;
    }
}


