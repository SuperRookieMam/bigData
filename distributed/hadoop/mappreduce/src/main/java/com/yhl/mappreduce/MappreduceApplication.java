package com.yhl.mappreduce;

import com.yhl.mappreduce.Jobcommit.JobSubmit;
import com.yhl.mappreduce.map.MapReduceOfMap;
import com.yhl.mappreduce.reduce.MapReduceOfReduce;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MappreduceApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(MappreduceApplication.class, args);
        JobSubmit.submitJob("F:/1.jar", MapReduceOfMap.class,
                            MapReduceOfReduce.class,3,"/test/",
                   "/kk");
    }
}
