package com.github.zjzcn.spider;

import com.github.zjzcn.spider.jd.comments.pipeline.JdbcPipeline;
import com.github.zjzcn.spider.jd.comments.processor.JDCommentsCrawl;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangb on 2017/3/17.
 */
public class Main {

    public static void main(String[] args) {
        List<String> urls = new ArrayList<String>();
        //urls.add("https://club.jd.com/repay/2634567_31a199d4-656c-4413-ac97-8378741536ed_1.html");
        urls.add("https://list.jd.com/list.html?cat=737,794,798&ev=5305_7188%40exbrand_2505&sort=sort_totalsales15_desc&trans=1&JL=3_%E5%93%81%E7%89%8C_TCL#J_crumbsBar");
        urls.add("https://list.jd.com/list.html?cat=737,794,798&ev=5305_7188%40exbrand_2505&page=2&sort=sort_totalsales15_desc&trans=1&JL=6_0_0#J_main");
//        urls.add("https://item.jd.com/4261888.html");
//        urls.add("https://item.jd.com/10627656945.html");

        List<Pipeline> pipelines = new ArrayList<Pipeline>();
        pipelines.add(new ConsolePipeline());
        pipelines.add(new JdbcPipeline());

        Spider spider = Spider.create(new JDCommentsCrawl()).setPipelines(pipelines);
        for (String url :
                urls) {
            spider.addUrl(url);
        }
        spider.thread(10).run();
    }

}
