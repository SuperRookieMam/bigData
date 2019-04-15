package com.yhl.hdfs.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class HdfsFileUtil {



    /**
     * 判断hdfs是否存在这个文件
     * */
    public static boolean isEixsts(String path){
        try(FileSystem fileSystem = getDefaultFileSystem()) {
            return fileSystem.exists(new Path(path));
        }catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 从本地上传文件，如果远程服务器文件存在则覆盖文件
     * */
    public static void copyAndCoverFile(String localFilePath, String remoteFilePath) {

        Path localPath = new Path(localFilePath);
        Path remotePath = new Path(remoteFilePath);
        try (FileSystem fs =getDefaultFileSystem()) {
            /* fs.copyFromLocalFile 第一个参数表示是否删除源文件，第二个参数表示是否覆盖 */
            fs.copyFromLocalFile(false, true, localPath, remotePath);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
    /**
     * 从本地上传文件，如果文件存在则追加
     * */
    public static void appendToFile(String localFilePath, String remoteFilePath) throws IOException {

        Path remotePath = new Path(remoteFilePath);

        try (FileSystem fs = getDefaultFileSystem() ;
             FileInputStream in = new FileInputStream(localFilePath);) {
             FSDataOutputStream out = fs.append(remotePath);
            byte[] data = new byte[1024];
            int read = -1;
            while ((read = in.read(data)) > 0) {
                out.write(data, 0, read);
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * @param type 为cover 覆盖 为append 为追加
     * */
    public static boolean upfile(String localFilePath, String remoteFilePath,String type) {
        try {
            /* 判断文件是否存在 */
            FileSystem fs =  getDefaultFileSystem();
            boolean fileExists = fs.exists(new Path(remoteFilePath));
            if (!fileExists){
                fs.mkdirs(new Path(remoteFilePath));
            }
            /* 进行处理 */
            if (type.equals("cover")) { // 文件不存在，或者type则上传
                 copyAndCoverFile(localFilePath,remoteFilePath);
                 return true;
            } else if (fileExists&&type.equals("append")) { // 选择追加
                appendToFile(localFilePath,remoteFilePath);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
            return false;
    }

    /**
     * 获取本项目配置的config的FileSystem
     * 需要使用其它的自己设置
     * */
    public static FileSystem getDefaultFileSystem() throws IOException {
       Configuration configuration = SpringUtil.getBean("hdfsConfiguration", Configuration.class);
       if (configuration!=null){
           return FileSystem.newInstance(configuration);
       }
        return null;
    }


    public static void downLoad(String remoteFilePath, String localFilePath) {

        Path remotePath = new Path(remoteFilePath);

        try (FileSystem fs =getDefaultFileSystem()) {

            File f = new File(localFilePath);
            /* 如果文件名存在，自动重命名(在文件名后面加上 _0, _1 ...) */
            if (f.exists()) {
               Integer i = Integer.valueOf(0);
                while (true) {
                    f = new File(localFilePath + "_" + i.toString());
                    if (!f.exists()) {
                        localFilePath = localFilePath + "_" + i.toString();
                        break;
                    } else {
                        i++;
                        continue;
                    }
                }
            }
            // 下载文件到本地
            Path localPath = new Path(localFilePath);
            fs.copyToLocalFile(remotePath, localPath);
        } catch (IOException e) {
            e.printStackTrace();

        }

    }
}

