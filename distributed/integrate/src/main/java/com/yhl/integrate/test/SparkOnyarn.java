package com.yhl.integrate.test;


import com.yhl.integrate.Integrate;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.deploy.SparkSubmit;
import scala.Serializable;
import scala.Tuple2;

import java.util.Arrays;

public class SparkOnyarn implements Serializable {

    private static final long serialVersionUID = 1729438996113919101L;

    public  void  testSubminteOnyar(){
        //设置虚拟机参数
        System.setProperty("HADOOP_USER_NAME", "root");
        System.setProperty("user.name", "root");
        // spark配置
        SparkConf sparkConf=new SparkConf()
               // .setAppName("test")
               //.setMaster("spark://node-2:7077")
               //.set("deploy-mode", "client")
               // .set("spark.executor.memory", "512m")
              //  .set("spark.yarn.jars", "hdfs://master:9000/spark/jars/*")
                .set("spark.yarn.archive", "hdfs://master:9000/spark/*") //集群的jars包,是你自己上传上去的
                // .setJars(new String[]{"C:\\Users\\Administrator\\Desktop\\1.jar"})//这是sbt打包后的文件
                //这个可能是一个资源问题，应该给任务分配更多的 cores 和Executors，
                // 并且分配更多的内存。并且需要给RDD分配更多的分区
                .set("spark.dynamicAllocation.enabled", "false");

//                .setIfMissing("spark.driver.host","node-2");
        JavaSparkContext javaSparkContext =new JavaSparkContext(sparkConf);
        JavaRDD<String> lines = javaSparkContext.textFile("hdfs://master:9000/test.txt");
        JavaRDD<String> words = lines.flatMap(
                                      line -> Arrays.asList(line.split(" ")).iterator());
        JavaPairRDD<String, Integer> temp = words.mapToPair(word -> new Tuple2<>(word,1));
        JavaPairRDD<String, Integer> temp1 = temp.reduceByKey((m,n)->m+n);
        JavaPairRDD<Integer,String> swap =  temp1.mapToPair(tp -> tp.swap());
        JavaPairRDD<Integer,String> sorted =  swap.sortByKey(false);
        System.out.println("<<>>");
        swap.saveAsTextFile("hdfs://master:9000/1.txt");
        swap.collect().forEach(ele->{
            System.out.println("<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println(ele.toString());
        });
        System.out.println("<<>>");
    }
    public static void  sparksubmmit(){
        String[] args = new String[] {
                        "--master","spark://node-2:7077",
                        "--deploy-mode","cluster",
                        "--name","testsubmit",
                        "--class", Integrate.class.getName(),
                        "--jars", "hdfs://master:9000/spark/jars/*",
                        "--executor-memory", "512m",
                        "C:\\Users\\Administrator\\Desktop\\1.jar"
                         };
        SparkSubmit.main(new String[]{});
    }

    public static void main(String[] args) {
        sparksubmmit();
    }
}

