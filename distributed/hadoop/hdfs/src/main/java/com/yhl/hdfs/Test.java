package com.yhl.hdfs;

import com.yhl.hdfs.util.HdfsFileUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Test {

    public static void main(String[] args) throws Exception {
      SpringApplication.run(Test.class, args);
      HdfsFileUtil.upfile("C:\\Users\\Administrator\\Desktop\\2.zip","/test1","cover");
      // HdfsFileUtil.downLoad("/kk/_SUCCESS","C:\\Users\\Administrator\\Desktop\\test");
       // HdfsFileUtil.downLoad("/kk/part-r-00000","C:\\Users\\Administrator\\Desktop\\test");
     // System.out.println("亲测可用");
     //   FsShellUtil fsShellUtil =new FsShellUtil();
     //fsShellUtil.run(" hadoop fs -ls /");
    }


}
