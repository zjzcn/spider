package com.github.zjzcn.spider.jd.comments.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by tangb on 2017/3/17.
 */
public class DoneProduct {

    private static Logger log = LoggerFactory.getLogger(DoneProduct.class);

    private static Set<String> done = new CopyOnWriteArraySet<String>();

    /**
     * 商品是否已经抓取
     * @param productId 商品ID
     * @return
     */
    public static Boolean isDone(String productId){
        return done.contains(productId);
    }

    public static void add(String productId){
        if(!isDone(productId)){
            done.add(productId);
        }else{
            log.warn("商品 【" + productId + "】已经抓取，请勿重复抓取");
        }
    }

    public static void remove(String productId){
        done.remove(productId);
    }

}
