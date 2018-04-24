package com.github.zjzcn.spider.proxyip;

import us.codecraft.webmagic.Spider;

import java.util.List;

public class ProxyIpSpider {

    public static void main(String[] args){
        Spider spider = Spider.create(new ProxyIpProcessor());

        List<String> urls = ProxyIpSite.getSiteUrls();
        spider.addUrl(urls.toArray(new String[0]));
        spider.thread(1).run();
    }
}
