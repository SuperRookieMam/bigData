package com.yhl.mappreduce.map;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/*
*注意：hadoop使用的自己的序列化机制
*   Mapper里面使用hadoop的包装类
*
* Mapper<KEYIN, VALUEIN, KEYOUT, VALUEOUT>
* 分类进出键值对
* 进应该时来自文件的 数据
* 出应该数传给shuffle 重组后在传给ruduce
* 所以reduce 那边键值对应该和准备按对对那个
* */
public class MapReduceOfMap extends Mapper<LongWritable, Text,Text, IntWritable> {

    /**
     * @param value 时文件的数据
     * @param key 包装的long
     * @param context 是切出来的对象
     * 看源码 ：提示类型提示 ：KEYIN, VALUEIN, KEYOUT, VALUEOUT
     *         分为进来的键值对，和出去的键值对，
     * */
  @Override
   public void  map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
     String line = value.toString();
     String[] words  = line.split(" ");
      for (String word : words) {
          context.write(new Text(word),new IntWritable(1));
      }

    }
}
