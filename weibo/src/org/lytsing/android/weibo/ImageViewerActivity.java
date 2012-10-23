package org.lytsing.android.weibo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.androidquery.AQuery;

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
            .image(mMiddleImageUrl, false, false);

        aq.id(R.id.toLargeImage).clicked(this, "buttonClicked");

    }

    public void buttonClicked(View button) {
        Intent intent = new Intent(this, ImageZoomActivity.class);
        intent.putExtra(Consts.ORIGINAL_PIC_URL_KEY, mOriginalPicUrl);
        startActivity(intent);
        finish();
    }
}
