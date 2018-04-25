package com.github.zjzcn.spider.proxyip;

import com.github.zjzcn.util.DateUtil;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class ProxyIp implements Delayed {

    private static final long DELAY_MS = 10 * 60 * 1000; // 10minus

    private static final String SPLIT = ":";

    private String id;
    private String ip;
    private Integer port;
    private String createTime;
    private String lastCheckTime;

    public ProxyIp() {

    }

    public ProxyIp(String id) {
        this.id = id;
        String[] split = id.split(SPLIT);
        this.ip = split[0];
        this.port = Integer.valueOf(split[1]);
        this.createTime = DateUtil.format(new Date());
        this.setLastCheckTime(this.getCreateTime());
    }

    public ProxyIp(String ip, Integer port) {
        this(ip + SPLIT + port);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastCheckTime() {
        return lastCheckTime;
    }

    public void setLastCheckTime(String lastCheckTime) {
        this.lastCheckTime = lastCheckTime;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object that) {
        if (that instanceof ProxyIp) {
            ProxyIp o = (ProxyIp) that;
            return (ip.equals(o.ip)) && (port.equals(o.port));
        }
        return false;
    }

    @Override
    public int hashCode() {
        long value = 17;
        value += 37 * value + ip.hashCode();
        value += 37 * value + port.hashCode();
        return (int) (value ^ (value >>> 32));
    }

    @Override
    public long getDelay(TimeUnit unit) {
        Date checkTime = DateUtil.parse(lastCheckTime);
        long delayMs = checkTime.getTime() + DELAY_MS - System.currentTimeMillis();
        return unit.convert(delayMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if (o == this) {
            return 0;
        }
        long d = (getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS));
        return (d == 0) ? 0 : ((d < 0) ? -1 : 1);
    }
}
