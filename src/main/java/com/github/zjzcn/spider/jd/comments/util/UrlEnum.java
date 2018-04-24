package com.github.zjzcn.spider.jd.comments.util;

/**
 * Created by tangb on 2017/3/17.
 */
public enum UrlEnum {
    PRODUCT_LIST("商品列表页URL", "https://list.jd.com/list.html"),
    PRODUCT_ITEM("商品详情页URL", "https://item.jd.com"),
    PRODUCT_COMMENT("商品评论URL", "https://club.jd.com/comment/productPageComments.action"),
    PRODUCT_REPLY("商品评论回复URL", "https://club.jd.com/repay");

    private String name;
    private String url;

    UrlEnum(String name, String url) {
        this.name = name;
        this.url = url;
    }

    // get set 方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
