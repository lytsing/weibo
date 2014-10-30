
package org.lytsing.android.weibo.core;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Communicates responses from a server or offline requests. One and only one
 * method will be invoked in response to a given request. Callback methods are
 * executed using the RestAdapter callback executor. When none is specified, the
 * following defaults are used: Callbacks are executed on the application's main
 * (UI) thread. See Also:
 * @see RestAdapter.Builder.setExecutors(java.util.concurrent.Executor,java.util.concurrent.Executor)
 *
 * @param <T> expected response type
 */
public abstract class Callback<T> implements retrofit.Callback<T> {
    public final void success(T t, Response response) {
        success(new Result<T>(t, response));
    }

    public final void failure(RetrofitError error) {
        failure(WeiboApiException.convert(error));
    }

    /**
     * Called when call completes successfully.
     *
     * @param result
     */
    public abstract void success(Result<T> result);

    /**
     * Unsuccessful call due to network failure, non-2XX status code, or
     * unexpected exception.
     *
     * @param exception
     */
    public abstract void failure(WeiboException exception);
}

