package com.github.zjzcn.spider.jd.comments.processor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.zjzcn.spider.jd.comments.model.Comment;
import com.github.zjzcn.spider.jd.comments.model.Reply;
import com.github.zjzcn.spider.jd.comments.util.DoneProduct;
import com.github.zjzcn.spider.jd.comments.util.UrlEnum;
import com.github.zjzcn.spider.jd.comments.util.UrlUtil;
import com.github.zjzcn.util.HttpUserAgent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Json;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDCommentsCrawl implements PageProcessor {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private Site site = null;
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd hh:mm");

    public Site getSite() {
        // 设置请求参数
        site = Site.me()
                .setRetryTimes(5)
                .setSleepTime(1000)
                .setUserAgent(HttpUserAgent.get());

//        // 加载代理IP文件
//        String basepath = System.getProperty("user.dir");
//        String resultpath = basepath + "/data/webmagic/proxyip.txt";
//
//        List<String[]> proxyPool = new ArrayList<String[]>();
//
//        // site.setHttpProxyPool(proxyPool, true);
        return site;
    }

    public void process(Page page) {
        String url = page.getUrl().toString();
        if(null == page.getHtml()){
            try {
                log.warn("#################  html为空，线程休眠10分钟");
                Thread.sleep(10 * 60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // if 页面是商品列表页，商品列表页处理
        if(url.startsWith(UrlEnum.PRODUCT_LIST.getUrl())){
            processProductList(page);
        }
        // if 页面是商品详情页，商品详情页处理
        else if(url.startsWith(UrlEnum.PRODUCT_ITEM.getUrl())){
            processProductItem(page);
        }
        // if 页面是评论返回json数据，评论数据处理
        else if(url.startsWith(UrlEnum.PRODUCT_COMMENT.getUrl())){
            processProductComments(page);
        }
        // if 页面是回复返回json数据，回复数据处理
        else if(url.startsWith(UrlEnum.PRODUCT_REPLY.getUrl())){
            processProductReply(page);
        }
    }


    /**
     * 处理商品列表页面
     * @param page
     */
    private void  processProductList(Page page){
        Html html = page.getHtml();
        List<String> items = html.xpath("//*[@id='plist']/ul/li[@class='gl-item']//div[@class='p-img']/a/@href").all();
        if (!items.isEmpty()) {
            for (String producrUrl : items) {
                String productId = getProductId(producrUrl);
                if (!DoneProduct.isDone(productId)) {
                    // 种子生成：从商品列表页中提取商品详情页的连接数据，放入爬虫
                    DoneProduct.add(productId);
                    page.addTargetRequest(new Request(producrUrl));
                }
            }
        }
    }

    /**
     * 如果页面是商品详情页，解析出详情页的相关数据
     * @param page
     */
    private void processProductItem(Page page){
        Html html = page.getHtml();
        String url = page.getUrl().toString();
        page.putField("skuUrl", url);

        String productId = html.xpath("//*[@id='preview']/div[@class='preview-info']/div[@class='left-btns']/a[1]/@data-id").toString();
        page.putField("productId", productId);

        String shopName = html.xpath("//div[@id='crumb-wrap']/div[@class='w']/div[@class='contact']/div/div[@class='item']/div[@class='name']/a/text()").toString();
        if(shopName == null){
           shopName = html.xpath("//div[@id='crumb-wrap']/div[@class='w']/div[@class='contact']/div/div[@class='item']/div[@class='name']/em/text()").toString();
        }
        page.putField("shopName", shopName);

        String skuName = html.xpath("//div[@class='itemInfo-wrap']/div[@class='sku-name']/text()").toString();
        page.putField("skuName", skuName);

        String selectedSku = html.xpath("//*[@id=\"choose-attr-2\"]/div[@class='dd']/div[@class='selected']/a/text()").toString();
        page.putField("skuVersion", selectedSku);

        List<String> dataSkus = html.xpath("//*[@id=\"choose-attr-2\"]/div[@class='dd']/div/@data-sku").all();
        // 种子生成：该版本的商品如果没有爬取，放入爬虫进行爬取
        for (String dataSku : dataSkus) {
            if (!DoneProduct.isDone(dataSku)) {
                String dataSkuUrl = UrlUtil.getProductUrlByProductId(dataSku);
                page.addTargetRequest(dataSkuUrl);
            }
        }

        // 种子生成：提取该详情页的评论URL，放入爬虫进行爬取
        String commentsUrl = UrlUtil.getProductCommentsUrl(productId);
        page.addTargetRequest(commentsUrl);
    }

    /**
     * 如果页面是商品评论json数据
     * @param page
     */
    private void processProductComments(Page page) {
        Json json = page.getJson();
        if(null == json){
            try {
                log.warn("#################  json为空，线程休眠10分钟");
                Thread.sleep(10 * 60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // TODO: 现阶段一次对全部的数据进行请求，后续需要修改为增量请求评论数据
        // 种子生成：获取第一次评论后得到maxPage，然后拼接出所有的评论连接，放入爬虫进行爬取
        String jsonStr = json.toString();
        JSONObject commentJsonObj = null;
        Integer maxPage = 1;
        try {
            commentJsonObj = JSONObject.parseObject(jsonStr);
            maxPage = commentJsonObj.getInteger("maxPage");
        }catch (Exception e){
            try {
                Thread.sleep(10 * 60 * 1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            return;
        }
        JSONObject productCommentSummary = commentJsonObj.getJSONObject("productCommentSummary");
        String productId = productCommentSummary.getString("skuId");
        page.putField("productId", productId);

        Double goodRate = productCommentSummary.getDouble("goodRateShow");
        page.putField("goodRate", goodRate);

        JSONArray commentTagsJsonArray = commentJsonObj.getJSONArray("hotCommentTagStatistics");
        if (null != commentTagsJsonArray && commentTagsJsonArray.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < commentTagsJsonArray.size(); i++) {
                JSONObject commentTagJson = commentTagsJsonArray.getJSONObject(i);
                String key = commentTagJson.getString("name");
                Integer value = commentTagJson.getInteger("count");
                sb.append(key).append("(").append(value).append(") ");
            }
            page.putField("goodRateTag", sb);
        }

        if(maxPage == null || (maxPage - 0 == 0)){
            return;
        }else{
            String url = page.getUrl().toString();
            if (url.contains("page=0")){
                int num = 500;
                if(maxPage <= num){
                    num = maxPage;
                }
                for(int pageNum = 1; pageNum <= num; pageNum++){
                    page.addTargetRequest(UrlUtil.getProductCommentsUrl(productId, String.valueOf(pageNum)));
                }
            }
        }
        JSONArray comments = commentJsonObj.getJSONArray("comments");
        for(int i = 0; i < comments.size(); i++){
            JSONObject comment = comments.getJSONObject(i);
            String guid = comment.getString("guid");
            Integer replyCount = comment.getInteger("replyCount");
            String referenceId = comment.getString("referenceId");
            if(replyCount > 0){
                int pageNum = replyCount / 15 + 1;
                for (int j = 1; j <= pageNum; j++){
                    page.addTargetRequest(UrlUtil.getProductReplyUrl(referenceId, guid, j));
                }
            }
        }

        // TODO: 解析函数放在此处
        List<Comment> cs = processComment(jsonStr);
        page.putField("commentList", cs);
    }

    /**
     * 处理回复页面
     * @param page
     */
    private void processProductReply(Page page){
        Html html = page.getHtml();

        String guid = html.xpath("//div[@class='dt-content']/input[@id='id1']/@cid").toString();
        page.putField("guid", guid);

        String productId = html.xpath("//div[@id='pinfo']/input[@id='productId']/@value").toString();
        page.putField("productId", productId);

        List<String> replyItems = html.xpath("//div[@class='reply-items']/div[@class='item']").all();

        List<Reply> replyContents = new ArrayList<Reply>();
        for (String replyItem : replyItems){
            Reply reply = new Reply();

            Document replyDoc = Jsoup.parse(replyItem);
            Elements tt = replyDoc.select("div.tt");
            String info = tt.text();

            Elements timeDoc = replyDoc.select("span.time");
            String replyTime = timeDoc.text();

            String replyUser = info.substring(0, info.indexOf(":"));
            String replyContent = info.substring(info.indexOf(":") + 1);

            reply.setProductId(productId);
            reply.setCommentGuid(guid);
            reply.setType(1);
            reply.setReplyComment(replyContent);
            reply.setReplyUser(replyUser);
            reply.setReplyTime(parse(replyTime));
            replyContents.add(reply);
        }
        page.putField("replyContents", replyContents);
    }

    //评论解析
    public List<Comment> processComment(String josnObj) {
        List<Comment> comments = new ArrayList<Comment>();
        //评论概要json
        JSONObject commentSummaryJson = JSONObject.parseObject(josnObj).getJSONObject("productCommentSummary");
        if (null != commentSummaryJson && commentSummaryJson.size() > 0) {
            //好评度
            int goodRate = commentSummaryJson.getInteger("goodRateShow");
            //好评标签
            Map<String, Integer> goodRateTagMap = new HashMap<String, Integer>();
            JSONArray commentTagsJsonArray = JSONObject.parseObject(josnObj).getJSONArray("hotCommentTagStatistics");
            if (null != commentTagsJsonArray && commentTagsJsonArray.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < commentTagsJsonArray.size(); i++) {
                    JSONObject commentTagJson = commentTagsJsonArray.getJSONObject(i);
                    String key = commentTagJson.getString("name");
                    Integer value = commentTagJson.getInteger("count");
                    sb.append(key).append("(").append(value).append(") ");
                    goodRateTagMap.put(key, value);
                }
            }

            //评价json串
            JSONArray commentsJsonArray = JSONObject.parseObject(josnObj).getJSONArray("comments");
            if (null != commentsJsonArray && commentsJsonArray.size() > 0) {
                for (int i = 0; i < commentsJsonArray.size(); i++) {
                    Comment comment = new Comment();

                    JSONObject commentJsonObject = commentsJsonArray.getJSONObject(i);
                    //用户名称
                    String nickame = commentJsonObject.getString("nickname");
                    comment.setNickName(nickame);
                    //用户等级
                    String userLevelName = commentJsonObject.getString("userLevelName");
                    comment.setUserLevel(userLevelName);
                    //评论创建时间
                    String creationTime = commentJsonObject.getString("creationTime");
                    comment.setCreationTime(parse(creationTime));
                    //点赞数
                    int usefulVoteCount = commentJsonObject.getInteger("usefulVoteCount");
                    comment.setUsefulVoteCount(usefulVoteCount);
                    //官方回复
                    List<String> replyConentLists = new ArrayList<String>();
                    JSONArray repliesArray = commentJsonObject.getJSONArray("replies");
                    if (null != repliesArray && repliesArray.size() > 0) {
                        for (int k = 0; k < repliesArray.size(); k++) {
                            JSONObject repliesObject = repliesArray.getJSONObject(k);
                            String replyConent = repliesObject.getString("content");
                            replyConentLists.add(replyConent);
                            System.out.println("官方回复:" + replyConent);
                        }
                    }
                    //回复数
                    int replyCount = commentJsonObject.getInteger("replyCount");
                    comment.setReplyCount(replyCount);
                    //回复id guid
                    String guid = commentJsonObject.getString("guid");
                    comment.setGuid(guid);

                    String productId = commentJsonObject.getString("referenceId");
                    comment.setProductId(productId);
                    //评价级别
                    int score = commentJsonObject.getInteger("score");
                    comment.setScore(score);
                    //评价内容
                    String commentContent = commentJsonObject.getString("content");
                    comment.setComment(commentContent);
                    //用户客户端
                    String userClient = commentJsonObject.getString("userClientShow");
                    comment.setUserClient(userClient);
                    //用户追评
                    JSONObject afterUserCommentObject = commentJsonObject.getJSONObject("afterUserComment");
                    if (null != afterUserCommentObject) {
                        JSONObject hAfterUserCommentObject = afterUserCommentObject.getJSONObject("hAfterUserComment");
                        if (null != hAfterUserCommentObject) {
                            String afterContent = hAfterUserCommentObject.getString("content");
                            comment.setAfterComment(afterContent);
                        }
                    }

                    comments.add(comment);
                }
            }
        }
        return comments;
    }

    private String getProductId(String url) {
        String productId = null;
        if (url.startsWith(UrlEnum.PRODUCT_ITEM.getUrl())) {
            String subUrl = url.replace(UrlEnum.PRODUCT_ITEM.getUrl() + "/", "");
            productId = subUrl.substring(0, subUrl.indexOf("."));
        }
        return productId;
    }

    private Date parse(String dateStr){
        Date date = null;
        try {
            date = sdf1.parse(dateStr);
        } catch (ParseException e) {
        }
        try {
            if (date == null) {
                date = sdf2.parse(dateStr);
            }
        } catch (ParseException e) {
        }
        return date;
    }

}
