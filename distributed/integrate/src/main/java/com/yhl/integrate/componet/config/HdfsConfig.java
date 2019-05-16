package com.yhl.integrate.componet.config;

import com.yhl.integrate.util.HdfsUtile;
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
        //允许替换文件内容，时2.x以后才提供的，
        //对于dfs.client.block.write.replace-datanode-on-failure.enable，
        // 客户端在写失败的时候，是否使用更换策略，默认是true没有问题。
        configuration.set("dfs.client.block.write.replace-datanode-on-failure.enable","true");
        //对于，dfs.client.block.write.replace-datanode-on-failure.policy，
        // default在3个或以上备份的时候，是会尝试更换结点尝试写入datanode。
        // 而在两个备份的时候，不更换datanode，直接开始写。
        // 对于3个datanode的集群，只要一个节点没响应写入就会出问题，所以可以关掉
        configuration.set("dfs.client.block.write.replace-datanode-on-failure.enable","NEVER");
        //HDFS的实现类
        configuration.set("fs.hdfs.impl", DistributedFileSystem.class.getName());
        return configuration;
    }
    @Bean
    public HdfsUtile getHdfsUtile(){
        return new HdfsUtile();
    }
}
