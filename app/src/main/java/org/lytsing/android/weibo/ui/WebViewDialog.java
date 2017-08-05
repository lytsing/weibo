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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.webkit.WebView;

import org.lytsing.android.weibo.R;

public class WebViewDialog extends FragmentActivity {

    public static Intent getIntent(Context context, int title, String url) {
        return new Intent(context, WebViewDialog.class)
                .putExtra(Intent.EXTRA_TITLE, title)
                .putExtra(Intent.EXTRA_STREAM, url);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getIntent().getIntExtra(Intent.EXTRA_TITLE, R.string.app_name));

        setContentView(R.layout.activity_licenses);

        WebView webView = (WebView) findViewById(R.id.content);
        webView.loadUrl(getIntent().getStringExtra(Intent.EXTRA_STREAM));
    }
}

