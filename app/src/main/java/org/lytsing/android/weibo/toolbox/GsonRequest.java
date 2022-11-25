/*
 * Copyright (C) 2013 http://lytsing.org
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * GsonRequest is a way to solve the Volley's JSONObjectReuqest can't post paramsters like the
 * StringRequest
 * @see {http://stackoverflow.com/questions/16626032/volley-post-get-parameters}
 *
 * @author Lytsing Huang
 *
 * Changlog:
 * 1. Add retrying failed requests and customizing request Timeout
 *  see http://arnab.ch/blog/2013/08/asynchronous-http-requests-in-android-using-volley/
 * 2. 2014/10/22 support http gzip, see https://github.com/darja/VolleyDemo/
 *
 * @param <T> JSON type of response expected
 */
public class GsonRequest<T> extends Request<T> {
    private final Gson mGson;
    private final Class<T> mClazz;
    private final Listener<T> mListener;
    private Map<String, String> mParams;

    private static final String HEADER_ENCODING = "Content-Encoding";
    private static final String HEADER_USER_AGENT = "User-Agent";
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";

    private boolean mGzipEnabled = true;

    private static final int SET_SOCKET_TIMEOUT = 20 * 1000;

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

        // Set a retry policy in case of SocketTimeout & ConnectionTimeout Exceptions.
        // Volley does retry for you if you have specified the policy.
        setRetryPolicy(new DefaultRetryPolicy(SET_SOCKET_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String responseString = null;
            String charset = HttpHeaderParser.parseCharset(response.headers);

            if (mGzipEnabled && isGzipped(response)) {
                try {
                    byte[] data = decompressResponse(response.data);
                    responseString = new String(data, charset);
                } catch (IOException e) {
                    // it seems that result is not GZIP
                }
            }

            if (responseString == null) {
                responseString = new String(response.data, charset);
            }

            return Response.success(mGson.fromJson(responseString, mClazz),
                                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    private boolean isGzipped(NetworkResponse response) {
        Map<String, String> headers = response.headers;
        return headers != null && !headers.isEmpty() && headers.containsKey(HEADER_ENCODING) &&
            headers.get(HEADER_ENCODING).equalsIgnoreCase(ENCODING_GZIP);
    }

    protected byte[] decompressResponse(byte [] compressed) throws IOException {
        ByteArrayOutputStream baos = null;
        try {
            int size;
            ByteArrayInputStream memstream = new ByteArrayInputStream(compressed);
            GZIPInputStream gzip = new GZIPInputStream(memstream);
            final int buffSize = 8192;
            byte[] tempBuffer = new byte[buffSize];
            baos = new ByteArrayOutputStream();
            while ((size = gzip.read(tempBuffer, 0, buffSize)) != -1) {
                baos.write(tempBuffer, 0, size);
            }
            return baos.toByteArray();
        } finally {
            if (baos != null) {
                baos.close();
            }
        }
    }

    @Override
    protected Map<String, String> getParams()
            throws AuthFailureError {
        return mParams;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>(2);
        // add user agent header which Android System support
        headers.put(HEADER_USER_AGENT, System.getProperty("http.agent"));
        // add gzip header
        headers.put(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
        return headers;
    }
}

