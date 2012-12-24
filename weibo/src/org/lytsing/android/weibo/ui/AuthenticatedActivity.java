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

package org.lytsing.android.weibo.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.sso.SsoHandler;

import org.lytsing.android.weibo.AccessTokenKeeper;
import org.lytsing.android.weibo.util.Preferences;

public class AuthenticatedActivity extends BaseActivity {
    private SsoHandler mSsoHandler;

    private Oauth2AccessToken accessToken;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (hasAccessToken()) {
            enterTimeline();
        } else {
            mSsoHandler = new SsoHandler(this, mWeibo);
            mSsoHandler.authorize(new AuthDialogListener());
        }
    }

    private boolean hasAccessToken() {
        SharedPreferences prefs = Preferences.get(this);
        String token = prefs.getString(Preferences.ACCESS_TOKEN, null);
        String expires_in = String.valueOf(prefs.getLong(Preferences.EXPIRES_IN, 0));

        return (token != null && expires_in != null);
    }

    private void enterTimeline() {
        Intent intent = new Intent(AuthenticatedActivity.this, TimelineActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    class AuthDialogListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            String token = values.getString(Preferences.ACCESS_TOKEN);
            String expires_in = values.getString(Preferences.EXPIRES_IN);

            accessToken = new Oauth2AccessToken(token, expires_in);
            if (accessToken.isSessionValid()) {

                AccessTokenKeeper.keepAccessToken(AuthenticatedActivity.this,
                        accessToken);

                enterTimeline();
            }
        }

        @Override
        public void onError(WeiboDialogError e) {
            displayToast("Auth error : " + e.getMessage());
        }

        @Override
        public void onCancel() {
            displayToast("Auth cancel");
        }

        @Override
        public void onWeiboException(WeiboException e) {
            displayToast("Auth exception : " + e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * 下面两个注释掉的代码，仅当sdk支持sso时有效，
         */
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
}
