import com.torm.orm.query.criteria.Criteria;
import com.torm.orm.query.criteria.criterion.Order;
import com.torm.orm.query.criteria.criterion.projection.Projections;
import com.torm.orm.session.Session;
import com.torm.orm.session.SessionFactory;
import com.torm.sample.entity.UserEntity;
import org.testng.annotations.Test;

import java.util.List;

public class TestCriteria {
    @Test
    public void addSelection() {
        Session session = SessionFactory.openSession();
        Criteria criteria = session.createCriteria(UserEntity.class);
        criteria.addSelection("fullName");
        criteria.addSelection("password");
        criteria.addSelection("roleId");
        List<UserEntity> userEntityList = criteria.list();
        session.close();
    }

    @Test
    public void addSelectionGroupBy() {
        Session session = SessionFactory.openSession();
        Criteria criteria = session.createCriteria(UserEntity.class);
        criteria.addSelection(Projections.max("id").as("idId"));
        criteria.addGroupBy("roleId");
        List userEntityList = criteria.list();
        session.close();
    }

    @Test
    public void addOrder() {
        Session session = SessionFactory.openSession();
        Criteria criteria = session.createCriteria(UserEntity.class);
        criteria.addOrderBy(Order.asc("roleId"));
        List<UserEntity> userEntityList = criteria.list();
        session.close();
    }
}
