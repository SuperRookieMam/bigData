package com.yhl.hbase.componet.config;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class HbaseConfig {

   @Bean("hBaseConfiguration")
  public  org.apache.hadoop.conf.Configuration hbaseConfig(){
       org.apache.hadoop.conf.Configuration configuration = HBaseConfiguration.create();
       configuration.set("hbase.zookeeper.quorum","");
       configuration.set("hbase.zookeeper.port","");
        return configuration;
  }
  @Bean("hbaseTemplate")
  public HbaseTemplate hbaseTemplate(@Qualifier("hBaseConfiguration") org.apache.hadoop.conf.Configuration configuration){
    HbaseTemplate hbaseTemplate = new HbaseTemplate();
      hbaseTemplate.setConfiguration(configuration);
      hbaseTemplate.setAutoFlush(true);
      return hbaseTemplate;
  }


}
