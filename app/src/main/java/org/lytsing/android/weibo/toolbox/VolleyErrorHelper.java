/*
 * Copyright (C) 2013 http://lytsing.org
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
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import android.content.Context;

import org.lytsing.android.weibo.R;

/**
 * simple helper to display appropriate message when one of these exceptions occurs.
 * @author Liqing Huang
 * @see <a href="http://arnab.ch/blog/2013/08/asynchronous-http-requests-in-android-using-volley/">asynchronous-http-requests-in-android-using-volley</a>
 *
 */
public class VolleyErrorHelper {
    /**
     * Returns appropriate message which is to be displayed to the user
     * against the specified error object.
     *
     * @param error
     * @param context
     * @return
     */
    public static String getMessage(Object error, Context context) {
        if (error instanceof TimeoutError) {
            return context.getResources().getString(R.string.general_server_down);
        } else if (isServerProblem(error)) {
            return handleServerError(error, context);
        } else if (isNetworkProblem(error)) {
            return context.getResources().getString(R.string.networking_error);
        }

        return context.getResources().getString(R.string.general_error);
    }

    /**
     * Determines whether the error is related to network
     * @param error
     * @return
     */
    private static boolean isNetworkProblem(Object error) {
        return (error instanceof NetworkError) || (error instanceof NoConnectionError);
    }

    /**
     * Determines whether the error is related to server
     * @param error
     * @return
     */
    private static boolean isServerProblem(Object error) {
        return (error instanceof ServerError) || (error instanceof AuthFailureError);
    }

    /**
     * Handles the server error, tries to determine whether to show a stock message or to
     * show a message retrieved from the server.
     *
     * @param err
     * @param context
     * @return
     */
    private static String handleServerError(Object err, Context context) {
        VolleyError error = (VolleyError) err;

        NetworkResponse response = error.networkResponse;

        if (response != null) {
            switch (response.statusCode) {
                case 304:
                    return context.getResources().getString(R.string.http_error_304);
                case 400:
                    return context.getResources().getString(R.string.http_error_400);
                case 401:
                    return context.getResources().getString(R.string.http_error_401);
                case 402:
                    return context.getResources().getString(R.string.http_error_402);
                case 403:
                    return context.getResources().getString(R.string.http_error_403);
                case 404:
                    return context.getResources().getString(R.string.http_error_404);
                case 500:
                    return context.getResources().getString(R.string.http_error_500);
                case 502:
                    return context.getResources().getString(R.string.http_error_502);
                case 503:
                    return context.getResources().getString(R.string.http_error_503);
                default:
                    return context.getResources().getString(R.string.general_server_down);
            }
        }

        return context.getResources().getString(R.string.general_error);
    }
}

