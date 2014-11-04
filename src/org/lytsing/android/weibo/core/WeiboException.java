
package org.lytsing.android.weibo.core;

public class WeiboException extends RuntimeException {
    public WeiboException(String detailMessage) {
        super(detailMessage);
    }

    public WeiboException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
