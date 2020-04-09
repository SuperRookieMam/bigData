package com.dm.hdc.hadoop.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix ="hadoop")
public class HadoopConfig {

    private Map<String,String> hdfs;

    private Map<String,String> yarn;

    private Map<String,String> spark;

    private Map<String,String> hbase;

    private Map<String,String> kafakaProductor;

    private Map<String,String> kafakaCustomer;

    private Map<String,String> flumeClient;
}
