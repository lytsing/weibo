
package org.lytsing.android.weibo.core;

import retrofit.client.Response;

/**
 *
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

