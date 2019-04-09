package com.yhl.orm.componet.factory;


import com.yhl.orm.dao.JpaBaseDao;
import com.yhl.orm.dao.Impl.JpaBaseDaoImpl;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.io.Serializable;

public class BaseDaoFactory <T, ID extends Serializable>  extends JpaRepositoryFactory {

    public BaseDaoFactory(EntityManager entityManager) {
        super(entityManager);
    }
    @Override
    public  JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
        //判断接口是不是继承自己的baseDao 如果是返回Basedao的实现,如果不是按照源码进行下去
        if (JpaBaseDao.class.isAssignableFrom(information.getRepositoryInterface())){
            return new JpaBaseDaoImpl<>(information.getDomainType(),entityManager);
        }
        JpaEntityInformation<?, Serializable> entityInformation = this.getEntityInformation(information.getDomainType());
        Object repository = this.getTargetRepositoryViaReflection(information, new Object[]{entityInformation, entityManager});
        Assert.isInstanceOf(JpaRepositoryImplementation.class, repository);
        return (JpaRepositoryImplementation)repository;
    }
    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        if (JpaBaseDao.class.isAssignableFrom(metadata.getRepositoryInterface())){
            return JpaBaseDao.class;
        }
        return SimpleJpaRepository.class;
    }
}
