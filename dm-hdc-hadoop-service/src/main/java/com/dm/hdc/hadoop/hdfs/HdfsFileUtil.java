package com.dm.hdc.hadoop.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Component
public class HdfsFileUtil {

    @Autowired
    private Configuration hdfsConfigrarion;

    private FileSystem fileSystem;



    public boolean isEixsts(String path) {
        try  {
            return getFileSystem().exists(new Path(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 从本地上传文件，如果远程服务器文件存在则覆盖文件
     * */
    public void copyAndCoverFile(String localFilePath, String remoteFilePath) {
        try  {
            /* fs.copyFromLocalFile 第一个参数表示是否删除源文件，第二个参数表示是否覆盖 */
            getFileSystem().copyFromLocalFile(false, true, new Path(localFilePath), new Path(remoteFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从本地上传文件，如果文件存在则追加
     * */
    public void appendToFile(String localFilePath, String remoteFilePath) throws IOException {
        Path remotePath = new Path(remoteFilePath);
        try ( FileInputStream in = new FileInputStream(localFilePath);) {
            FSDataOutputStream out = getFileSystem().append(remotePath);
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
    public boolean upfile(String localFilePath, String remoteFilePath,String type) {
        try {
            /* 判断文件是否存在 */
            FileSystem fs =  getFileSystem();
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


    public void downLoad(String remoteFilePath, String localFilePath) {

        Path remotePath = new Path(remoteFilePath);
        try  {
            FileSystem fs =  getFileSystem();
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

    public FileSystem getFileSystem() throws IOException {
        if (fileSystem==null) {
            fileSystem =FileSystem.newInstance(hdfsConfigrarion);
        }
        return fileSystem;
    }


    public void closeFileSystem() throws IOException {
        if (fileSystem!=null) {
            fileSystem.close();
        }
    }



}
