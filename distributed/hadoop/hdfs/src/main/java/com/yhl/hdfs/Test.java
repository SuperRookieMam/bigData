package com.yhl.hdfs;

import com.yhl.hdfs.util.HdfsFileUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Test {
    /**
     *注意org.apache.hadoop.security.AccessControlException: Permission denied
     * 解决办法有几种如下
     *  1. 虚拟机中直接hadoop fs -chmod 777 /
     *  2. 修改到Namenode上修改hadoop的配置文件：etc/hadoop/hdfs-site.xml 加入
     *      <property>
     *          <name>dfs.permissions</name>
     *          <value>false</value>
     *      </property>
     *  3. 用户环境变量添加 HADOOP_USER_NAME=hadoop，需重启电脑
     *  4. IDEA 的 VM 添加 -DHADOOP_USER_NAME=hadoop
     *  5. 代码中设置系统变量，需在加载配置类创建 fileSystem 对象前
     * */
    public static void main(String[] args) throws Exception {
      SpringApplication.run(Test.class, args);
      HdfsFileUtil.upfile("C:\\Users\\Administrator\\Desktop\\info.log","/test","cover");
      // HdfsFileUtil.downLoad("/kk/_SUCCESS","C:\\Users\\Administrator\\Desktop\\test");
       // HdfsFileUtil.downLoad("/kk/part-r-00000","C:\\Users\\Administrator\\Desktop\\test");
     // System.out.println("亲测可用");
     //   FsShellUtil fsShellUtil =new FsShellUtil();
     //fsShellUtil.run(" hadoop fs -ls /");
    }


}
