package com.yhl.integrate.test;

import kafka.serializer.StringDecoder;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Tuple2;

import java.util.*;
import java.util.regex.Pattern;

public class SparkKafkaTest {
    //spak 与卡夫卡直连方式

    private static final Pattern SPACE = Pattern.compile(" ");

    // 这个时与卡夫卡直连
    public static void JavaDirectKafkaWordCount (String[] args) throws Exception {
        if (args.length < 3) {
            System.err.println("Usage: JavaDirectKafkaWordCount <brokers> <groupId> <topics>\n" +
                    "  <brokers> is a list of one or more Kafka brokers\n" +
                    "  <groupId> is a consumer group name to consume from topics\n" +
                    "  <topics> is a list of one or more kafka topics to consume from\n\n");
            System.exit(1);
        }

        //StreamingExamples.setStreamingLogLevels();

        String brokers = args[0];
        String groupId = args[1];
        String topics = args[2];

        // Create context with a 2 seconds batch interval
        SparkConf sparkConf = new SparkConf().setAppName("JavaDirectKafkaWordCount");
        //每两面读取一次
        JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(2));


        Set<String> topicsSet = new HashSet<>(Arrays.asList(topics.split(",")));
        Map<String, String> kafkaParams = new HashMap<>();
        kafkaParams.put( "bootstrap.servers", brokers);
        kafkaParams.put("group.id", groupId);
//        kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
/**
 * 注意我这个只是test没有维护zk偏移量，需要自己维护
 * */
        // Create direct kafka stream with brokers and topics
//       这个时创建一个soket 并且监听，当这个端口产生数据的时候就会进行计算
//        JavaInputDStream<String> messages =
//                jssc.socketTextStream("node-1",9092);
            //这个时利用卡夫卡 监听的卡夫卡的端口，当卡夫卡发生产生出具时就用进行计算
        JavaPairInputDStream<String,String> messages =  KafkaUtils.createDirectStream(jssc,
                                                              String.class,
                                                              String.class,
                                                              StringDecoder.class,
                                                              StringDecoder.class,
                                                              kafkaParams,
                                                              topicsSet );
        // Get the lines, split them into words, count the words and print
        //这里形成的时topic msge的 Tuple2<String, String>
        JavaDStream<String> lines = messages.flatMap(ele -> Arrays.asList(ele._2.split(" ")).iterator());
        JavaDStream<String> words = lines.flatMap(x -> Arrays.asList(SPACE.split(x)).iterator());
        JavaPairDStream<String, Integer> wordCounts = words.mapToPair(s -> new Tuple2<>(s, 1))
                .reduceByKey((i1, i2) -> i1 + i2);
        wordCounts.print();

        // Start the computation
        jssc.start();
        jssc.awaitTermination();
    }
    /**
     *  这个时直接使用sparkstreaming 不使用卡夫卡
     * */
    public static void sparkstreamingTest (String[] args) throws Exception {
        if (args.length < 3) {
            System.err.println("Usage: JavaDirectKafkaWordCount <brokers> <groupId> <topics>\n" +
                    "  <brokers> is a list of one or more Kafka brokers\n" +
                    "  <groupId> is a consumer group name to consume from topics\n" +
                    "  <topics> is a list of one or more kafka topics to consume from\n\n");
            System.exit(1);
        }
        String brokers = args[0];
        String groupId = args[1];
        String topics = args[2];
        // Create context with a 2 seconds batch interval
        SparkConf sparkConf = new SparkConf().setAppName("JavaDirectKafkaWordCount");
        //每两面读取一次
        JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(2));

        Set<String> topicsSet = new HashSet<>(Arrays.asList(topics.split(",")));
        // Create direct kafka stream with brokers and topics
//       这个时创建一个soket 并且监听，当这个端口产生数据的时候就会进行计算
        JavaInputDStream<String> messages =
                jssc.socketTextStream("node-1",8888);
        // Get the lines, split them into words, count the words and print
        //这里形成的时topic msge的 Tuple2<String, String>
        JavaDStream<String> lines = messages.flatMap(ele -> Arrays.asList(ele.split(" ")).iterator());
        JavaDStream<String> words = lines.flatMap(x -> Arrays.asList(SPACE.split(x)).iterator());
        JavaPairDStream<String, Integer> wordCounts = words.mapToPair(s -> new Tuple2<>(s, 1))
                .reduceByKey((i1, i2) -> i1 + i2);
        wordCounts.print();
        // Start the computation
        jssc.start();
        jssc.awaitTermination();
    }

}
