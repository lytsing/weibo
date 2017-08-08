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

package org.lytsing.android.weibo.core;

import com.android.volley.VolleyLog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.lytsing.android.weibo.core.models.ApiError;

import java.io.UnsupportedEncodingException;
import retrofit.RetrofitError;
import retrofit.mime.TypedByteArray;

public class WeiboApiException extends WeiboException {
    private static final String ERROR_CODE = "code";

    private static final int DEFAULT_ERROR_CODE = 0;

    private final RetrofitError mRetrofitError;

    private final int mErrorCode;

    WeiboApiException(int mErrorCode, RetrofitError retrofitError) {
        super(retrofitError.getMessage());
        this.mRetrofitError = retrofitError;
        this.mErrorCode = mErrorCode;
    }

    WeiboApiException(RetrofitError retrofitError) {
        super(createExceptionMessage(retrofitError));
        setStackTrace(retrofitError.getStackTrace());

        this.mRetrofitError = retrofitError;
        this.mErrorCode = readErrorCode(retrofitError);
    }

    private static String createExceptionMessage(RetrofitError retrofitError) {
        if (retrofitError.getMessage() != null) {
            return retrofitError.getMessage();
        }
        if (retrofitError.getResponse() != null) {
            return "Status: " + retrofitError.getResponse().getStatus();
        }
        return "unknown error";
    }

    public int getmErrorCode() {
        return this.mErrorCode;
    }

    public boolean canRetry() {
        int status = this.mRetrofitError.getResponse().getStatus();
        return (status < 400) || (status > 499);
    }

    public RetrofitError getmRetrofitError() {
        return this.mRetrofitError;
    }

    public static final WeiboApiException convert(RetrofitError retrofitError) {
        return new WeiboApiException(retrofitError);
    }

    public static int readErrorCode(RetrofitError retrofitError) {
        if ((retrofitError == null) || (retrofitError.getResponse() == null)
                || (retrofitError.getResponse().getBody() == null)) {
            return 0;
        }
        byte[] responseBytes = ((TypedByteArray) retrofitError.getResponse().getBody()).getBytes();

        if (responseBytes == null)
            return 0;
        try {
            String response = new String(responseBytes, "UTF-8");
            return parseErrorCode(response);
        } catch (UnsupportedEncodingException e) {
            VolleyLog.e("Twitter", "Failed to convert to string", e);
        }
        return 0;
    }

    static int parseErrorCode(String response) {
        Gson gson = new Gson();
        try {
            JsonObject responseObj = new JsonParser().parse(response).getAsJsonObject();
            ApiError[] apiErrors = gson.fromJson(responseObj.get("errors"),
                    ApiError[].class);

            if (apiErrors.length == 0) {
                return 0;
            }

            return apiErrors[0].getCode();
        } catch (JsonSyntaxException e) {
            VolleyLog.e("Twitter", "Invalid json: " + response, e);
        }
        return 0;
    }
}
