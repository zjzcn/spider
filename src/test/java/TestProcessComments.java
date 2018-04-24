import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tangb on 2017/3/17.
 */
public class TestProcessComments {
    
    public static void main(String[] args) throws IOException {
        String path = "C:\\Users\\tangb\\Desktop";
        File file = new File(path, "test.txt");
        List<String> contents = Files.readLines(file, Charsets.UTF_8);
        for (String line: contents) {
            System.out.println(line);
            getComment(line);
        }
    }

    //评论解析
    public static void getComment(String josnObj) {
        //评论概要json
        JSONObject commentSummaryJson = JSONObject.parseObject(josnObj).getJSONObject("productCommentSummary");
        //好评度
        int goodRate = commentSummaryJson.getInteger("goodRateShow");
        //好评标签
        Map<String, Integer> commentTagMap = new HashMap<String, Integer>();

        JSONArray commentTagsJsonArray = JSONObject.parseObject(josnObj).getJSONArray("hotCommentTagStatistics");
        for (int i = 0; i < commentTagsJsonArray.size(); i++) {
            JSONObject commentTagJson = commentTagsJsonArray.getJSONObject(i);
            String key = commentTagJson.getString("name");
            Integer value = commentTagJson.getInteger("count");
            commentTagMap.put(key, value);
        }

        //评价json串
        JSONArray commentsJsonArray = JSONObject.parseObject(josnObj).getJSONArray("comments");
        System.out.println(commentsJsonArray.size());
        if (null != commentsJsonArray && commentsJsonArray.size() > 0) {
            for (int i = 0; i < commentsJsonArray.size(); i++) {
                JSONObject commentJsonObject = commentsJsonArray.getJSONObject(i);
                //用户名称
                String nickName = commentJsonObject.getString("nickname");
                //用户等级
                String userLevelName = commentJsonObject.getString("userLevelName");
                //评论创建时间
                String creationTime = commentJsonObject.getString("creationTime");
                //点赞数
                int usefulVoteCount = commentJsonObject.getInteger("usefulVoteCount");
                //回复数
                int replyCount = commentJsonObject.getInteger("replyCount");
                //回复id guid
                String guid = commentJsonObject.getString("guid");
                //评价级别
                int score = commentJsonObject.getInteger("score");

                //Json串comments对象中的每一个 showOrderComment 对象
                JSONObject commentJson = commentsJsonArray.getJSONObject(i);
                //评价内容
                String commentContent = commentJson.getString("content");
                //用户客户端
                String userClient = commentJson.getString("userClientShow");

                String content = Joiner.on("\t").join("解析内容：", nickName, userLevelName, creationTime, usefulVoteCount, replyCount,
                        guid, score, commentContent, userClient);
                System.out.println(content);
            }
        }
    }
}
