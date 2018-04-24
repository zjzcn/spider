package com.github.zjzcn.spider.jd.comments.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.Properties;

public class DbPoolConnection {

	private static Logger log = LoggerFactory.getLogger(DbPoolConnection.class);

	private static DbPoolConnection databasePool = null;
    private static DruidDataSource dds = null;
	static {
		Properties properties = loadPropertyFile("conf.properties");
		try {
			dds = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private DbPoolConnection() {
	}

	public static synchronized DbPoolConnection getInstance() {
		if (null == databasePool) {
			databasePool = new DbPoolConnection();
		}
		return databasePool;
	}

	public DruidDataSource getDataSource() throws SQLException {
		return dds;
	}

	public DruidPooledConnection getConnection() throws SQLException {
		return dds.getConnection();
	}

	public static Properties loadPropertyFile(String configFile) {
		String webRootPath = null;
		if (null == configFile || configFile.equals(""))
			throw new IllegalArgumentException("Properties file path can not be null : " + configFile);
		ClassLoader classLoader = DbPoolConnection.class.getClassLoader();
		URL resource = classLoader.getResource(configFile);
		String path = resource.getPath();
		log.info("加载配置文件..." + path);
		File file = new File(path);
		InputStream inputStream = null;
		Properties p = null;
		try {
			inputStream = new FileInputStream(file);
			p = new Properties();
			p.load(inputStream);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Properties file not found: "
					+ configFile);
		} catch (IOException e) {
			throw new IllegalArgumentException(
					"Properties file can not be loading: " + configFile);
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return p;
	}

}