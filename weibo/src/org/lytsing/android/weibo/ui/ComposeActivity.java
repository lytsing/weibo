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

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.LocationAjaxCallback;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.net.RequestListener;

import org.lytsing.android.weibo.GridViewFaceAdapter;
import org.lytsing.android.weibo.R;
import org.lytsing.android.weibo.util.AlertUtil;
import org.lytsing.android.weibo.util.Log;
import org.lytsing.android.weibo.util.Util;

import java.io.File;
import java.io.IOException;

public class ComposeActivity extends BaseActivity implements OnClickListener,
        RequestListener {
        
    private EditText mEdit;
    
    private GridView mGridView;
    
    private GridViewFaceAdapter mGVFaceAdapter;
    
    private InputMethodManager imm;
    
    private TextView mTextNum;
    
    private String mPicPath = "";
    private String mContent = "";
    private String mLatitude = "";
    private String mLongitude = "";
    
    private AQuery aq;
    
    private boolean mIsLocation = false;
    
    public static final int WEIBO_MAX_LENGTH = 140;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy); 
        
        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        
        initView();
        
        initGridView();
        
        //location_ajax();
    }
    
    public void initView() {
        this.setContentView(R.layout.newblog2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        aq = new AQuery(this);

        aq.id(R.id.ll_text_limit_unit).clicked(this);
        aq.id(R.id.ib_insert_pic).clicked(this);
        aq.id(R.id.ib_insert_location).clicked(this);
        aq.id(R.id.ib_face_keyboard).clicked(this);
        
        mTextNum = (TextView) findViewById(R.id.tv_text_limit);
        
        mEdit = (EditText) this.findViewById(R.id.et_mblog);
        mEdit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

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
            public void onClick(View v) {
                //显示软键盘
                showIMM();
            }
        });
        
        //aq.id(R.id.ly_loadlocation).visible();
    }
    
    //初始化表情控件
    private void initGridView() {
        mGVFaceAdapter = new GridViewFaceAdapter(this);
        mGridView = (GridView)findViewById(R.id.tweet_pub_faces);
        mGridView.setAdapter(mGVFaceAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //插入的表情
                SpannableString ss = new SpannableString(view.getTag().toString());
                Drawable d = getResources().getDrawable((int)mGVFaceAdapter.getItemId(position));
                d.setBounds(0, 0, 35, 35);//设置表情图片的显示大小
                ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
                ss.setSpan(span, 0, view.getTag().toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);              
                //在光标所在处插入表情
                mEdit.getText().insert(mEdit.getSelectionStart(), ss);              
            }           
        });
    }
    
    @Override
    public void onComplete(String response) {
        Util.showToast(this, R.string.send_success);
        this.finish();
    }

    @Override
    public void onIOException(IOException e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onError(final WeiboException e) {
        String content = String.format(
                ComposeActivity.this.getString(R.string.send_failed) + ":%s", e.getMessage());
        Util.showToast(this, content);
    }
    
    private void composeNewPost() {
        StatusesAPI api = new StatusesAPI(mAccessToken);
        mContent = mEdit.getText().toString();
        if (TextUtils.isEmpty(mContent))
            return;

        if (!TextUtils.isEmpty(mPicPath)) {
            Util.showToast(this, R.string.sending);
            api.upload(this.mContent, this.mPicPath, mLatitude, mLongitude, this);
        } else {
            // Just update a text weibo!
            api.update(mContent, mLatitude, mLongitude, this);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.send, menu);
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
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
            if (TextUtils.isEmpty(mContent)) return;
            
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
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
                public boolean onMenuItemClick(android.view.MenuItem item) {
                                    
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 2);

                    return true;
                }
            });

            popup.show();
        } else if (viewId == R.id.ib_face_keyboard) {
            showOrHideIMM();
        } else if (viewId == R.id.ib_insert_location) {
            if (mIsLocation) {
                aq.id(R.id.tv_location).gone();
                aq.id(R.id.ib_insert_location).image(R.drawable.btn_insert_location_nor);
                mIsLocation = false;
                mLatitude = "";
                mLongitude = "";
            } else {
                aq.id(R.id.ly_loadlocation).visible();
                location_ajax();
            }
        }
    }

    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) { 
            if (null == data) {
                Toast.makeText(ComposeActivity.this, "添加图片失败!", Toast.LENGTH_SHORT).show();

                return;
            }
            Uri uri = data.getData();
            mPicPath = getRealPathFromURI(uri);
            Log.d("pic url == " + mPicPath);
            
            File file = new File(mPicPath);

            // load image from file, down sample to target width of 45 pixels
            aq.id(R.id.iv_insertpic).image(file, 45).visible();
        }
        
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    // And to convert the image URI to the direct file system path of the image file
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private LocationAjaxCallback cb;
    public void location_ajax() {
        
        LocationAjaxCallback cb = new LocationAjaxCallback();
        cb.weakHandler(this, "locationCb").timeout(30 * 1000).accuracy(1000).iteration(3);      
        cb.async(this);
        
        this.cb = cb;
    }
    
    public void locationCb(String url, Location loc, AjaxStatus status) {
        
        if (loc != null) {
            Log.d("Location:" + loc.toString());
            mLatitude = String.valueOf(loc.getLatitude());
            mLongitude = String.valueOf(loc.getLongitude());
            
            //PlaceAPI api = new PlaceAPI(mAccessToken);
            //api.nearbyPois(mLatitude, mLongitude, range, q, category, count, page, offset, listener);
            
            mIsLocation = true;
            
            aq.id(R.id.ly_loadlocation).gone();
            aq.id(R.id.tv_location).visible();
            aq.id(R.id.ib_insert_location).image(R.drawable.btn_insert_location_nor_2);
        } else {
            Log.w("loc is null!");
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (cb != null) {
            cb.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        aq.dismiss();
    }

    private void showIMM() {
        aq.id(R.id.ib_face_keyboard).tag(1);
        showOrHideIMM();
    }

    private void showFace() {
        aq.id(R.id.ib_face_keyboard).image(R.drawable.btn_insert_keyboard).tag(1);
        mGridView.setVisibility(View.VISIBLE);
    }

    private void hideFace() {
        aq.id(R.id.ib_face_keyboard).image(R.drawable.btn_insert_face).tag(null);
        mGridView.setVisibility(View.GONE);
    }

    private void showOrHideIMM() {

        if (aq.id(R.id.ib_face_keyboard).getTag() == null) {
            ImageView faceOrKeyboard = (ImageView) findViewById(R.id.ib_face_keyboard);
            imm.hideSoftInputFromWindow(faceOrKeyboard.getWindowToken(), 0);
            showFace();
        } else {
            imm.showSoftInput(mEdit, 0);
            hideFace();
        }
    }
}

