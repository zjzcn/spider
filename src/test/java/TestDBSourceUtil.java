import com.github.zjzcn.spider.jd.comments.util.DBUtilsHelper;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;

import java.util.List;

/**
 * Created by tangb on 2017/3/18.
 */
public class TestDBSourceUtil {

    public static void main(String[] args) throws Exception {
        DBUtilsHelper dbh = new DBUtilsHelper();
        QueryRunner runner = dbh.getRunner();

        // 返回ArrayListHandler结果,第一行结果：List<Object[]>
        System.out.println("B:返回ArrayListHandler结果(仅显示5行).........");
        List<Object[]> arrayListResult = runner.query("select 1", new ArrayListHandler());
        System.out.println(arrayListResult.size());
        for (int i = 0; i < arrayListResult.size() && i < 5; i++) {
            System.out.println(arrayListResult.get(i)[i].toString() + "    ");
        }
    }

}
