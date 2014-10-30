
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

public class WeiboApiClient {
    final ConcurrentHashMap<Class, Object> services;

    final RestAdapter adapter;

    WeiboApiClient(SSLSocketFactory sslSocketFactory, ExecutorService executorService) {

        this.services = new ConcurrentHashMap();

        Gson gson = new GsonBuilder().create();
        this.adapter = new RestAdapter.Builder().build();
    }

    public FavoriteService getFavoriteService() {
        return (FavoriteService) getService(FavoriteService.class);
    }

    public StatusesService getStatusesService() {
        return (StatusesService) getService(StatusesService.class);
    }

    public SearchService getSearchService() {
        return (SearchService) getService(SearchService.class);
    }

    protected <T> T getService(Class<T> cls) {
        if (!this.services.contains(cls)) {
            this.services.putIfAbsent(cls, this.adapter.create(cls));
        }
        return (T) this.services.get(cls);
    }
}

