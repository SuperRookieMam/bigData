package com.dm.hdc.hadoop.controller;


import com.dm.hdc.hadoop.flume.FlumeTest;
import com.dm.hdc.hadoop.hbase.HbaseTest;
import com.dm.hdc.hadoop.hdfs.HdfsFileUtil;
import com.dm.hdc.hadoop.kafaka.KafakaTest;
import com.dm.hdc.hadoop.yarn.spark.SparkTask;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("test")
public class TestController {
    @Autowired
    private HdfsFileUtil hdfsFileUtil;
    @Autowired
    private SparkTask sparkTask;
    @Autowired
    private HbaseTest hbaseTest;
    @Autowired
    private KafakaTest kafakaTest;
    @Autowired
    private FlumeTest flumeTest;

    @RequestMapping("up")
    public void test() {
        hdfsFileUtil.upfile("C:\\Users\\Administrator\\Desktop\\spark-examples_2.12-3.0.0-preview2.jar","/spark-jars","cover");
    }

    @RequestMapping("dowload")
    public void test1() {
        hdfsFileUtil.downLoad("/test/info.log","C:\\Users\\Administrator\\Desktop\\info.log");
    }

    @RequestMapping("submit")
    public String test2() throws IOException, InterruptedException {
        sparkTask.testSparkTask1();
        return "ceshi";
    }

    /**
     * 创建hbase 表格
     * */
    @RequestMapping("createTable")
    public String test3(@RequestParam("tableName") String tableName) throws IOException, InterruptedException {
        hbaseTest.creatTable(tableName, Arrays.asList("column1","column2","column3","column4","column5"));
        return "数据表创建成功";
    }
    /**
     * 创建hbase 表格
     * */
    @RequestMapping("alltable")
    public List<String> test4(){
        return hbaseTest.getAllTableNames();
    }

    @RequestMapping("tableMsg")
    public List<TableDescriptor> test5(@RequestParam("tableName") String tableName){
        return hbaseTest.getTableDescriptor(tableName);
    }

    @RequestMapping("kafakabgin")
    public String test6() {
        kafakaTest.beginFistCosumer();
        return "完成";
    }


    @RequestMapping("sendmsg")
    public String test7(@RequestParam("msg") String msg) {
        flumeTest.sendDataToFlume(msg);
        return "完成";
    }
}
