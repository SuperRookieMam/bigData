package com.dm.hdc.hadoop.kafaka;

import com.dm.hdc.hadoop.config.HadoopConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

@Component
public class KafakaTest {
    private Properties productPropertis;
    private Properties customPropertis;
    private static KafkaConsumer<String, String> consumer;
    @Autowired
    private HadoopConfig hadoopConfig;




    /**
     * kafa 不带事物的生产者实列
     *
     * */
    public void  beginfirstProducter(){
        Properties props = getProductPropertis();
        Producer<String, String> producer = new KafkaProducer<>(props);
        for (int i = 0; i < 100; i++){
            producer.send(new ProducerRecord<String, String>("my-topic", Integer.toString(i), Integer.toString(i)));
            System.out.println(Integer.toString(i)+"<<<<<<<<<<<<<<<写入完成>>>>>>>>>>>>>>>>>>>>>>>>"+Integer.toString(i));
        }
        producer.close();
    }

    // 这个是 带事物的生产者
    public void  beginfirstProducterAndTransational(){
        Properties props = getProductPropertis();
        Producer<String, String> producer = new KafkaProducer<>(props, new StringSerializer(), new StringSerializer());
        producer.initTransactions();
        try {
            producer.beginTransaction();
            for (int i = 0; i < 100; i++)
            producer.send(new ProducerRecord<>("first", Integer.toString(i), Integer.toString(i)));
            producer.commitTransaction();
        } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
            producer.close();
        } catch (KafkaException e) {
            producer.abortTransaction();
        }
        producer.close();
    }


    /**
     *消费者 这个例子演示了Kafka的消费者api的一个简单用法，它依赖于自动偏移提交
     * */
    @Async
    public void  beginFistCosumer(){
        KafkaConsumer<String, String> consumer = getCustomer();
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record.key()+"<<<===>>>"+record.value());
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
            }
        }
    }
   /**
    *  消费者 用户还可以控制何时应该将记录视为已使用的记录，从而提交其偏移量，
    *  而不是依赖于使用者定期提交已使用的偏移量。
    *  当消息的消耗与一些处理逻辑相耦合时，这是非常有用的，因此在完成处理之前，不应该将消息视为已消耗。
    * */
    public void  beginSecondcosumer(){
        Properties props = getCustomPropertis();
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("foo", "bar"));
        final int minBatchSize = 200;
        List<ConsumerRecord<String, String>> buffer = new ArrayList<>();
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                buffer.add(record);
            }
            System.out.println(buffer);
            if (buffer.size() >= minBatchSize) {
                //insertIntoDb(buffer);
                consumer.commitSync();
                buffer.clear();
            }
        }
    }


    private Properties getProductPropertis() {
       if (productPropertis==null){
           productPropertis = new Properties();
           Map<String,String> kafamap =  hadoopConfig.getKafakaProductor();
           kafamap.keySet().forEach(ele->productPropertis.setProperty(ele, kafamap.get(ele)));
       }
       return productPropertis;
    }
    private Properties getCustomPropertis() {
        if (customPropertis==null){
            customPropertis = new Properties();
            Map<String,String> kafamap =  hadoopConfig.getKafakaCustomer();
            kafamap.keySet().forEach(ele->customPropertis.setProperty(ele, kafamap.get(ele)));
        }
        return customPropertis;
    }
    private KafkaConsumer<String, String> getCustomer() {
        if (consumer==null) {
            consumer = new KafkaConsumer<>(getCustomPropertis());
            consumer.subscribe(Arrays.asList("first"));
        }
        return consumer;
    }


}

