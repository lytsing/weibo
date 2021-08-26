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

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.*;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.core.app.NavUtils;
import androidx.loader.content.CursorLoader;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.LocationAjaxCallback;
import com.orhanobut.logger.Logger;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import org.lytsing.android.weibo.R;
import org.lytsing.android.weibo.adapters.GridViewFaceAdapter;
import org.lytsing.android.weibo.util.AlertUtil;
import org.lytsing.android.weibo.util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Liqing Huang
 */
public class ComposeActivity extends BaseActivity implements OnClickListener,
        RequestListener {

    private EditText mEdit;

    private GridView mGridView;

    private GridViewFaceAdapter mGVFaceAdapter;

    private InputMethodManager imm;

    private TextView mTextNum;

    private String mPicPath = "";
    private Uri mImageUri = null;
    private String mContent = "";
    private String mLatitude = "";
    private String mLongitude = "";

    private AQuery mAq;

    private boolean mIsLocation = false;

    public static final int WEIBO_MAX_LENGTH = 140;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        initView();

        initGridView();

        //locationAjax();
    }

    public void initView() {
        this.setContentView(R.layout.newblog2);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mAq = new AQuery(this);

        mAq.id(R.id.ll_text_limit_unit).clicked(this);
        mAq.id(R.id.ib_insert_pic).clicked(this);
        mAq.id(R.id.ib_insert_location).clicked(this);
        mAq.id(R.id.ib_face_keyboard).clicked(this);

        mTextNum = (TextView) findViewById(R.id.tv_text_limit);

        mEdit = (EditText) this.findViewById(R.id.et_mblog);
        mEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String mText = mEdit.getText().toString();
                int len = mText.length();
                if (len <= WEIBO_MAX_LENGTH) {
                    len = WEIBO_MAX_LENGTH - len;
                    mTextNum.setTextColor(getResources().getColor(R.color.text_num_gray));

                } else {
                    len = len - WEIBO_MAX_LENGTH;
                    mTextNum.setTextColor(Color.RED);
                }

                mTextNum.setText(String.valueOf(len));
            }
        });

        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 显示软键盘
                showIMM();
            }
        });

        //mAq.id(R.id.ly_loadlocation).visible();
    }

    // 初始化表情控件
    private void initGridView() {
        mGVFaceAdapter = new GridViewFaceAdapter(this);
        mGridView = (GridView) findViewById(R.id.tweet_pub_faces);
        mGridView.setAdapter(mGVFaceAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 插入的表情
                SpannableString ss = new SpannableString(view.getTag().toString());
                Drawable d = getResources().getDrawable((int) mGVFaceAdapter.getItemId(position));
                d.setBounds(0, 0, 35, 35); // 设置表情图片的显示大小
                ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
                ss.setSpan(span, 0, view.getTag().toString().length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                // 在光标所在处插入表情
                mEdit.getText().insert(mEdit.getSelectionStart(), ss);
            }
        });
    }

    @Override
    public void onComplete(String response) {
        Utils.showToast(this, R.string.send_success);
        this.finish();
    }

    @Override
    public void onWeiboException(final WeiboException e) {
        String content = String.format(
                ComposeActivity.this.getString(R.string.send_failed) + ":%s", e.getMessage());
        Utils.showToast(this, content);
    }

    private void composeNewPost() {
        StatusesAPI api = new StatusesAPI(mAccessToken);
        mContent = mEdit.getText().toString();
        if (TextUtils.isEmpty(mContent)) {
            return;
        }

        if (!TextUtils.isEmpty(mPicPath)) {
            Utils.showToast(this, R.string.sending);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
            } catch (FileNotFoundException e) {
                Logger.e(e.getMessage());
            } catch (IOException e) {
                Logger.e(e.getMessage());
            }

            api.upload(this.mContent, bitmap, mLatitude, mLongitude, this);
        } else {
            // Just update a text weibo!
            api.update(mContent, mLatitude, mLongitude, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.send, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send:
                composeNewPost();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return;
        }

        if (viewId == R.id.ll_text_limit_unit) {
            mContent = mEdit.getText().toString();
            if (TextUtils.isEmpty(mContent)) {
                return;
            }

            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mEdit.setText("");
                }
            };

            AlertUtil.showAlert(this, R.string.attention, R.string.delete_all,
                    getString(R.string.ok), listener,
                    getString(R.string.cancel), null);
        } else if (viewId == R.id.ib_insert_pic) {
            PopupMenu popup = new PopupMenu(this, view);
            popup.getMenuInflater().inflate(R.menu.pic, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(android.view.MenuItem item) {

                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(galleryIntent, 2);

                    return true;
                }
            });

            popup.show();
        } else if (viewId == R.id.ib_face_keyboard) {
            showOrHideIMM();
        } else if (viewId == R.id.ib_insert_location) {
            if (mIsLocation) {
                mAq.id(R.id.tv_location).gone();
                mAq.id(R.id.ib_insert_location).image(R.drawable.btn_insert_location_nor);
                mIsLocation = false;
                mLatitude = "";
                mLongitude = "";
            } else {
                mAq.id(R.id.ly_loadlocation).visible();
                locationAjax();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (null == data) {
                displayToast("添加图片失败!");
                return;
            }

            mImageUri = data.getData();
            mPicPath = getRealPathFromURI(mImageUri);
            if (mPicPath != null) {
                File file = new File(mPicPath);
                // load image from file, down sample to target width of 45 pixels
                mAq.id(R.id.iv_insertpic).image(file, 45).visible();
            } else {
                displayToast("添加图片失败!");
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // And to convert the image URI to the direct file system path of the image file
    // TODO: bugs on Android 4.4, it return null when Choose Open from Rencent/Images/Downlaods.
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }

    private LocationAjaxCallback mCallback;
    public void locationAjax() {

        LocationAjaxCallback cb = new LocationAjaxCallback();
        cb.weakHandler(this, "locationCb").timeout(30 * 1000).accuracy(1000).iteration(3);
        cb.async(this);

        this.mCallback = cb;
    }

    public void locationCb(String url, Location loc, AjaxStatus status) {

        if (loc != null) {
            Logger.d("Location:" + loc.toString());
            mLatitude = String.valueOf(loc.getLatitude());
            mLongitude = String.valueOf(loc.getLongitude());

            mIsLocation = true;

            mAq.id(R.id.ly_loadlocation).gone();
            mAq.id(R.id.tv_location).visible();
            mAq.id(R.id.ib_insert_location).image(R.drawable.btn_insert_location_nor_2);
        } else {
            Logger.w("loc is null!");
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mCallback != null) {
            mCallback.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mAq.dismiss();
    }

    private void showIMM() {
        mAq.id(R.id.ib_face_keyboard).tag(1);
        showOrHideIMM();
    }

    private void showFace() {
        mAq.id(R.id.ib_face_keyboard).image(R.drawable.btn_insert_keyboard).tag(1);
        mGridView.setVisibility(View.VISIBLE);
    }

    private void hideFace() {
        mAq.id(R.id.ib_face_keyboard).image(R.drawable.btn_insert_face).tag(null);
        mGridView.setVisibility(View.GONE);
    }

    private void showOrHideIMM() {

        if (mAq.id(R.id.ib_face_keyboard).getTag() == null) {
            ImageView faceOrKeyboard = (ImageView) findViewById(R.id.ib_face_keyboard);
            imm.hideSoftInputFromWindow(faceOrKeyboard.getWindowToken(), 0);
            showFace();
        } else {
            imm.showSoftInput(mEdit, 0);
            hideFace();
        }
    }
}

