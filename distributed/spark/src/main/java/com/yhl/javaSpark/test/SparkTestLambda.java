package com.yhl.javaSpark.test;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.Arrays;

public class SparkTestLambda {

    public static void main(String[] args) {
        SparkConf conf =new SparkConf().setAppName("javatest");
        JavaSparkContext context =new JavaSparkContext(conf);
        JavaRDD<String> lines =context.textFile(args[0]);
        JavaRDD<String> words = lines.flatMap(line -> Arrays.asList(line.split(" ")).iterator());
        JavaPairRDD<String,Integer> wordOne = words.mapToPair(word->new Tuple2<>(word,1));

        JavaPairRDD<String,Integer> reduce= wordOne.reduceByKey((m,n)->m+n);

        JavaPairRDD<Integer,String> swap =  reduce.mapToPair(tp -> tp.swap());

        JavaPairRDD<Integer,String> sorted =  swap.sortByKey(false);
        JavaPairRDD<String,Integer> result =  sorted.mapToPair(tp -> tp.swap());
        result.saveAsTextFile(args[1]);

        context.close();
    }
}
