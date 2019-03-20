import com.torm.orm.query.sqlquery.SQLQuery;
import com.torm.orm.session.Session;
import com.torm.orm.session.SessionFactory;
import com.torm.sample.entity.RoleEntity;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.List;

public class TestSqlQuery {
    @Test
    public void test() {
        Session session = SessionFactory.openSession();
        try {
            assert session != null;
            SQLQuery sqlQuery = session.createSQLQuery("SELECT * FROM role WHERE id = {id}");
            sqlQuery.setEntity(RoleEntity.class);
            sqlQuery.setParam("id", "ADMIN");
            List list = sqlQuery.list();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            assert session != null;
            session.close();
        }
    }
}
