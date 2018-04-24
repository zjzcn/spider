package com.github.zjzcn.spider.proxyip;

import com.github.zjzcn.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ProxyIpPool {

    private static final Logger logger = LoggerFactory.getLogger(ProxyIpPool.class);

    private static Random random = new Random();

    private static List<ProxyIp> livingIpPool = new CopyOnWriteArrayList<>();

    private static ProxyIpPool pool = new ProxyIpPool();

    private ProxyIpDao proxyIpDao = new ProxyIpDao();

    private BlockingQueue<ProxyIp> queue = new LinkedBlockingQueue<>();

    private final int CLEANER_THREAD_SIZE = 10;

    private Object lock = new Object();

    private ProxyIpPool() {
        // load ip
        loadProxyIpPoolFromDb();

        ExecutorService executor = Executors.newFixedThreadPool(CLEANER_THREAD_SIZE);
        for (int i=0; i<CLEANER_THREAD_SIZE; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            ProxyIp proxyIp = queue.take();
                            Boolean isLiving = ProxyIpChecker.checkProxyIp(proxyIp.getIp(), proxyIp.getPort());

                            synchronized (lock) {
                                if (isLiving) {
                                    if (!livingIpPool.contains(proxyIp)) {
                                        livingIpPool.add(proxyIp);
                                    }

                                    ProxyIp dbProxyIp = proxyIpDao.queryOne(proxyIp.getId());
                                    if (dbProxyIp == null) {
                                        proxyIp.setCreateTime(DateUtil.format(new Date()));
                                        proxyIp.setLastCheckTime(DateUtil.format(new Date()));
                                        proxyIpDao.insert(proxyIp);
                                    } else {
                                        proxyIpDao.update(proxyIp.getId(), DateUtil.format(new Date()));
                                    }
                                } else {
                                    livingIpPool.remove(proxyIp);

                                    proxyIpDao.delete(proxyIp.toString());
                                }
                            }
                        } catch (Exception e) {
                            logger.error("ProxyIpPoolCleaner error and continue loop.", e);
                        }
                    }
                }
            });
        }
    }

    public static ProxyIpPool getPool() {
        return pool;
    }

    public ProxyIp getProxyIp() {
        ProxyIp proxyIp = null;
        do {
            if(livingIpPool.isEmpty()){
                return null;
            }

            int i = random.nextInt(livingIpPool.size());
            try {
                proxyIp = livingIpPool.get(i);
            }catch (IndexOutOfBoundsException e){
            }
        } while (proxyIp == null);
        return proxyIp;
    }

    public void addProxyIps(Collection<ProxyIp> proxyIps) {
        for (ProxyIp proxyIp : proxyIps) {
            queue.add(proxyIp);
        }
    }

    private void loadProxyIpPoolFromDb() {
        List<ProxyIp> proxyIps = proxyIpDao.queryList();

        addProxyIps(proxyIps);

        logger.info("Load ip pool success, proxyIpSize={}", proxyIps.size());
    }

    public static void main(String[] args){
        int i = 0;
        while(i < 100){
            i ++;
//            System.out.println(getProxyIp());
        }
    }

}
