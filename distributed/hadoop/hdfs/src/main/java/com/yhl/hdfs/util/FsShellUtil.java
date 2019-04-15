package com.yhl.hdfs.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FsShell;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class FsShellUtil implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
         FsShell fsShell =new FsShell(SpringUtil.getBean("hdfsConfiguration", Configuration.class));
         System.out.println(fsShell.getCurrentTrashDir()+"<<<<<<<<<<<<<>>>>>>>>>>>>");
         System.out.println(fsShell.run(args));
    }

}
