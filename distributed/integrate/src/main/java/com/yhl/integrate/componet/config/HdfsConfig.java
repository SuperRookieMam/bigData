package com.yhl.integrate.componet.config;

import com.yhl.integrate.util.HdfsUtile;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.spark.SparkConf;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.IOException;

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

        //  yarn配置 还需要配置需要
        configuration.set("mapreduce.framework.name","yarn");
        configuration.set("yarn.resourcemanager.hostname","node-2");
        //如果要在windows上运行需要加跨平台提交参数
        configuration.set("mapreduce.app-submission.cross-platform","true");
        configuration.set("mapreduce.application.classpath", "/usr/local/hadoop/etc/hadoop:/usr/local/hadoop/share/hadoop/common/lib/*:/usr/local/hadoop/share/hadoop/common/*:/usr/local/hadoop/share/hadoop/hdfs:/usr/local/hadoop/share/hadoop/hdfs/lib/*:/usr/local/hadoop/share/hadoop/hdfs/*:/usr/local/hadoop/share/hadoop/mapreduce/lib/*:/usr/local/hadoop/share/hadoop/mapreduce/*:/usr/local/hadoop/share/hadoop/yarn:/usr/local/hadoop/share/hadoop/yarn/lib/*:/usr/local/hadoop/share/hadoop/yarn/*");
        configuration.set("yarn.nodemanager.aux-services","mapreduce_shuffle");
        configuration.set("mapreduce.jobhistory.address","master:10020" );
        configuration.set("mapreduce.jobhistory.webapp.address","master:19888" ); ;

        return configuration;
    }
    @Bean
    public HdfsUtile getHdfsUtile(){
        return new HdfsUtile();
    }
//    注意这个没哟
    @Bean
    public SparkConf getSparkConf() throws IOException {
        //设置虚拟机参数
        System.setProperty("HADOOP_USER_NAME", "root");
        System.setProperty("user.name", "root");
        // spark配置
        SparkConf sparkConf=new SparkConf().setAppName("test");



        return sparkConf;
    }


}
