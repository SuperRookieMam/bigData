package com.yhl.mappreduce.Jobcommit;

import com.yhl.mappreduce.componet.util.SpringUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * 提交你写的jar包倒yarn 方便
 * 你自定义的jar包进行离线分析
 * */
public class JobSubmit {

    public static<M extends Mapper,R extends Reducer> boolean  submitJob(String jarPath, Class<M> mappclass, Class<R> reduceclass) throws IOException, ClassNotFoundException, InterruptedException {
        Job job =Job.getInstance(SpringUtil.getBean("yarnConfiguration",Configuration.class));
        //获得资源文件(.class文件)所在路径
        job.setJar(jarPath);
        // job.setJarByClass(JobSubmit.class);//根据类的类型class路径
        job.setMapperClass(mappclass);
        job.setReducerClass(reduceclass);
        //3封装本子job的mapreduce产生的结果数据的key value类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        //3封装本子job的mapreduce产生的结果数据的key value类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        //4.封装本次job要处理的输入数据集所在路径
        FileInputFormat.setInputPaths(job,new Path("/"));
        FileOutputFormat.setOutputPath(job,new Path("/kk"));//输出路径必须不存在
        //5.封装参数，想要启动的reduce task 的数量，//可以不用指定，map 不用指定，reduce可以指定
        job.setNumReduceTasks(2);
        boolean b =job.waitForCompletion(true);
        if (b){
            System.out.println("执行成功");
        }else {
            System.out.println("执行失败");
        }
        return b;
    }

}
