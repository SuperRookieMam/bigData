package com.yhl.integrate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.Serializable;

@SpringBootApplication
public class Integrate implements Serializable {
    private static final long serialVersionUID = 6439426974989257703L;

    public static void main(String[] args) throws Exception {
          SpringApplication.run(Integrate.class, args);
           // HdfsUtile hdfsUtile =new HdfsUtile();
           // File file =new File("C:\\Users\\Administrator\\Desktop\\测试文本.txt");
           // InputStream inputStream =new FileInputStream(file);
          //  hdfsUtile.upfile(false,true,"/",inputStream,"文本测试");
           //  hdfsUtile.delete("/test1/2.zip",false);
           // hdfsUtile.copyToLocalFile("/文本测试","C:\\Users\\Administrator\\Desktop\\NEW.txt");
           // hdfsUtile.completeLocalOutput("/文本测试","C:\\Users\\Administrator\\Desktop\\测试文本.txt");
        //设置虚拟机参数
//        System.setProperty("HADOOP_USER_NAME", "root");
//        System.setProperty("user.name", "root");
//        String[] argss = new String[] {
//                "--master","yarn",
//                "--deploy-mode","cluster",
//                "--name","testsubmit",
//                "--class", SparkOnyarn.class.getName(),
//                "--jars","hdfs://master:9000/spark/jars/*",
//                "--executor-memory","512m",
//                "--conf","spark.yarn.jars=hdfs://master:9000/spark/jars/*",
//                "--conf","spark.yarn.archive=hdfs://master:9000/spark/*",
//                "--conf","spark.dynamicAllocation.enabled=false" ,
//                "hdfs://10.10.0.55:9000/spark/1.jar"
//        };
//        SparkSubmit.main(argss);
//        DruidDataSource druidDataSource =new DruidDataSource();
//        druidDataSource.setUrl("jdbc:mysql://10.10.0.107:3306/hive?createDatabaseIfNotExist=true");
//        druidDataSource.setUsername("root");
//        druidDataSource.setPassword("123456");
//        druidDataSource.setDriverClassName("com.mysql.jdbc.Driver");
//        JdbcTemplate jdbcTemplate = new JdbcTemplate(druidDataSource);
//        HiveTest hiveTest =   new HiveTest(jdbcTemplate);
//        System.out.println(hiveTest.createTable());

       }
}
