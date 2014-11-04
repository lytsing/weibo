
package org.lytsing.android.weibo.core.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Search {

    @SerializedName("statuses")
    public final List<Statuses> tweets;

    @SerializedName("search_metadata")
    public final SearchMetadata searchMetadata;

    public Search(List<Statuses> tweets, SearchMetadata searchMetadata) {
        this.tweets = tweets;
        this.searchMetadata = searchMetadata;
    }
}
