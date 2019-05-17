package com.yhl.integrate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Integrate {
        public static void main(String[] args) throws Exception {
            SpringApplication.run(Integrate.class, args);
           // HdfsUtile hdfsUtile =new HdfsUtile();
           // File file =new File("C:\\Users\\Administrator\\Desktop\\测试文本.txt");
           // InputStream inputStream =new FileInputStream(file);
          //  hdfsUtile.upfile(false,true,"/",inputStream,"文本测试");
           //  hdfsUtile.delete("/test1/2.zip",false);
           // hdfsUtile.copyToLocalFile("/文本测试","C:\\Users\\Administrator\\Desktop\\NEW.txt");
           // hdfsUtile.completeLocalOutput("/文本测试","C:\\Users\\Administrator\\Desktop\\测试文本.txt");
        }
}
