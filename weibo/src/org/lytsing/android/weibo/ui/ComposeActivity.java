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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.lytsing.android.weibo.GridViewFaceAdapter;
import org.lytsing.android.weibo.util.AlertUtil;

import android.content.Context;
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
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.MenuItem;
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

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.LocationAjaxCallback;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.weibo.android.R;
import com.weibo.net.AsyncWeiboRunner;
import com.weibo.net.AsyncWeiboRunner.RequestListener;
import com.weibo.net.Utility;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;

public class ComposeActivity extends BaseActivity implements OnClickListener,
        RequestListener {
    
    private ActionBar mActionBar;
    
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
        
        location_ajax();

    }
    
    public void initView() {
        this.setContentView(R.layout.newblog2);
        
        
        aq = new AQuery(this);
        
        mActionBar = (ActionBar) findViewById(R.id.actionbar);
        mActionBar.setHomeAction(new IntentAction(this, TimelineActivity.createIntent(this), R.drawable.ic_title_home_default));
        mActionBar.setDisplayHomeAsUpEnabled(true);
        
        mActionBar.addAction(new SendAction());

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
        
        aq.id(R.id.ly_loadlocation).visible();
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

    public Intent createIntent(Context context) {
        Intent intent = new Intent(context, TimelineActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        return intent;
    }
    
    private class SendAction extends AbstractAction {
        
        public SendAction() {
            super(R.drawable.ic_send);
        }

        @Override
        public void performAction(View view) {
            composeNewPost();
        }
    }
    
    @Override
    public void onComplete(String response) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                displayToast(R.string.send_sucess);
            }
        });

        this.finish();
    }

    @Override
    public void onIOException(IOException e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onError(final WeiboException e) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                displayToast(String.format(
                        ComposeActivity.this.getString(R.string.send_failed)
                                + ":%s", e.getMessage()));
            }
        });
    }
    
    private void composeNewPost() {
        Weibo weibo = Weibo.getInstance();
        try {
            mContent = mEdit.getText().toString();
            if (TextUtils.isEmpty(mContent)) return;
            
            if (!TextUtils.isEmpty(mPicPath)) {
                upload(weibo, Weibo.getAppKey(), this.mPicPath,
                        this.mContent, mLongitude, mLatitude);
            } else {
                // Just update a text weibo!
                update(weibo, Weibo.getAppKey(), mContent, mLongitude, mLatitude);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WeiboException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

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
                public boolean onMenuItemClick(MenuItem item) {
                                    
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
                location_ajax();
            }
            
        }
    }
    
    private String upload(Weibo weibo, String source, String file, String status, String lon,
            String lat) throws WeiboException {
        WeiboParameters bundle = new WeiboParameters();
        bundle.add("source", source);
        bundle.add("pic", file);
        bundle.add("status", status);
        if (!TextUtils.isEmpty(lon)) {
            bundle.add("lon", lon);
        }
        if (!TextUtils.isEmpty(lat)) {
            bundle.add("lat", lat);
        }
        String rlt = "";
        String url = Weibo.SERVER + "statuses/upload.json";
        AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner(weibo);
        weiboRunner.request(this, url, bundle, Utility.HTTPMETHOD_POST, this);

        return rlt;
    }

    private String update(Weibo weibo, String source, String status, String lon, String lat)
            throws MalformedURLException, IOException, WeiboException {
        WeiboParameters bundle = new WeiboParameters();
        bundle.add("source", source);
        bundle.add("status", status);
        if (!TextUtils.isEmpty(lon)) {
            bundle.add("lon", lon);
        }
        if (!TextUtils.isEmpty(lat)) {
            bundle.add("lat", lat);
        }
        String rlt = "";
        String url = Weibo.SERVER + "statuses/update.json";
        AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner(weibo);
        weiboRunner.request(this, url, bundle, Utility.HTTPMETHOD_POST, this);
        return rlt;
    }
    
    private String getNearbyPois(Weibo weibo, String latitude, String longitude)
            throws MalformedURLException, IOException, WeiboException {
        WeiboParameters bundle = new WeiboParameters();
        bundle.add("lat", latitude);
        bundle.add("long", longitude);

        String rlt = "";
        String url = Weibo.SERVER + "place/nearby/pois.json";
        
        rlt = weibo.request(this, url, bundle, Utility.HTTPMETHOD_GET, weibo.getAccessToken());
        
        return rlt;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) { 
            if(null == data){
                Toast.makeText(ComposeActivity.this, "添加图片失败!",
                                Toast.LENGTH_SHORT).show();
                                        
                return;
            }
            Uri uri = data.getData();
            mPicPath = getRealPathFromURI(uri);
            Log.d("pic url == ", mPicPath);
            
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
    
    public void locationCb(String url, Location loc, AjaxStatus status){
        
        if (loc != null) {
            Weibo weibo = Weibo.getInstance();
            Log.d("Location:", loc.toString());
            mLatitude = String.valueOf(loc.getLatitude());
            mLongitude = String.valueOf(loc.getLongitude());
            
            try {
                String ret = getNearbyPois(weibo, mLatitude, mLongitude);
                Log.d("getNearbyPois:", ret);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (WeiboException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            mIsLocation = true;
            
            aq.id(R.id.ly_loadlocation).gone();
            aq.id(R.id.tv_location).visible();
            aq.id(R.id.ib_insert_location).image(R.drawable.btn_insert_location_nor_2);
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
    public void onDestroy(){

        aq.dismiss();

        super.onDestroy();
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
        aq.id(R.id.ib_face_keyboard).image(R.drawable.btn_insert_face)
            .tag(null);
        mGridView.setVisibility(View.GONE);
    }

    private void showOrHideIMM() {

        if (aq.id(R.id.ib_face_keyboard).getTag() == null) {
            // 隐藏软键盘
            ImageView faceOrKeyboard = (ImageView) findViewById(R.id.ib_face_keyboard);
            imm.hideSoftInputFromWindow(faceOrKeyboard.getWindowToken(), 0);
            // 显示表情
            showFace();
        } else {
            // 显示软键盘
            imm.showSoftInput(mEdit, 0);
            // 隐藏表情
            hideFace();
        }
    }
}

