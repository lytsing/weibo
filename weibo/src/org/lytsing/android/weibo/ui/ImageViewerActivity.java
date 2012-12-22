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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.androidquery.AQuery;

import org.lytsing.android.weibo.Consts;
import org.lytsing.android.weibo.R;

public class ImageViewerActivity extends Activity {

    private AQuery aq;

    private String mMiddleImageUrl = null;

    private String mOriginalPicUrl = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.imageviewactivity);

        aq = new AQuery(this);

        mMiddleImageUrl = getIntent().getStringExtra(Consts.MIDDLE_IMAGE_URL_KEY);
        mOriginalPicUrl = getIntent().getStringExtra(Consts.ORIGINAL_PIC_URL_KEY);

        aq.id(R.id.ivImageDia).progress(R.id.progress)
            .image(mMiddleImageUrl, false, false, 0, 0, null, AQuery.FADE_IN_NETWORK, 0);

        aq.id(R.id.toLargeImage).clicked(this, "buttonClicked");

    }

    public void buttonClicked(View button) {
        Intent intent = new Intent(this, ImageZoomActivity.class);
        intent.putExtra(Consts.ORIGINAL_PIC_URL_KEY, mOriginalPicUrl);
        startActivity(intent);
        finish();
    }
}
