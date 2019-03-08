import com.torm.orm.query.criteria.criterion.Criterion;
import com.torm.orm.query.criteria.criterion.Logical;
import com.torm.orm.query.criteria.criterion.MatchMode;
import com.torm.orm.query.criteria.criterion.expression.GroupExpression;
import com.torm.sample.dao.UserDao;
import com.torm.sample.entity.UserEntity;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TestUserDao {

    UserDao userDao = new UserDao();

    @Test
    public void findAll() {
        List<UserEntity> userEntityList = userDao.findAll();
    }

    @Test
    public void findAllByProperties() {
//        Sql: SELECT * FROM user WHERE (username LIKE '%thanh' OR id BETWEEN 1 AND 3) AND full_name IS NOT NULL
        List<Criterion> criterionList = new ArrayList<>();

        GroupExpression groupExpression = Logical.andGroup();
        groupExpression.add(Logical.and("username").like("thanh", MatchMode.START));
        groupExpression.add(Logical.or("id").between(2, 3));

        criterionList.add(groupExpression);
        criterionList.add(Logical.and("fullName").isNotNull());

        List<UserEntity> userEntityList = userDao.findAllByProperties(criterionList);
    }

    @Test
    public void findOneById() {
        UserEntity userEntity = userDao.findOneById(2);
    }

    @Test
    public void rowCount() {
        Long rowCount = userDao.rowCount();
    }

    @Test
    public void save() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("haimy");
        userEntity.setFullName("Tran Hai My");
        userEntity.setRoleId("USER");

        try {
            userDao.save(userEntity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void update() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(4);
        userEntity.setUsername("haimy");
        userEntity.setFullName("Tran Hai Cau");
        userEntity.setPassword("1->9");
        userEntity.setRoleId("ADMIN");

        try {
            userDao.update(userEntity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void delete() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(4);

        try {
            userDao.delete(userEntity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
