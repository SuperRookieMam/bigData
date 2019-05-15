package com.yhl.integrate.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 封装此类的原因，
 * 只是为操作的时候，直接传字符串，而不用取new path 啊什么的，
 * 初始化一个config配置的FileSystem，其实也是直接调用FileSystem工具类
 * */
@Component
public class HdfsUtile {

    private FileSystem fileSystem ;

    public HdfsUtile(){
       Configuration configuration = SpringUtil.getBean("hdfsConfiguration", Configuration.class);
        try {
            this.fileSystem =  FileSystem.newInstance(configuration);
        } catch (IOException e) {
            e.printStackTrace();
            throw new  RuntimeException("初始化失败");
        }
    }
    /**
     * 判断hdfs是否存在这个文件
     * */
    public boolean isEixsts(String path){
        try {
            return fileSystem.exists(new Path(path));
        }catch (IOException e){
            e.printStackTrace();
            throw new  RuntimeException("IO异常");
        }
    }
    /**
     * mkdir用来创建一个目录
     * */
    public boolean mkdir(String path){
        try {
            return fileSystem.mkdirs(new Path(path));
        }catch (IOException e){
            e.printStackTrace();
            throw new  RuntimeException("创建失败");
        }
    }
    /**
     * @param fsPermission 类似目录的访问权限
     *               511：r(读)w(写)x(执行):(属主权限)rwx:(属组权限)rwx:(其它用户权限)
     *
     * */
    public boolean mkdir(String path,String fsPermission){
        try {
            Assert.isNull(fsPermission,"传入正确的权限字符串");
            Assert.isTrue(fsPermission.length()!= 9,"传入正确的权限字符串");
            FsAction a =  FsAction.getFsAction(fsPermission.substring(0,3));
            FsAction b =  FsAction.getFsAction(fsPermission.substring(3,6));
            FsAction c =FsAction.getFsAction(fsPermission.substring(6));
            Assert.isTrue(a==null||b==null||c==null,"传入正确的权限字符串");
            return fileSystem.mkdirs(new Path(path),new  FsPermission(a,b,c));
        }catch (IOException e){
            e.printStackTrace();
            throw new  RuntimeException("创建失败");
        }
    }


    /**
     * @param deletsource 是否删除本地源文件
     * @param overwrite    如果服务器文件存在是否覆盖服务器文件
     * @param localPath  本地文件路径
     * @param remotePath  服务器文件路径
     * */
    public  void copyFromLocalFile(boolean deletsource,boolean overwrite, String localPath, String remotePath) {
        try   {
            /* fs.copyFromLocalFile 第一个参数表示是否删除源文件，第二个参数表示是否覆盖 */
            fileSystem.copyFromLocalFile(deletsource, overwrite, new Path(localPath), new Path(remotePath));
        } catch (IOException e) {
            e.printStackTrace();
            throw new  RuntimeException("上传失败");

        }
    }
    /**
     * 从本地上传文件，如果文件存在则追加
     * */
    public static void appendToFile(String localFilePath, String remoteFilePath) throws IOException {

        Path remotePath = new Path(remoteFilePath);

        try (FileSystem fs = getDefaultFileSystem();
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
