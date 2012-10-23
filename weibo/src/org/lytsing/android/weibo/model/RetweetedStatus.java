package org.lytsing.android.weibo.model;

import java.io.Serializable;
import java.util.List;

public class RetweetedStatus implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3731983092212626968L;

	/** 创建时间 */
	public String created_at;

	/** 创建时间 */
	public long id;

	/** 字符串型的微博ID */
	public String idstr;

	/** 微博信息内容 */
	public String text;

	/** 微博来源 */
	public String source;

	/** 是否已收藏 */
	public boolean favorited;

	/** 是否被截断 */
	public boolean truncated;

	/** 回复ID */
	public String in_reply_to_status_id;

	/** 回复人UID */
	public String in_reply_to_user_id;

	/** 回复人昵称 */
	public String in_reply_to_screen_name;

    /** 缩略图 */
    public String thumbnail_pic;
    
    /** 中型图片 */
    public String bmiddle_pic;
    
    /** 原始图片 */
    public String original_pic;

	public List<Geo> geos;

	public String mid;

	/** 转发数 */
	public int reposts_count;

	/** 评论数 */
	public int comments_count;

	public int melvel;

	public Visible visible;

	public User user;
}

