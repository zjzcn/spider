package com.github.zjzcn.spider.jd.comments.util;

import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;
import java.sql.SQLException;
  
public class DBUtilsHelper {  
    private DataSource ds = null;  
    private QueryRunner runner = null;  
  
    public DBUtilsHelper() {  
        try {  
            this.ds = DbPoolConnection.getInstance().getDataSource();  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
        if (this.ds != null) {  
            this.runner = new QueryRunner(this.ds);  
        }  
    }  
  
    public DBUtilsHelper(DataSource ds) {
        this.ds = ds;
        this.runner = new QueryRunner(this.ds);  
    }  
  
    public QueryRunner getRunner() {  
        return this.runner;  
    }  
}  