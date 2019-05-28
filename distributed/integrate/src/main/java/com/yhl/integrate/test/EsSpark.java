package com.yhl.integrate.test;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.elasticsearch.spark.rdd.api.java.JavaEsSpark;
import org.spark_project.guava.collect.ImmutableList;
import org.spark_project.guava.collect.ImmutableMap;

import java.util.Map;

public class EsSpark {
    public void writeEs() {
        String elasticIndex = "spark/docs";
        //https://www.elastic.co/guide/en/elasticsearch/hadoop/current/spark.html#spark-native
        SparkConf sparkConf = new SparkConf().setAppName("writeEs").setMaster("local[*]").set("es.index.auto.create", "true")
                .set("es.nodes", "ELASTIC_SEARCH_IP").set("es.port", "9200").set("es.nodes.wan.only", "true");
        SparkSession sparkSession = SparkSession.builder().config(sparkConf).getOrCreate();
        JavaSparkContext jsc = new JavaSparkContext(sparkSession.sparkContext());//adapter
        Map<String, ?> numbers = ImmutableMap.of("one", 1, "two", 2);
        Map<String, ?> airports = ImmutableMap.of("OTP", "Otopeni", "SFO", "San Fran");
        JavaRDD<Map<String, ?>> javaRDD = jsc.parallelize(ImmutableList.of(numbers, airports));
        JavaEsSpark.saveToEs(javaRDD, elasticIndex);
    }

    public void readEs() {
        SparkConf sparkConf = new SparkConf().setAppName("writeEs").setMaster("local[*]").set("es.index.auto.create", "true")
                .set("es.nodes", "ELASTIC_SEARCH_IP").set("es.port", "9200").set("es.nodes.wan.only", "true");
        SparkSession sparkSession = SparkSession.builder().config(sparkConf).getOrCreate();
        JavaSparkContext jsc = new JavaSparkContext(sparkSession.sparkContext());//adapter
        JavaRDD<Map<String, Object>> searchRdd = esRDD(jsc, "index_ik_test/fulltext", "?q=中国");
        for (Map<String, Object> item : searchRdd.collect()) {
            item.forEach((key, value)->{
                System.out.println("search key:" + key + ", search value:" + value);
            });
        }
        sparkSession.stop();
    }
    public JavaRDD<Map<String,Object>> esRDD(JavaSparkContext jsc,String index,String param){
        return null;
    }
}
