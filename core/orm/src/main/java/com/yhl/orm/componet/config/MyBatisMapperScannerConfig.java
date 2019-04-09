package com.yhl.orm.componet.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.spring.mapper.MapperScannerConfigurer;

import java.util.Properties;

@Configuration
@AutoConfigureAfter(MybatisConfig.class)
public class MyBatisMapperScannerConfig {

    /**
     * MapperScannerConfigurer将扫描basePackage所指定的包下的所有接口类（包括子类），
     * 如果它们在SQL映射文件中定义过，则将它们动态定义为一个Spring Bean，这样，
     * 我们在Service中就可以直接注入映射接口的bean，service中的代码*
     * */
    @Bean
    public  static MapperScannerConfigurer mapperScannerConfigurer(){
        //注意这里我用的通用的TK 通用的
        MapperScannerConfigurer mapperScannerConfigurer =new MapperScannerConfigurer();
        /**
         * 这个属性一般都用不到，只有当你配置多数据源的时候，
         * 这是会有多个sqlSessionFactory，
         * 你就需要通过该属性来指定哪一个sqlSessionFactory
         * */
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        mapperScannerConfigurer.setBasePackage("com.**.mapper");
        Properties properties = new Properties();
        properties.setProperty("mappers", Mapper.class.getName());
        properties.setProperty("notEmpty", "false");
        properties.setProperty("IDENTITY", "MYSQL");
        mapperScannerConfigurer.setProperties(properties);
        return mapperScannerConfigurer;
    }
}
