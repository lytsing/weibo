package org.lytsing.android.weibo.core.models;

import com.google.gson.annotations.SerializedName;

public class SearchMetadata
{

  @SerializedName("max_id")
  public final long maxId;

  @SerializedName("since_id")
  public final long sinceId;

  @SerializedName("refresh_url")
  public final String refreshUrl;

  @SerializedName("next_results")
  public final String nextResults;

  @SerializedName("count")
  public final long count;

  @SerializedName("completed_in")
  public final double completedIn;

  @SerializedName("since_id_str")
  public final String sinceIdStr;

  @SerializedName("query")
  public final String query;

  @SerializedName("max_id_str")
  public final String maxIdStr;

  public SearchMetadata(int maxId, int sinceId, String refreshUrl, String nextResults, int count, double completedIn, String sinceIdStr, String query, String maxIdStr)
  {
    this.maxId = maxId;
    this.sinceId = sinceId;
    this.refreshUrl = refreshUrl;
    this.nextResults = nextResults;
    this.count = count;
    this.completedIn = completedIn;
    this.sinceIdStr = sinceIdStr;
    this.query = query;
    this.maxIdStr = maxIdStr;
  }
}