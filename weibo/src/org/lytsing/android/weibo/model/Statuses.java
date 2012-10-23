
package org.lytsing.android.weibo.model;

import java.io.Serializable;

public class Statuses implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 创建时间 */
    public String created_at;
    
    /** 微博ID */
    public long id;
    
    public String mid;
    
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
    public String  in_reply_to_user_id;
    
    /** 回复人昵称 */
    public String in_reply_to_screen_name;
    
    /** 缩略图 */
    public String thumbnail_pic;
    
    /** 中型图片 */
    public String bmiddle_pic;
    
    /** 原始图片 */
    public String original_pic;
    
    /** 地理信息字段 */
    //public List<Geo> geo;
    public Geo geo;
    
    /** 微博作者的用户信息字段 */
    public User user;
    
    /** 转发的博文，如果不是转发，则没有此字段 */
    public RetweetedStatus retweeted_status;
    
    /** 转发数 */
    public int reposts_count;
    
    /** 评论数 */
    public int comments_count;
    public int melvel;
    public Visible visible;
}
