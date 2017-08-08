package org.lytsing.android.weibo.core.services;

import org.lytsing.android.weibo.core.models.Geo;
import org.lytsing.android.weibo.core.models.Search;

import retrofit.Callback;
import retrofit.http.EncodedQuery;
import retrofit.http.GET;
import retrofit.http.Query;


@SuppressWarnings("deprecation")
public interface SearchService {
    @GET("/1.1/search/tweets.json")
    void tweets(
            @Query("q") String query,
            @EncodedQuery("geocode") Geo geocode,
            @Query("lang") String lang,
            @Query("locale") String locale,
            @Query("result_type") String resultType,
            @Query("count") Integer count,
            @Query("until") String until,
            @Query("since_id") Long sinceId,
            @Query("max_id") Long maxId,
            @Query("include_entities") Boolean includeEntities,
            Callback<Search> cb);
}
