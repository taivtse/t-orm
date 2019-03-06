import com.torm.orm.query.sqlquery.SqlQuery;
import com.torm.orm.session.Session;
import com.torm.orm.session.SessionFactory;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.List;

public class TestSqlQuery {
    @Test
    public void test() {
        Session session = SessionFactory.openSession();
        List list;
        try {
            SqlQuery sqlQuery = session.createSQLQuery("SELECT * FROM role WHERE id = {id}");
            sqlQuery.setParameter("id", "ADMIN");
            list = sqlQuery.list();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
