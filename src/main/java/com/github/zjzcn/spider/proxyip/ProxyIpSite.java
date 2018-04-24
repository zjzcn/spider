package com.github.zjzcn.spider.proxyip;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Html;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProxyIpSite {

    public enum Url{
        XICIDAILI("http://www.xicidaili.com/nn/", 2000, false),
        KUAIDAILI("https://www.kuaidaili.com/free/inha/", 22000, true),
        URL_66IP("http://www.66ip.cn/nmtq.php?getnum=512&isp=0&anonymoustype=0&start=&ports=&export=&ipaddress=&area=0&proxytype=2&api=66ip", 1, true),
        IP3366("http://www.ip3366.net/free/", 50, true),
        PROXY360("http://www.proxy360.cn/Region/China", 20, true),
        MIMIIP("http://www.mimiip.com/", 20, true),
        DATA5U("http://www.data5u.com/free/index.shtml", 20,true),
        IP181("http://www.ip181.com/", 20, true),
        KXDAILI("http://www.kxdaili.com/", 20, true);

        private String siteUrl;
        private int pageCount;
        private boolean isUsable;
        Url(String siteUrl, int pageCount, boolean isUsable) {
            this.siteUrl = siteUrl;
            this.pageCount = pageCount;
            this.isUsable = isUsable;
        }
        public java.lang.String getSiteUrl() {
            return siteUrl;
        }
        public int getPageCount() {
            return pageCount;
        }

        public boolean isUsable() {
            return isUsable;
        }
    }

    public static List<String> getSiteUrls() {
        List<String> urls = new ArrayList<>();
        for(int i = 1 ; i <= Url.XICIDAILI.getPageCount()&& Url.XICIDAILI.isUsable(); i ++){
            urls.add(Url.XICIDAILI.getSiteUrl() + i);
        }

        for(int i = 1 ; i <= Url.KUAIDAILI.getPageCount()&& Url.KUAIDAILI.isUsable(); i ++){
            urls.add(Url.KUAIDAILI.getSiteUrl() + i);
        }

        return urls;
    }

    public static Set<ProxyIp> parseHtml(Page page) {
        Html html = page.getHtml();
        List<String> ips = new ArrayList<>();
        List<String> ports = new ArrayList<>();

        String url = page.getUrl().toString();
        if (url.startsWith(Url.XICIDAILI.getSiteUrl())) {
            ips = html.xpath("//table[@id='ip_list']/tbody/tr/td[2]/text()").all();
            ports = html.xpath("//table[@id='ip_list']/tbody/tr/td[3]/text()").all();
        } else if(url.startsWith(Url.KUAIDAILI.getSiteUrl())) {
            ips = html.xpath("//*[@id=\"list\"]/table/tbody/tr/td[1]/text()").all();
            ports = html.xpath("//*[@id=\"list\"]/table/tbody/tr/td[2]/text()").all();
        }
        Set<ProxyIp> proxyIps = new HashSet<>();
        for(int i = 0; i < ips.size(); i++) {
            proxyIps.add(new ProxyIp(ips.get(i), Integer.valueOf(ports.get(i))));
        }
        return proxyIps;
    }
}
