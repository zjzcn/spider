package com.github.zjzcn.spider.proxyip;

import com.github.zjzcn.util.HttpUserAgent;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.Set;

public class ProxyIpProcessor implements PageProcessor {
    private Site site = Site.me()
            .setRetryTimes(2)
            .setSleepTime(3 * 1000)
            .setCharset("UTF-8")
            .setUserAgent(HttpUserAgent.get());

    @Override
    public Site getSite() {
        return site;
    }

    @Override
    public void process(Page page) {
        Set<ProxyIp> proxyIps = ProxyIpSite.parseHtml(page);
        ProxyIpPool.getPool().addProxyIps(proxyIps);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {

        }
    }

}
