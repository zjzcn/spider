package com.github.zjzcn.spider.jd.comments.util;

import com.google.common.base.Joiner;

/**
 * Created by tangb on 2017/3/17.
 */
public class UrlUtil {

    /**
     * 获取商品详情页链接URL
     *
     * @param productId 商品ID
     * @return
     */
    public static String getProductUrlByProductId(String productId) {
        return UrlEnum.PRODUCT_ITEM.getUrl() + "/" + productId + ".html";
    }

    public static String getProductReplyUrl(String productId, String guid, int page) {
        return UrlEnum.PRODUCT_REPLY.getUrl() + "/" + productId + "_" + guid + "_" + page + ".html";
    }

    /**
     * 构造评论数据的链接URL
     *
     * @param productId
     * @return
     */
    public static String getProductCommentsUrl(String productId) {
        return getProductCommentsUrl(productId, "0", "0");
    }

    public static String getProductCommentsUrl(String productId, String page) {
        return getProductCommentsUrl(productId, "0", page);
    }

    public static String getProductCommentsUrl(String productId, String score, String page) {
        return getProductCommentsUrl(productId, score, "6", page, "10", "0");
    }

    public static String getProductCommentsUrl(String productId, String score, String sortType, String page, String pageSize, String isShadowSku) {
        return Joiner.on("").join(UrlEnum.PRODUCT_COMMENT.getUrl(),
                "?productId=", productId,
                "&score=", score,
                "&sortType=", sortType,
                "&page=", page,
                "&pageSize=", pageSize,
                "&isShadowSku=", isShadowSku);
    }

}
