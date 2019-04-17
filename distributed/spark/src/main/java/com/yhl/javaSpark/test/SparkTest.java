package com.yhl.javaSpark.test;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Iterator;

public class SparkTest {
    public static void main(String[] args) {
        SparkConf conf =new SparkConf().setAppName("javatest");
        JavaSparkContext context =new JavaSparkContext(conf);
        JavaRDD<String> lines =context.textFile(args[0]);
        JavaRDD<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public Iterator<String> call(String s) throws Exception {
                return Arrays.asList(s.split(" ")).iterator();
            }
        });
       JavaPairRDD<String,Integer> wordOne = words.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String s) throws Exception {
                return new Tuple2<>(s,1);
            }
        });

        JavaPairRDD<String,Integer> reduce= wordOne.reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer integer, Integer integer2) throws Exception {
                return integer+integer2;
            }
        });

        JavaPairRDD<Integer,String> swap =  reduce.mapToPair(new PairFunction<Tuple2<String, Integer>, Integer, String>() {
            @Override
            public Tuple2<Integer, String> call(Tuple2<String, Integer> stringIntegerTuple2) throws Exception {
                return stringIntegerTuple2.swap();
            }
        });

        JavaPairRDD<Integer,String> sorted =  swap.sortByKey(false);
        JavaPairRDD<String,Integer> swap1 =  sorted.mapToPair(new PairFunction<Tuple2<Integer,String>, String, Integer>() {
            @Override
            public Tuple2<String,Integer> call(Tuple2<Integer ,String> stringIntegerTuple2) throws Exception {
                return stringIntegerTuple2.swap();
            }
        });
        swap1.saveAsTextFile(args[1]);
        context.close();
    }
}
