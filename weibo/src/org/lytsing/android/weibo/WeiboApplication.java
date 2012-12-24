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

import android.app.Application;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.BitmapAjaxCallback;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;

public class WeiboApplication extends Application {

    private static WeiboApplication sWeiboApplication;

    private static Weibo sWeibo;
    
    private static Oauth2AccessToken sOauth2AccessToken;
    
    @Override
    public void onCreate() {
        super.onCreate();

        sWeiboApplication = this;
        sWeibo = Weibo.getInstance(Configuration.CONSUMER_KEY, Configuration.REDIRECT_CALLBACK_URL);
        
        sOauth2AccessToken = AccessTokenKeeper.readAccessToken(this);

        
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

    public Weibo getWeibo() {
        return sWeibo;
    }
    
    public Oauth2AccessToken getOauth2AccessToken() {
        return sOauth2AccessToken;
    }
}


