import com.github.zjzcn.spider.jd.comments.model.Product;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tangb on 2017/3/18.
 */
public class TestBeanUtils {

    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("productId", "12345");
        map.put("shopName", "TCL电视旗舰店");
        map.put("skuName", "电视黑色");
        map.put("insertTime", new Date());
        map.put("goodRate", 0.754);
        map.put("123", 0.754);

        Product p = new Product();
        BeanUtils.copyProperties(p, map);
        System.out.println(p.getProductId());
        System.out.println(p.getShopName());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = null;
        try {
            date = sdf.parse("2017-03-18 16:30:33");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(date);
    }

}
