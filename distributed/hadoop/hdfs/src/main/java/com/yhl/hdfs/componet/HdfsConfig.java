package com.yhl.hdfs.componet;

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
        configuration.set("fs.defaultFS","hdfs://10.10.1.105:9000");
        configuration.set("fs.hdfs.impl", DistributedFileSystem.class.getName());
        return configuration;
    }



}
