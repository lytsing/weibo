/*
 * Copyright 2013 Ognyan Bankov
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

package org.lytsing.android.weibo.toolbox;

import com.android.volley.DefaultRetryPolicy;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.params.CoreProtocolPNames;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * GsonRequest is a way to solve the Volley's JSONObjectReuqest can't post paramsters like the StringRequest
 * @see http://stackoverflow.com/questions/16626032/volley-post-get-parameters
 * 
 * @author Lytsing Huang
 *
 * Changlog:
 * 1. Add retrying failed requests and customizing request Timeout
 *  see http://arnab.ch/blog/2013/08/asynchronous-http-requests-in-android-using-volley/
 * 
 * @param <T>
 */

public class GsonRequest<T> extends Request<T> {
    private final Gson mGson;
    private final Class<T> mClazz;
    private final Listener<T> mListener;
    private Map<String, String> mParams;

    public GsonRequest(int method,
                       String url,
                       Map<String, String> params,
                       Class<T> clazz,
                       Listener<T> listener,
                       ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mClazz = clazz;
        this.mListener = listener;
        this.mParams = params;
        mGson = new Gson();
    }

    public GsonRequest(int method,
                       String url,
                       Map<String, String> params,
                       Class<T> clazz,
                       Listener<T> listener,
                       ErrorListener errorListener,
                       Gson gson) {
        super(method, url, errorListener);
        this.mClazz = clazz;
        this.mListener = listener;
        this.mParams = params;
        mGson = new Gson();
    }


    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }


    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            VolleyLog.d("Response: %s", json);

            return Response.success(mGson.fromJson(json, mClazz),
                                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
    
    protected Map<String, String> getParams()
            throws com.android.volley.AuthFailureError {
        return mParams;
    };
    
    /**
     * Setting Request Headers (HTTP headers) for Apache/Nginx Proxy Server monitor log
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put(CoreProtocolPNames.USER_AGENT, System.getProperty("http.agent"));
        return headers;
    }
}

