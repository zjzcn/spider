package com.github.zjzcn.spider.jd.comments.pipeline;

import com.github.zjzcn.spider.jd.comments.dao.CommentDao;
import com.github.zjzcn.spider.jd.comments.dao.ProductDao;
import com.github.zjzcn.spider.jd.comments.dao.ReplyDao;
import com.github.zjzcn.spider.jd.comments.model.Comment;
import com.github.zjzcn.spider.jd.comments.model.Product;
import com.github.zjzcn.spider.jd.comments.model.Reply;
import com.github.zjzcn.spider.jd.comments.util.UrlEnum;
import org.apache.commons.beanutils.BeanUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by tangb on 2017/3/18.
 */
public class JdbcPipeline implements Pipeline {

    private ProductDao productDao = new ProductDao();

    private CommentDao commentDao = new CommentDao();

    private ReplyDao replyDao = new ReplyDao();

    public void process(ResultItems resultItems, Task task) {
        String url = resultItems.getRequest().getUrl();
        Map<String, Object> map = resultItems.getAll();
        // 商品数据入库
        if(url.startsWith(UrlEnum.PRODUCT_ITEM.getUrl())){
            Product p = new Product();
            try {
                BeanUtils.copyProperties(p, map);
                p.setInsertTime(new Date());
                productDao.insert(p);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        // 评论数据入口
        else if(url.startsWith(UrlEnum.PRODUCT_COMMENT.getUrl())){

            String productId = String.valueOf(map.get("productId"));
            Double goodRate = Double.valueOf(String.valueOf(map.get("goodRate")));
            String goodRateTag = String.valueOf(map.get("goodRateTag"));
            // 根据评论数据返回的好评标签更新商品的好评率和好评标签
            productDao.updateGoodRate(productId, goodRate, goodRateTag);
            // 存储评论数据
            List<Comment> cs = (List<Comment>) map.get("commentList");
            if(cs != null && !cs.isEmpty()){
                for (Comment comment : cs){
                    comment.setInsertTime(new Date());
                    comment.setUrl(url);
                    commentDao.insert(comment);
                }
            }
        }
        // 回复数据入口
        else if(url.startsWith(UrlEnum.PRODUCT_REPLY.getUrl())){
            List<Reply> replys = (List<Reply>)map.get("replyContents");
            if(!replys.isEmpty()){
                for (Reply reply : replys){
                    reply.setInsertTime(new Date());
                    reply.setUrl(url);
                    replyDao.insert(reply);
                }
            }
        }
    }

}
