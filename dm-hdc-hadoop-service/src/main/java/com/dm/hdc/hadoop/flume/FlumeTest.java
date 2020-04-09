package com.dm.hdc.hadoop.flume;


import com.dm.hdc.hadoop.config.HadoopConfig;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.EventBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Properties;

@Component
public class FlumeTest {

    private static RpcClient client;

    private static Properties properties;

    @Autowired
    private HadoopConfig hadoopConfig;

    public void sendDataToFlume(String data) {
        initClient();
        Event event = EventBuilder.withBody(data, Charset.forName("UTF-8"));

        try {
            client.append(event);
        } catch (EventDeliveryException e) {
            e.printStackTrace();
            // clean up and recreate the client
            client.close();
            client = null;
            initClient();
        }
    }

    private void initClient() {
        if (client==null) {
            this.properties = new Properties();
            Map<String,String> flumeMap = hadoopConfig.getFlumeClient();
            flumeMap.keySet().forEach(ele->properties.setProperty(ele,flumeMap.get(ele)));
            this.client = RpcClientFactory.getInstance(properties);
        }
    }



    public void cleanUpClient() {
        // Close the RPC connection
        client.close();
    }


    public void beginAgent() {

    }


}
