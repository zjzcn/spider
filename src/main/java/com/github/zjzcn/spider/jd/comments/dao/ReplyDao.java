package com.github.zjzcn.spider.jd.comments.dao;

import com.github.zjzcn.spider.jd.comments.model.Reply;
import com.github.zjzcn.spider.jd.comments.util.DBUtilsHelper;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by tangb on 2017/3/18.
 */
public class ReplyDao extends AbstractDao<Reply>{

    private Logger log = LoggerFactory.getLogger(this.getClass());

    DBUtilsHelper dbh = new DBUtilsHelper();

    public void insert(Reply reply) {
        if (reply == null) {
            log.warn("回复对象为null");
            return;
        }
        String insertSql = "insert into `tbl_jd_reply`(`id`,`type`, `reply_comment`,`reply_time`,`reply_user`,`comment_guid`,`insert_time`, `product_id`,`url`)values(?,?,?,?,?,?,?,?,?)";
        QueryRunner run = dbh.getRunner();
        Object[] params = {UUID.randomUUID().toString(), reply.getType(), reply.getReplyComment(), reply.getReplyTime(), reply.getReplyUser(), reply.getCommentGuid(), reply.getInsertTime(), reply.getProductId(), reply.getUrl()};
        try {
            run.insert(insertSql , new ArrayListHandler(), params);
        } catch (SQLException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

}
