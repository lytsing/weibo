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

package org.lytsing.android.weibo.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.weibo.sdk.android.WeiboException;

public class Util {
	
	private static Map<String, SimpleDateFormat> formatMap = new HashMap<String, SimpleDateFormat>();

	public static Date parseDate(String str) throws WeiboException {
		String format = "EEE MMM dd HH:mm:ss z yyyy";
		if (str == null || "".equals(str)) {
			return null;
		}
		SimpleDateFormat sdf = formatMap.get(format);
		if (null == sdf) {
			sdf = new SimpleDateFormat(format, Locale.ENGLISH);
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			formatMap.put(format, sdf);
		}

		synchronized (sdf) {
			// SimpleDateFormat is not thread safe
			try {
				return sdf.parse(str);
			} catch (ParseException e) {
				throw new WeiboException("Unexpected format(" + str
						+ ") returned from sina.com.cn");
			}
		}

	}
	
	public static String getNowLocaleTime() {
		Date date = new Date();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}
	
    /** 
     * needLoadMore
     * @param scrollPosition
     * @param loadedCount
     * @param totalCount
     * @return
     */
    public static boolean needLoadMore(long scrollPosition, long loadedCount, long totalCount) {
        if (scrollPosition > 0) {
            if (scrollPosition + 3 >= loadedCount && loadedCount < totalCount) {
                return true;
            }   
        }   

        return false;
    }
	
    /**
     * Inflate a new view hierarchy from the specified XML resource.
     * 
     * @param resource ID for an XML layout resource to load (e.g.,
     *        <code>R.layout.asset_info_section_header</code>)
     * @param context The current context.
     * @return The root View of the inflated XML file.
     */
    public static View inflateView(int resource, Context context) {

        return inflateView(resource, context, null);
    }

    /**
     * Inflate a new view hierarchy from the specified xml resource.
     * 
     * @param resourceID for an XML layout resource to load (e.g.,
     * @param context The current context.
     * @param parent simply an object that provides a set of LayoutParams
     *        values for root of the returned hierarchy
     * @return The root View of the inflated XML file.
     */
    public static View inflateView(int resource, Context context, ViewGroup parent) {
        LayoutInflater vi = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        return  vi.inflate(resource, parent, false);
    }

    public static void textHighlight2(TextView textView, String start, String end) {
        Spannable sp = (Spannable) textView.getText();
        String text = textView.getText().toString();
        int n = 0;
        int s = -1;
        int e = -1;
        while (n < text.length()) {
            s = text.indexOf(start, n);
            if (s != -1) {
                e = text.indexOf(end, s + start.length());
                if (e != -1) {
                    e = e + end.length();
                } else {
                    e = text.length();
                }
                n = e;
                // ics android blue color
                // see http://developer.android.com/design/style/color.html
                sp.setSpan(new ForegroundColorSpan(Color.rgb(0x33, 0xB5, 0xE5)), s, e,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                s = e = -1;
            } else {
                n = text.length();
            }
        }
    }
    
    public static void textHighlight(TextView textView, String start, String end) {
        Spannable sp = (Spannable) textView.getText();
        String text = textView.getText().toString();
        int n = 0;
        int s = -1;
        int e = -1;
        while (n < text.length()) {
            s = text.indexOf(start, n);
            if (s != -1) {
                e = text.indexOf(end, s + start.length());
                if (e != -1) {
                    e = e + end.length();
                } else {
                    e = text.length();
                }
                n = e;
                // ics android blue color
                // see http://developer.android.com/design/style/color.html
                sp.setSpan(new ForegroundColorSpan(Color.rgb(0x33, 0xB5, 0xE5)), s, e,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                s = e = -1;
            } else {
                n = text.length();
            }
        }
    }
    
    static private String getUpdateInfoFilename() {
        return Environment.getExternalStorageDirectory().toString() + "/weibo.json";
    }   
    
    static public boolean writeUpdateInfo(String msg) { 
        String fileName = getUpdateInfoFilename();
        File f = new File(fileName);
        try {
            if (f.exists()) {
                f.delete();
            }   
    
            f.createNewFile();
            FileOutputStream stream = new FileOutputStream(f);
            stream.write(msg.getBytes()); 
            stream.flush(); 
            stream.close();

            return true;
        } catch (IOException ex) {
            Log.e("writeUpdateInfo()" + ex);
            return false;
        }   
    }
}

