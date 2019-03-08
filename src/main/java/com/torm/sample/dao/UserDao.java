package com.torm.sample.dao;

import com.torm.orm.query.criteria.Criteria;
import com.torm.orm.query.criteria.criterion.Criterion;
import com.torm.orm.query.criteria.criterion.projection.Projections;
import com.torm.orm.session.Session;
import com.torm.orm.session.SessionFactory;
import com.torm.orm.transaction.Transaction;
import com.torm.sample.entity.UserEntity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    public List<UserEntity> findAll() {
        List<UserEntity> userEntityList = new ArrayList<>();
        Session session = this.getSession();

        try {
            userEntityList = session.createCriteria(UserEntity.class).list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return userEntityList;
    }

    public List<UserEntity> findAllByProperties(List<Criterion> criterionList) {
        List<UserEntity> userEntityList = new ArrayList<>();
        Session session = this.getSession();
        Criteria criteria = session.createCriteria(UserEntity.class);

        try {
//        set properties search
            if (criterionList != null) {
                criterionList.forEach(criterion -> criteria.addWhere(criterion));
            }
            userEntityList = criteria.list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return userEntityList;
    }

    public UserEntity findOneById(Integer id) {
        Session session = this.getSession();
        UserEntity userEntity = null;

        try {
            userEntity = session.get(UserEntity.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return userEntity;
    }

    public Long rowCount() {
        Session session = this.getSession();
        Criteria cr = session.createCriteria(UserEntity.class);
        Long rowCount = 0L;

        try {
            cr.addSelection(Projections.rowCount());
            rowCount = (Long) cr.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return rowCount;
    }

    public UserEntity save(UserEntity entity) throws SQLException {
        Session session = this.getSession();
        Transaction transaction = session.beginTransaction();

        try {
            session.save(entity);
            transaction.commit();
        } catch (SQLException e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }

        return entity;
    }

    public UserEntity update(UserEntity entity) throws SQLException {
        Session session = this.getSession();
        Transaction transaction = session.beginTransaction();

        try {
            session.update(entity);
            transaction.commit();
        } catch (SQLException e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }

        return entity;
    }

    public void delete(UserEntity entity) throws SQLException {
        Session session = this.getSession();
        Transaction transaction = session.beginTransaction();

        try {
            session.delete(entity);
            transaction.commit();
        } catch (SQLException e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    private Session getSession() {
        return SessionFactory.openSession();
    }
}
