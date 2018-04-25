package com.github.zjzcn.spider.proxyip;

import com.github.zjzcn.util.DateUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ProxyIpDao {

    private static final Logger logger = LoggerFactory.getLogger(ProxyIpDao.class);

    private static String PROXY_IP_DB_URL = "jdbc:sqlite:" + System.getProperty("user.dir") + "/data/proxyip.db";

    private QueryRunner runner = new QueryRunner();

    public ProxyIpDao() {
        createTable();
    }

    public Connection openConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(PROXY_IP_DB_URL);
        } catch (SQLException e) {
            System.err.println(e);
        }
        return connection;
    }

    public void closeConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
    }

    public void createTable() {
        String sql = "CREATE TABLE `proxy_ip`(`id` varchar(255) PRIMARY KEY,`ip` varchar(255),`port` int(11), `createTime` varchar(255), `lastCheckTime` varchar(255))";
        Connection connection = openConnection();
        try {
            runner.update(connection, sql);
        } catch (SQLException e) {
            if (!e.getMessage().contains("table `proxy_ip` already exists")) {
                logger.error("Create table[proxy_ip] error.", e);
            }
        } finally {
            closeConnection(connection);
        }
    }

    public void insert(ProxyIp proxyIp) {
        String sql = "INSERT INTO `proxy_ip`(`id`,`ip`,`port`,`createTime`, `lastCheckTime`)VALUES(?,?,?,?,?)";
        Object[] params = {proxyIp.getId(), proxyIp.getIp(), proxyIp.getPort(), proxyIp.getCreateTime(), proxyIp.getLastCheckTime()};

        Connection connection = openConnection();
        try {
            runner.insert(connection, sql, new ArrayListHandler(), params);
        } catch (SQLException e) {
            if (!e.getMessage().contains("A PRIMARY KEY constraint failed")) {
                logger.error("Insert proxy_ip data error. id={}", proxyIp.getId(), e);
            }
        } finally {
            closeConnection(connection);
        }
    }

    public void update(String id, String lastCheckTime) {
        String sql = " UPDATE proxy_ip SET lastCheckTime=? WHERE id =?";

        Connection connection = openConnection();
        try {
            runner.update(connection, sql, lastCheckTime, id);
        } catch (SQLException e) {
            logger.error("update proxy_ip table error.", e);
        } finally {
            closeConnection(connection);
        }
    }

    public void delete(String id) {
        String sql = " delete from proxy_ip WHERE id =?";

        Connection connection = openConnection();
        try {
            runner.update(connection, sql, id);
        } catch (SQLException e) {
            logger.error("Delete proxy_ip data error， id={}.", id, e);
        } finally {
            closeConnection(connection);
        }
    }

    public ProxyIp queryOne(String id) {
        String sql = " SELECT * from proxy_ip where id=?";

        Connection connection = openConnection();
        try {
            ProxyIp proxyIp = runner.query(connection, sql, new BeanHandler<>(ProxyIp.class), id);
            return proxyIp;
        } catch (SQLException e) {
            logger.error("Select proxy_ip data error.", e);
        } finally {
            closeConnection(connection);
        }
        return null;
    }

    public List<ProxyIp> queryList() {
        String sql = " SELECT * from proxy_ip";

        Connection connection = openConnection();
        try {
            List<ProxyIp> list = runner.query(connection, sql, new BeanListHandler<>(ProxyIp.class));
            return list;
        } catch (SQLException e) {
            logger.error("Select proxy_ip data error.", e);
        } finally {
            closeConnection(connection);
        }
        return Collections.emptyList();
    }

    public static void main(String[] args) {
        ProxyIpDao proxyIpDao = new ProxyIpDao();
        ProxyIp proxyIp = new ProxyIp();
        proxyIp.setId("12345");
        proxyIpDao.insert(proxyIp);
        proxyIpDao.insert(proxyIp);
        System.out.println(proxyIpDao.queryList());

        System.out.println(proxyIpDao.queryOne("12345"));

        proxyIpDao.update("12345", DateUtil.format(new Date()));
        System.out.println(proxyIpDao.queryOne("12345"));

        proxyIpDao.delete("12345");

        System.out.println(proxyIpDao.queryOne("12345"));

//        Connection connection = null;
//        try {
//            connection = DriverManager.getConnection("jdbc:sqlite:" + PROXY_IP_DB);
//            Statement statement = connection.createStatement();
//            statement.setQueryTimeout(30);
//
//            statement.executeUpdate("drop table if exists person");
//            statement.executeUpdate("create table person (id integer, name string)");
//            statement.executeUpdate("insert into person values(1, 'leo')");
//            statement.executeUpdate("insert into person values(2, 'yui')");
//            ResultSet rs = statement.executeQuery("select * from person");
//            while (rs.next()) {
//                System.out.println("name = " + rs.getString("name"));
//                System.out.println("id = " + rs.getInt("id"));
//            }
//        } catch (SQLException e) {
//            System.err.println(e.getMessage());
//        } finally {
//            try {
//                if (connection != null)
//                    connection.close();
//            } catch (SQLException e) {
//                System.err.println(e);
//            }
//        }
    }
}
