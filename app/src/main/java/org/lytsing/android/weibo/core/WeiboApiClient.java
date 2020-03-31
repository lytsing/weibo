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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.lytsing.android.weibo.core.services.FavoriteService;
import org.lytsing.android.weibo.core.services.SearchService;
import org.lytsing.android.weibo.core.services.StatusesService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import javax.net.ssl.SSLSocketFactory;
import retrofit.RestAdapter;

/**
 * Weibo Api Client.
 * @author Liqing Huang
 */
public class WeiboApiClient {
    final ConcurrentHashMap<Class, Object> services;

    final RestAdapter adapter;

    WeiboApiClient(SSLSocketFactory sslSocketFactory, ExecutorService executorService) {

        this.services = new ConcurrentHashMap();

        Gson gson = new GsonBuilder().create();
        this.adapter = new RestAdapter.Builder().build();
    }

    public FavoriteService getFavoriteService() {
        return getService(FavoriteService.class);
    }

    public StatusesService getStatusesService() {
        return getService(StatusesService.class);
    }

    public SearchService getSearchService() {
        return getService(SearchService.class);
    }

    protected <T> T getService(Class<T> cls) {
        if (!this.services.contains(cls)) {
            this.services.putIfAbsent(cls, this.adapter.create(cls));
        }
        return (T) this.services.get(cls);
    }
}

