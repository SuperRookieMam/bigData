package com.dm.hdc.hadoop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HdpApplication {
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
        System.setProperty("HADOOP_USER_NAME" , "hadoop");
        SpringApplication.run(HdpApplication.class, args);
    }

}
