package com.yhl.integrate.test;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class SparkKafkaTest {
    //spak 与卡夫卡直连方式

    private static final Pattern SPACE = Pattern.compile(" ");

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
        JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(2));

        Set<String> topicsSet = new HashSet<>(Arrays.asList(topics.split(",")));
//        Map<String, Object> kafkaParams = new HashMap<>();
//        kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
//        kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
//        kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // Create direct kafka stream with brokers and topics
        JavaInputDStream<String> messages =
                jssc.socketTextStream("node-1",9092) ;
        // Get the lines, split them into words, count the words and print
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
