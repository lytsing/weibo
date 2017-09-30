/*
 * Copyright (C) 2012 http://lytsing.org
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

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import org.lytsing.android.weibo.core.Configuration;
import org.lytsing.android.weibo.core.Session;
import org.lytsing.android.weibo.util.Preferences;

import android.content.Intent;
import android.os.Bundle;

/**
 *
 * @author lytsing
 *
 */
public class AuthenticatedActivity extends BaseActivity {
    private SsoHandler mSsoHandler;

    private Oauth2AccessToken mAccessToken;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WeiboAuth weiboAuth = new WeiboAuth(this, Configuration.CONSUMER_KEY,
                Configuration.REDIRECT_CALLBACK_URL, Configuration.SCOPE);

        mSsoHandler = new SsoHandler(this, weiboAuth);
        mSsoHandler.authorize(new SelfWbAuthListener());
    }

    private void enterTimeline() {
        Intent intent = new Intent(AuthenticatedActivity.this, TimelineActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private class SelfWbAuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(final Bundle values) {

            AuthenticatedActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String token = values.getString(Preferences.ACCESS_TOKEN);
                    String expiresIn = values.getString(Preferences.EXPIRES_IN);

                    mAccessToken = new Oauth2AccessToken(token, expiresIn);
                    if (mAccessToken.isSessionValid()) {

                        Session.save(AuthenticatedActivity.this,
                                mAccessToken);
                        getWeiboApplication().setOauth2AccessToken(mAccessToken);
                        enterTimeline();
                    }
                }
            });
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

        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
}

