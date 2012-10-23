/*
 * Copyright (C) 2010 lytsing.org
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

/**
 * Log
 * Wrapper android.util.Log API for sending log output.
 */
public class Log {
    protected static final boolean DEBUG = true;
    protected static final String TAG = "Weibo";

    /**
     * Send a VERBOSE log message.
     * @param msg The message you would like logged.
     */
    public static void v(String msg) {
        if (DEBUG) {
            android.util.Log.v(TAG, buildMessage(msg));
        }
    }

    /**
     * Send a VERBOSE log message and log the exception.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static void v(String msg, Throwable tr) {
        if (DEBUG) {
            android.util.Log.v(TAG, buildMessage(msg), tr);
        }
    }

    /**
     * Send a DEBUG log message.
     * @param msg
     */
    public static void d(String msg) {
        if (DEBUG) {
            android.util.Log.d(TAG, buildMessage(msg));
        }
    }

    /**
     * Send a DEBUG log message and log the exception.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static void d(String msg, Throwable tr) {
        if (DEBUG) {
            android.util.Log.d(TAG, buildMessage(msg), tr);
        }
    }

    /**
     * Send an INFO log message.
     * @param msg The message you would like logged.
     */
    public static void i(String msg) {
        if (DEBUG) {
            android.util.Log.i(TAG, buildMessage(msg));
        }
    }

    /**
     * Send a INFO log message and log the exception.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static void i(String msg, Throwable tr) {
        if (DEBUG) {
            android.util.Log.i(TAG, buildMessage(msg), tr);
        }
    }

    /**
     * Send an ERROR log message.
     * @param msg The message you would like logged.
     */
    public static void e(String msg) {
        if (DEBUG) {
            android.util.Log.e(TAG, buildMessage(msg));
        }
    }

    /**
     * Send a WARN log message
     * @param msg The message you would like logged.
     */
    public static void w(String msg) {
        if (DEBUG) {
            android.util.Log.w(TAG, buildMessage(msg));
        }
    }

    /**
     * Send a WARN log message and log the exception.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static void w(String msg, Throwable tr) {
        if (DEBUG) {
            android.util.Log.w(TAG, buildMessage(msg), tr);
        }
    }

    /**
     * Send an empty WARN log message and log the exception.
     * @param tr An exception to log
     */
    public static void w(Throwable tr) {
        if (DEBUG) {
            android.util.Log.w(TAG, buildMessage(""), tr);
        }
    }
    
    /**
     * Send an ERROR log message and log the exception.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static void e(String msg, Throwable tr) {
        if (DEBUG) {
            android.util.Log.e(TAG, buildMessage(msg), tr);
        }
    }

    /**
     * Building Message
     * @param msg The message you would like logged.
     * @return Message String
     */
    protected static String buildMessage(String msg) {      
        StackTraceElement ste = new Throwable().fillInStackTrace().getStackTrace()[2];

         return new StringBuilder()
                .append(ste.getClassName())
                .append(".")
                .append(ste.getMethodName())
                .append("(): ")
                .append(msg).toString();
    }
}

