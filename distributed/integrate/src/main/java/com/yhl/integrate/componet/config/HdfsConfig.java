package com.yhl.integrate.componet.config;

import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class HdfsConfig {

    @Bean(name = "hdfsConfiguration")
    public org.apache.hadoop.conf.Configuration  getHdfdconfig(){
        org.apache.hadoop.conf.Configuration configuration =new org.apache.hadoop.conf.Configuration();
        /**
         *配置参数如下，如果需要配置其它参考hadoop官网
         * */
        //namenode 地址
        configuration.set("fs.defaultFS","hdfs://master:9000");
        //HDFS的实现类
        configuration.set("fs.hdfs.impl", DistributedFileSystem.class.getName());


        return configuration;
    }
}
