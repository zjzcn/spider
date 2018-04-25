package com.github.zjzcn.spider.dangdang;

import com.github.zjzcn.spider.jd.comments.processor.JDCommentsCrawl;
import com.github.zjzcn.spider.proxyip.ProxyIp;
import com.github.zjzcn.spider.proxyip.ProxyIpPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.ProxyProvider;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by tangb on 2017/3/17.
 */
public class DangDangSpider {

    private static final Logger logger = LoggerFactory.getLogger(DangDangSpider.class);

    public static void main(String[] args) {
        List<String> urls = new ArrayList<>();
        urls.add("http://product.m.dangdang.com/25242647.html?t=1524284830");

        List<Pipeline> pipelines = new ArrayList<Pipeline>();
        pipelines.add(new ConsolePipeline());

        Spider spider = Spider.create(new JDCommentsCrawl()).setPipelines(pipelines);
        HttpClientDownloader downloader = new HttpClientDownloader();
        spider.setDownloader(downloader);

        Set<ProxyIp> proxyIps = new HashSet<>();
        proxyIps.add(new ProxyIp("117.90.7.156", 9000));
        addProxyToSpider(downloader, proxyIps);

        for (String url : urls) {
            spider.addUrl(url);
        }
        spider.thread(10).run();
    }

    private static void addProxyToSpider(HttpClientDownloader downloader, Set<ProxyIp> proxyIps) {
        logger.info("Add to proxy ip pool, ip={}.", proxyIps.toString());
        Proxy[] proxies = new Proxy[proxyIps.size()];
        int i = 0;
        for (ProxyIp proxyIp : proxyIps) {
            proxies[i] = new Proxy(proxyIp.getIp(), proxyIp.getPort());
            i++;
        }
        ProxyProvider proxyProvider = SimpleProxyProvider.from(proxies);
        downloader.setProxyProvider(proxyProvider);
    }

    private static void updateProxyToSpider(HttpClientDownloader downloader) {
        Thread updateProxyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10 * 1000);
                    } catch (InterruptedException e) {
                    }

                    Set<ProxyIp> proxyIps = ProxyIpPool.getPool().getProxyIps();
                    addProxyToSpider(downloader, proxyIps);
                }
            }
        });
        updateProxyThread.start();
    }

}
