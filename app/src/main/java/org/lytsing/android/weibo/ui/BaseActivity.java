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
import org.lytsing.android.weibo.R;
import org.lytsing.android.weibo.WeiboApplication;

import android.os.Bundle;
import androidx.core.app.NavUtils;
import androidx.fragment.app.FragmentActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * A base activity that handles common functionality in the app.
 * @author Liqing Huang
 */
public abstract class BaseActivity extends FragmentActivity {

    private static final int TOAST_DURATION = Toast.LENGTH_SHORT;

    protected WeiboApplication mApplication;

    protected Oauth2AccessToken mAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApplication = (WeiboApplication) getApplication();
        mAccessToken = mApplication.getOauth2AccessToken();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (this instanceof TimelineActivity) {
                    return false;
                }

                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * @param resId resource id
     */
    public void displayToast(int resId) {
        Toast.makeText(this, resId, TOAST_DURATION).show();
    }

    /**
     * @param text display text
     */
    public void displayToast(CharSequence text) {
        Toast.makeText(this, text, TOAST_DURATION).show();
    }

    public WeiboApplication getWeiboApplication() {
        return mApplication;
    }
}

