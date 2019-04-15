package com.yhl.mappreduce.reduce;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

/**
 * 注意看源码: Reducer<KEYIN,VALUEIN,KEYOUT,VALUEOUT>
 *  类型提示：分类进和出注意和map的键值对想对应
 * */

public class MapReduceOfReduce extends Reducer<IntWritable, Text,Text,IntWritable> {

    /**
     * This method is called once for each key. Most applications will define
     * their reduce class by overriding this method. The default implementation
     * is an identity function.
     * @param context 应该时返回的
     */
    @SuppressWarnings("unchecked")
    protected void reduce(Text key, Iterable<IntWritable> values, Context context
    ) throws IOException, InterruptedException {
        Iterator<IntWritable>  iterator= values.iterator();
        int cont =0;
        while (iterator.hasNext()){
            IntWritable intWritable =iterator.next();
            cont +=intWritable.get();
        }
        //这个应该时运行完成的的东西
         context.write(key,new IntWritable(cont));

    }

}
