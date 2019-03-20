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
    public void addSelectionMappedToArray() {
        Session session = SessionFactory.openSession();
        Criteria criteria = session.createCriteria(UserEntity.class);
        criteria.addSelection(Projections.rowCount());
        criteria.addSelection(Projections.min("id"));
        criteria.addSelection(Projections.max("id"));
        List<Object[]> resultList = criteria.list();
        session.close();
    }

    @Test
    public void addSelectionMappedToEntity() {
        Session session = SessionFactory.openSession();
        Criteria criteria = session.createCriteria(UserEntity.class);
        criteria.addSelection("fullName");
        criteria.addSelection("roleId");
        List<UserEntity> userEntityList = criteria.list();
        session.close();
    }

    @Test
    public void addSelectionGroupBy() {
        Session session = SessionFactory.openSession();
        Criteria criteria = session.createCriteria(UserEntity.class);
        criteria.addSelection(Projections.max("id").as("user_id"));
        criteria.addGroupBy("roleId");
        List selectionList = criteria.list();
        session.close();
    }

    @Test
    public void addOrder() {
        Session session = SessionFactory.openSession();
        Criteria criteria = session.createCriteria(UserEntity.class);
        criteria.addOrder(Order.asc("roleId"));
        List<UserEntity> userEntityList = criteria.list();
        session.close();
    }
}
