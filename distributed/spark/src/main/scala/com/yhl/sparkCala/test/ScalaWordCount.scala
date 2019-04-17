package com.yhl.sparkCala.test

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object ScalaWordCount {
  def main(args: Array[String]): Unit = {

     val conf = new  SparkConf().setAppName("sparktest");
     //创建spark执行的入口
      val sc =new SparkContext(conf);
      //指定从哪里读取创建Rdd (弹性分布式数据集)
      //
      val lines: RDD[String] =sc.textFile(args(0))
      val words: RDD[String] =lines.flatMap(_.split(" "))
      val wordAndOne: RDD[(String,Int)] =words.map((_, 1))
      val reduced: RDD[(String,Int)] =wordAndOne.reduceByKey(_+_)
      var  sorted: RDD[(String,Int)] = reduced.sortBy((_,false))
      sorted.saveAsTextFile(args(1));
      sc.stop()
  }

}
