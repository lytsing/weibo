
package org.lytsing.android.weibo.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class WeiboObject {
    public List<Statuses> statuses;
    
    @SerializedName("hasvisible")
    public boolean hasvisible;
     
    @SerializedName("previous_cursor")
    public long previous_cursor;
     
    @SerializedName("next_cursor")
    public long next_cursor;
     
    @SerializedName("total_number")
    public int total_number;
}

