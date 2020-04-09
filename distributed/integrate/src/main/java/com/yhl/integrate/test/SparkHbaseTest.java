package com.yhl.integrate.test;

import com.twitter.chill.Base64;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.shaded.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.io.IOException;
import java.util.List;

public class SparkHbaseTest {

    public static void getValueFromHB() throws IOException {
          String f="BaseInfo";
        String table="NewCityWeather";
        Scan scan=new Scan();
        scan.addFamily(Bytes.toBytes(f));
        //scan.addColumn(f,)

        Configuration conf= HBaseConfiguration.create();//读取hbase-site.xml等配置
        //conf.set("hbase.zookeeper.quorum","10.3.9.135,10.3.9.231,10.3.9.232");//这些在hbase-site.xml中是有的
        //conf.set("hbase.zookeeper.property.clientPort","2222");
        conf.set(TableInputFormat.INPUT_TABLE,table);//设置查询的表
        conf.set(TableInputFormat.SCAN, Base64.encodeBytes(ProtobufUtil.toScan(scan).toByteArray()));//设置扫描的列
        //SparkConf confsp=new SparkConf().setAppName("SparkHBaseTest").setMaster("yarn-client");
        //SparkConf confsp=new SparkConf().setAppName("SparkHBaseTest").setMaster("spark://10.3.9.135:7077");

         //设置应用名称，就是在spark web端显示的应用名称，当然还可以设置其它的，在提交的时候可以指定，所以不用set上面两行吧
         SparkConf confsp=new SparkConf().setAppName("SparkHBaseTest");
         //创建spark操作环境对象
         JavaSparkContext sc = new JavaSparkContext(confsp);
//        JavaSparkContext sc = new JavaSparkContext("yarn-client", "hbaseTest",
//                System.getenv("SPARK_HOME"), System.getenv("JARS"));
        //sc.addJar("D:\\jiuzhouwork\\other\\sparklibex\\spark-examples-1.6.1-hadoop2.7.1.jar");

          //从数据库中获取查询内容生成RDD
        JavaPairRDD<ImmutableBytesWritable, Result> myRDD=sc.newAPIHadoopRDD(conf,TableInputFormat.class,ImmutableBytesWritable.class,Result.class);
          //遍历数据 collect foreach
          List<Tuple2<ImmutableBytesWritable, Result>> output=myRDD.collect();
          for (Tuple2 tuple: output ) {
                System.out.println(tuple._1+"："+tuple._2);
          }

         System.out.println("sss:"+myRDD.count());
        //System.out.println("sss:");
        //logger.info("lwwwww:");


        JavaRDD rdd=JavaRDD.fromRDD(JavaPairRDD.toRDD(myRDD),myRDD.classTag());
//        JavaRDD<Vector> points = myRDD.map(new ParsePoint());
//
//        KMeansModel model = KMeans.train(points.rdd(), k, iterations, runs, KMeans.K_MEANS_PARALLEL());
//
//        System.out.println("Cluster centers:");
//        for (Vector center : model.clusterCenters()) {
//            System.out.println(" " + center);
//        }
//        double cost = model.computeCost(points.rdd());
//        System.out.println("Cost: " + cost);
//
//        sc.stop();


    }

}
