package com.yhl.estest.hello;

import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class HelloEs {
    public static void test( ) {
        Settings settings =Settings.builder()
//                .put("cluster.name","my-es")
//                //自动感知功能（可以通过当前指定的结点获取所有的es结点信息）
//                .put("client.transport.sniff",true)
                .build();
        //最好指定两个以上的结点细心，然后会自动选举出leader，如果一个，死了就勒
        TransportClient client =new PreBuiltTransportClient(settings)
                  .addTransportAddresses(
                          new TransportAddress(new InetSocketAddress("192.168.2.56",9300)));
        //库名，表明，id
        //存增加，如果不存在则创建表
        Map<String,String> map = new HashMap<>();
        map.put("username", "张三");
        JSONObject jsonObject =new JSONObject();
        jsonObject.put("NAME","zhangsan");
        IndexResponse response =client.prepareIndex("gamelog","user","1")
                                 .setSource(
                                         jsonObject
                                 ).get();
        //查询
        GetResponse getResponse =client.prepareGet("gamelog","user","1").get();
        System.out.println(getResponse.getSourceAsString());

    }




}
