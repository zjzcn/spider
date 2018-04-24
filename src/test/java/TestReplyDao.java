import com.github.zjzcn.spider.jd.comments.dao.ReplyDao;
import com.github.zjzcn.spider.jd.comments.model.Reply;

import java.util.Date;

/**
 * Created by tangb on 2017/3/18.
 */
public class TestReplyDao {

    public static void main(String[] args){
        Reply reply = new Reply();
        reply.setCommentGuid("01128838-b1e2-4ce3-a655-753887193477");
        reply.setReplyComment("尊敬的顾客，您的好评是对我们最大的鼓励，也是我们前进的动力！这款电视搭载语音人工智能系统，一键直达院线大片，人性化的配置让电视更加的方便，使用的时候将更加轻松，如需要帮助，联系客服或是咨询4008-123456，TCL电视竭诚为您服务，祝您新年愉快~");
        reply.setInsertTime(new Date());
        reply.setProductId("4261888");
        reply.setReplyTime(new Date());

        ReplyDao replyDao = new ReplyDao();
        replyDao.insert(reply);
    }

}
