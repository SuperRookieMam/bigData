package com.yhl.mappreduce.componet.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class YarnConfig {
    /**
     * 这个配置运行简单的运行
     * */
    @Bean("yarnConfiguration")
    public org.apache.hadoop.conf.Configuration getConfigration(){
        org.apache.hadoop.conf.Configuration configuration =new org.apache.hadoop.conf.Configuration();
        configuration.set("fs.defaultFS","hdfs://192.168.2.55:9000");
        configuration.set("mapreduce.framework.name","yarn");
        configuration.set("yarn.resourcemanager.hostname","192.168.2.55");
        //如果要在windows上运行需要加跨平台提交参数
        configuration.set("mapreduce.app-submission.cross-platform","true");
        configuration.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        configuration.set("mapreduce.application.classpath", "/usr/local/hadoop/etc/hadoop:/usr/local/hadoop/share/hadoop/common/lib/*:/usr/local/hadoop/share/hadoop/common/*:/usr/local/hadoop/share/hadoop/hdfs:/usr/local/hadoop/share/hadoop/hdfs/lib/*:/usr/local/hadoop/share/hadoop/hdfs/*:/usr/local/hadoop/share/hadoop/mapreduce/lib/*:/usr/local/hadoop/share/hadoop/mapreduce/*:/usr/local/hadoop/share/hadoop/yarn:/usr/local/hadoop/share/hadoop/yarn/lib/*:/usr/local/hadoop/share/hadoop/yarn/*");
        configuration.set("mapreduce.jobhistory.address","192.168.2.55:10020" );
        configuration.set("mapreduce.jobhistory.webapp.address","192.168.2.55:19888" ); ;


        return configuration;
    }
}
