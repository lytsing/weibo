package org.lytsing.android.weibo.model;

public class Comment {
    private long id;
    private String author;
    private String createdAt;
    private String text;
    private String source;
    private boolean isTruncated;
    
    public void setId(long id) {
        this.id = id;
    }
    
    public long getId() {
        return this.id;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getAuthor() {
        return this.author;
    }
    
    public void setCreationTime(String time) {
        this.createdAt = time;
    }
    
    public String getCreationTime() {
        return this.createdAt;
    }
    
    public void setBody(String body) {
        this.text = body;
    }
    
    public String getBody() {
        return this.text;
    } 
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getSource() {
        return this.source;
    }
    
    public void setTruncated(boolean truncated) {
        this.isTruncated = truncated;
    }
    
    public boolean hasTruncated() {
        return this.isTruncated;
    }
}
