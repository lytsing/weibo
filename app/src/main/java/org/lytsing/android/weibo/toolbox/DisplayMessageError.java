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

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

public class DisplayMessageError extends VolleyError {
    private String mDisplayErrorHtml;

    public DisplayMessageError(NetworkResponse networkResponse) {
        super(networkResponse);
    }

    public DisplayMessageError(String displayErrorHtml) {
        mDisplayErrorHtml = displayErrorHtml;
    }

    public String getDisplayErrorHtml() {
        return mDisplayErrorHtml;
    }

    public String toString() {
        return new StringBuilder().append("DisplayErrorMessage[")
            .append(mDisplayErrorHtml)
            .append("]").toString();
    }
}

