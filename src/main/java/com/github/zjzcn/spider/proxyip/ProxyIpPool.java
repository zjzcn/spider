package com.github.zjzcn.spider.proxyip;

import com.github.zjzcn.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ProxyIpPool {

    private static final Logger logger = LoggerFactory.getLogger(ProxyIpPool.class);

    private static Set<ProxyIp> livingIpPool = new CopyOnWriteArraySet<>();

    private static ProxyIpPool pool = new ProxyIpPool();

    private ProxyIpDao proxyIpDao = new ProxyIpDao();

    private DelayQueue<ProxyIp> delayQueue = new DelayQueue<>();
    private BlockingQueue<ProxyIp> crawlQueue = new LinkedBlockingQueue<>();

    private final int CRAWL_THREAD_SIZE = 6;
    private final int CLEAN_THREAD_SIZE = 2;

    private Object lock = new Object();

    private ProxyIpPool() {
        // load ip
        loadProxyIpPoolFromDb();

        ExecutorService executor = Executors.newFixedThreadPool(CLEAN_THREAD_SIZE + CRAWL_THREAD_SIZE);
        for (int i=0; i<CRAWL_THREAD_SIZE; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            ProxyIp proxyIp = crawlQueue.take();
                            checkProxyIp(proxyIp);
                        } catch (Exception e) {
                            logger.error("ProxyIpPoolCleaner[crawl] error and continue loop.", e);
                        }
                    }
                }
            });
        }

        for (int i=0; i<CLEAN_THREAD_SIZE; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            ProxyIp proxyIp = delayQueue.take();
                            checkProxyIp(proxyIp);
                        } catch (Exception e) {
                            logger.error("ProxyIpPoolCleaner[delay] error and continue loop.", e);
                        }
                    }
                }
            });
        }
    }

    public static ProxyIpPool getPool() {
        return pool;
    }

    public Set<ProxyIp> getProxyIps() {
        return livingIpPool;
    }

    public void addProxyIps(Collection<ProxyIp> proxyIps) {
        for (ProxyIp proxyIp : proxyIps) {
            crawlQueue.add(proxyIp);
        }
    }

    private void loadProxyIpPoolFromDb() {
        List<ProxyIp> proxyIps = proxyIpDao.queryList();
        for (ProxyIp proxyIp : proxyIps) {
            delayQueue.add(proxyIp);
        }
        logger.info("Load ip pool from db success, proxyIpSize={}", proxyIps.size());
    }


    private void checkProxyIp(ProxyIp proxyIp) {
        Boolean isLiving = ProxyIpChecker.checkProxyIp(proxyIp.getIp(), proxyIp.getPort());
        synchronized (lock) {
            if (isLiving) {
                if (!livingIpPool.contains(proxyIp)) {
                    livingIpPool.add(proxyIp);
                }

                ProxyIp dbProxyIp = proxyIpDao.queryOne(proxyIp.getId());

                String now = DateUtil.format(new Date());
                proxyIp.setLastCheckTime(now);
                if (dbProxyIp == null) {
                    proxyIpDao.insert(proxyIp);
                    logger.info("ProxyIpPoolCleaner add new proxy ip={}.", proxyIp);
                } else {
                    proxyIpDao.update(proxyIp.getId(), now);
                    logger.info("ProxyIpPoolCleaner update proxy ip={}.", proxyIp);
                }

                delayQueue.add(proxyIp);
            } else {
                livingIpPool.remove(proxyIp);
                proxyIpDao.delete(proxyIp.getId());
                logger.info("ProxyIpPoolCleaner delete proxy ip={}.", proxyIp);
            }
        }
    }
    
    public static void main(String[] args){
        int i = 0;
        while(i < 100){
            i ++;
//            System.out.println(getProxyIp());
        }
    }

}
