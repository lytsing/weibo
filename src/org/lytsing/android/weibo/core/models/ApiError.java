
package org.lytsing.android.weibo.core.models;

import com.google.gson.annotations.SerializedName;

public class ApiError {

    @SerializedName("message")
    private final String message;

    @SerializedName("code")
    private final int code;

    public ApiError(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public int getCode() {
        return this.code;
    }
}
