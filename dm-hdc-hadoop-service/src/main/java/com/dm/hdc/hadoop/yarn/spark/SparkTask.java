package com.dm.hdc.hadoop.yarn.spark;

import org.apache.hadoop.conf.Configuration;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.SparkSubmit;
import org.apache.spark.launcher.SparkAppHandle;
import org.apache.spark.launcher.SparkLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@Component
public class SparkTask {
    @Autowired
    private SparkConf sparkConf;
    @Autowired
    private Configuration hadoopConfiguration;

    //  提交倒yarn 执行spark 任务 与sparksubmit 参数一致
    public void testSparkTask() {
        System.setProperty("SPARK_YARN_MODE", "true");

        SparkSubmit.main(new String[]{
                "--class",
                "org.apache.spark.examples.SparkPi",
                "--master",
                "yarn",
                "--deploy-mode",
                "cluster",
                "--driver-memory",
                "1G",
                "--executor-cores",
                "1",
                "/usr/local/spark3.0/examples/spark-examples_2.12-3.0.0-preview2.jar",
                "40"
        });
    }
    //  提交倒yarn 执行spark 任务
    public void testSparkTask1() throws IOException, InterruptedException {
        CountDownLatch cdl= new CountDownLatch(1);
        SparkAppHandle handle = new SparkLauncher()
                .setSparkHome("/usr/local/spark3.0")
                .setAppResource("/usr/local/spark3.0/examples/jars/spark-examples_2.12-3.0.0-preview2.jar")
                .setMainClass("org.apache.spark.examples.SparkPi")
                .setMaster("yarn")
                .setDeployMode("client")
                .setAppName("test yarn client")
                // SPARK 的了依赖包，可以配置为本地，可以配置为haoop 的hdfs
                .setConf("spark.yarn.jars", "hdfs://10.10.1.105:9000/spark-jars/*")
                //允许跨平台提交
                .setConf("spark.driver.allowMultipleContexts", "true")
                .setConf("spark.executor.cores", "2")
                .setConf("spark.executor.instances", "2")
                // .addAppArgs("/README.md")
                .setVerbose(true)
                .startApplication(new SparkAppHandle.Listener() {
                    // 这里监听任务状态，当任务结束时（不管是什么原因结束）,isFinal方法会返回true,否则返回false
                    @Override
                    public void stateChanged(SparkAppHandle sparkAppHandle) {
                        if (sparkAppHandle.getState().isFinal()) {
                            cdl.countDown();
                        }
                        System.out.println("state:" + sparkAppHandle.getState().toString());
                    }

                    @Override
                    public void infoChanged(SparkAppHandle sparkAppHandle) {
                        System.out.println("Info:" + sparkAppHandle.getState().toString());
                    }
                });
        System.out.println("The task is executing, please wait ....");
        // 线程等待任务结束
        cdl.await();
        System.out.println("The task is finished!");

    }

}
