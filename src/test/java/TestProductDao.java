import com.github.zjzcn.spider.jd.comments.dao.ProductDao;
import com.github.zjzcn.spider.jd.comments.model.Product;

import java.util.Date;

/**
 * Created by tangb on 2017/3/18.
 */
public class TestProductDao {

    public static void main(String[] args){
        Product p = new Product();
        p.setProductId("12399");
        p.setShopName("TCL电视旗舰店");
        p.setSkuName("55寸黑色 曲面屏黑色电视");
        p.setSkuUrl("http://item.jd.com/12399.html");
        p.setSkuVersion("55寸黑色");
        p.setInsertTime(new Date());
        p.setGoodRate(0.955);
        p.setGoodRateTag("貌美如花");

        ProductDao dao = new ProductDao();
        dao.insert(p);
    }
}
