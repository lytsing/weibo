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

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import com.androidquery.util.AQUtility;

public class SettingsActivity extends PreferenceActivity {
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.settings);
    }
    
    protected void onResume() {
        super.onResume();
        
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        configureAboutSection(preferenceScreen);
    }
    
    
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        
        if ("os-licenses".equals(preference.getKey())) {
            startActivity(WebViewDialog.getIntent(this, R.string.os_licenses_label,
                    "file:///android_asset/licenses.html"));
        } else if ("clear-cache".equals(preference.getKey())) {
            image_clear_disk();
        }
        return true;
    }
    
    private void image_clear_disk(){
        AQUtility.cleanCacheAsync(this, 0, 0);
    }
    
    private void configureAboutSection(PreferenceScreen preferenceScreen) {
        Preference buildVersion = preferenceScreen.findPreference("build-version");
        
        String versionName = "";
        PackageManager pm = getPackageManager();

        try {
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            versionName = pi.versionName;
        } catch (NameNotFoundException e) {
            // Log.e("Get Version Code error!", e);
        }
        
        buildVersion.setSummary("版本 : " + versionName);
    }
}

