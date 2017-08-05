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

import android.app.Activity;
import android.os.Bundle;

import com.androidquery.AQuery;

import org.lytsing.android.weibo.R;

public class ImageZoomActivity extends Activity {

    private AQuery aq;

    private String mOriginalImageUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.image_zoom_activity);

        mOriginalImageUrl = getIntent().getStringExtra("original_pic_url");

        aq = new AQuery(this);

        image_zoom();
    }

    private void image_zoom() {
        aq.id(R.id.text).text("Try pinch zoom with finger.");
        if (mOriginalImageUrl != null) {
            aq.id(R.id.web).progress(R.id.progress).webImage(mOriginalImageUrl);
            // aq.id(R.id.web).progress(R.id.progress).webImage(url, true, true,
            // 0xFF000000);
        }
    }
}
