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

import retrofit.client.Response;

/**
 *
 * @author Liqing Huang
 * @param <T> - Parsed type, available in the data
 */
public class Result<T> {
    public final T data;

    public final Response response;

    public Result(T data, Response response) {
        this.data = data;
        this.response = response;
    }
}

