package com.github.zjzcn.spider.jd.comments.model;

import java.util.Date;

/**
 * Created by tangb on 2017/3/18.
 */
public class Product {

    private String id;
    private String productId;
    private String shopName;
    private String skuName;
    private String skuUrl;
    private String skuVersion;
    private Double goodRate;
    private String goodRateTag;
    private Date insertTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSkuUrl() {
        return skuUrl;
    }

    public void setSkuUrl(String skuUrl) {
        this.skuUrl = skuUrl;
    }

    public String getSkuVersion() {
        return skuVersion;
    }

    public void setSkuVersion(String skuVersion) {
        this.skuVersion = skuVersion;
    }

    public Double getGoodRate() {
        return goodRate;
    }

    public void setGoodRate(Double goodRate) {
        this.goodRate = goodRate;
    }

    public String getGoodRateTag() {
        return goodRateTag;
    }

    public void setGoodRateTag(String goodRateTag) {
        this.goodRateTag = goodRateTag;
    }

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }
}
