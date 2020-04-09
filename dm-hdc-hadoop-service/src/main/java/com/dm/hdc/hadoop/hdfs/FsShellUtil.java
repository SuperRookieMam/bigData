package com.dm.hdc.hadoop.hdfs;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FsShell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class FsShellUtil implements CommandLineRunner {

    @Autowired
    private Configuration hdfsConfigrarion;

    private FsShell fsShell;

    @Override
    public void run(String... args) throws Exception {
        FsShell fsShell =getFsShell();
        System.out.println(fsShell.getCurrentTrashDir());
        System.out.println(fsShell.run(args));
    }

    public FsShell getFsShell() {
        if (fsShell==null) {
            fsShell = new FsShell(hdfsConfigrarion);
        }
        return fsShell;
    }

    public void closeFsShell() {
        if (fsShell!=null) {
            try {
                fsShell.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
