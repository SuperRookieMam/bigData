package com.yhl.orm.componet.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@MapperScan(
            basePackages = {"com.**.mapper"},
            sqlSessionFactoryRef = "sqlSessionFactory"
            //貌似可以想jpa一样优雅的实现BaseDao，
            // 但是MyBatis用于复杂业务，所以不实现
            /*,factoryBean = MapperFactorBean.class*/
            )
@AutoConfigureAfter(DatasourceConfigration.class)
public class MybatisConfig {
    @Autowired
    @Qualifier("datasource")
    private DataSource dataSource;
    //xml位置
    private  String classpath="classpath*:mapping/**/*.xml";

    @Bean(name = "mysqlTransactionManager")
    @Primary
    public DataSourceTransactionManager mysqlTransactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("sqlSessionFactory")
    @Primary
    public SqlSessionFactory getSqlSessionFactory() throws Exception{
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        PathMatchingResourcePatternResolver solver=new PathMatchingResourcePatternResolver();
        Resource[] resources =solver.getResources(classpath);
        bean.setMapperLocations(resources);
        return bean.getObject();
    }



}
