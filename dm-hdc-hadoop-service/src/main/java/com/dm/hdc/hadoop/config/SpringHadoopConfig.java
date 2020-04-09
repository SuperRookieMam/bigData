package com.dm.hdc.hadoop.config;

import org.apache.spark.SparkConf;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.Map;

@Configuration
public class SpringHadoopConfig {
    @Resource
    private HadoopConfig hadoopConfig;

    @Bean("hdpConfigration")
    public org.apache.hadoop.conf.Configuration getHadoopConfigration() {
        org.apache.hadoop.conf.Configuration configuration =new org.apache.hadoop.conf.Configuration();
        //  根据application中的配置配置hadoop
        Map<String,String> hdfsConf = hadoopConfig.getHdfs();
        hdfsConf.keySet().forEach(ele-> configuration.set(ele,hdfsConf.get(ele)));
        // yarn 配置
        Map<String,String> yarnConf = hadoopConfig.getYarn();
        hdfsConf.keySet().forEach(ele-> configuration.set(ele,yarnConf.get(ele)));

        // 设置Hbase 的配置
        Map<String,String> hbaseConf = hadoopConfig.getHbase();
        hbaseConf.keySet().forEach(ele->configuration.set(ele,hbaseConf.get(ele)));
        return configuration;
    }

    @Bean("sparkConf")
    public SparkConf getSparkConfig() {
        SparkConf sparkConf = new SparkConf();
        Map<String,String> sparkConfig = hadoopConfig.getSpark();
        sparkConfig.keySet().forEach(ele->sparkConf.set(ele,sparkConfig.get(ele)));
        return sparkConf;
    }


}
