package com.github.zjzcn.spider.proxyip;

import com.github.zjzcn.util.HttpUserAgent;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProxyIpChecker {

    private static Logger log = LoggerFactory.getLogger(ProxyIpChecker.class);

    private static String reqUrl = "http://www.baidu.com";

    public static Boolean checkProxyIp(String proxyIp, int proxyPort) {
        return checkProxyIp(proxyIp, proxyPort, reqUrl);
    }

    public static Boolean checkNetwork() {
        return checkNetwork(reqUrl);
    }

    /**
     * 网络有效检测
     *
     * @param reqUrl
     */
    public static boolean checkNetwork(String reqUrl) {
        HttpClient client = HttpClients.custom()
                .build();

        return checkUrl(client, reqUrl);
    }

    /**
     * 代理IP有效检测
     *
     * @param reqUrl
     */
    public static boolean checkProxyIp(String proxyIp, Integer proxyPort, String reqUrl) {
        HttpClient client = HttpClients.custom()
                .setProxy(new HttpHost(proxyIp, proxyPort))
                .build();

        return checkUrl(client, reqUrl);
    }

    /**
     * 代理IP有效检测
     *
     * @param client
     * @param reqUrl
     */
    public static boolean checkUrl(HttpClient client, String reqUrl) {
        HttpGet httpGet = new HttpGet(reqUrl);
        httpGet.setConfig(RequestConfig.custom()
                .setSocketTimeout(3000)
                .setConnectTimeout(3000)
                .setConnectionRequestTimeout(3000)
                .build());
        httpGet.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
        httpGet.setHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate");
        httpGet.setHeader("User-Agent", HttpUserAgent.get());

        try {
            HttpResponse response = client.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();

            return statusCode == 200;
        } catch (IOException e) {
            log.warn(e.getMessage());
        } finally {
            if (httpGet != null) {
                httpGet.abort();
            }
        }
        return false;
    }

    public static void main(String[] args) {
        String url = "http://www.baidu.com";

        Map<String, Integer> uncertainMap = new HashMap<>();
        uncertainMap.put("117.90.7.156", 9000);

        for (String proxyIp : uncertainMap.keySet()){
            System.out.println(checkProxyIp(proxyIp, uncertainMap.get(proxyIp), url));
        }

    }
}