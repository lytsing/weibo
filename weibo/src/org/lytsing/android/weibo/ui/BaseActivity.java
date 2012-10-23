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

import org.lytsing.android.weibo.R;
import org.lytsing.android.weibo.WeiboApplication;

import com.weibo.net.Weibo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


/**
 * A base activity that handles common functionality in the app.
 */
public abstract class BaseActivity extends Activity {

    private static final int TOAST_DURATION = Toast.LENGTH_SHORT;

    protected WeiboApplication mApplication;

    protected Weibo mWeibo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApplication = (WeiboApplication)getApplication();
        mWeibo = mApplication.getWeibo();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.base, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.setting_menu_item:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.help_info_menu_item:
                break;
            default:
                break;
        }

        return false;
    }

    /**  
     * @param resId
     */
    public void displayToast(int resId) {
        Toast.makeText(this, resId, TOAST_DURATION).show();
    }    

    /**  
     * @param text
     */
    public void displayToast(CharSequence text) {
        Toast.makeText(this, text, TOAST_DURATION).show();
    }

    public WeiboApplication getWeiboApplication() {
        return mApplication;
    }
}

