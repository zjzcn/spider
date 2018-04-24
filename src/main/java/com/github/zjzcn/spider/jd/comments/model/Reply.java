package com.github.zjzcn.spider.jd.comments.model;

import java.util.Date;

/**
 * Created by tangb on 2017/3/18.
 */
public class Reply {

    private String id;
    private Integer type;
    private String replyUser;
    private String replyComment;
    private Date replyTime;
    private String productId;
    private String commentGuid;
    private Date insertTime;
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getReplyUser() {
        return replyUser;
    }

    public void setReplyUser(String replyUser) {
        this.replyUser = replyUser;
    }

    public String getReplyComment() {
        return replyComment;
    }

    public void setReplyComment(String replyComment) {
        this.replyComment = replyComment;
    }

    public Date getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(Date replyTime) {
        this.replyTime = replyTime;
    }

    public String getCommentGuid() {
        return commentGuid;
    }

    public void setCommentGuid(String commentGuid) {
        this.commentGuid = commentGuid;
    }

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
