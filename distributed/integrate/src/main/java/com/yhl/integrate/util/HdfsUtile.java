package com.yhl.integrate.util;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.util.Progressable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 封装此类的原因，
 * 只是为操作的时候，直接传字符串，而不用取new path 啊什么的，
 * 初始化一个config配置的FileSystem，其实也是直接调用FileSystem工具类
 * */
public class HdfsUtile implements Serializable {

    private static final long serialVersionUID = 6072549306149173532L;
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
     * @param fsPermission 类似目录的访问权限  rwxrwxrwx
     * r(读)w(写)x(执行):(属主权限)rwx:(属组权限)rwx:(其它用户权限)
     * */
    public boolean mkdir(String path,String fsPermission){
        try {
            Assert.isTrue(fsPermission!=null,"传入正确的权限字符串");
            Assert.isTrue(fsPermission.length()!= 9,"传入正确的权限字符串");
            FsAction a =  FsAction.getFsAction(fsPermission.substring(0,3));
            FsAction b =  FsAction.getFsAction(fsPermission.substring(3,6));
            FsAction c =FsAction.getFsAction(fsPermission.substring(6));
            Assert.isTrue(a!=null||b!=null||c!=null,"传入正确的权限字符串");
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
            fileSystem.copyFromLocalFile(deletsource, overwrite, new Path(localPath), new Path(remotePath));
        } catch (IOException e) {
            e.printStackTrace();
            throw new  RuntimeException("上传失败");

        }
    }

    /**
     * @param deletsource 是否删除本地源文件
     * @param overwrite    如果服务器文件存在是否覆盖服务器文件
     * @param localPahes  本地文件路径
     * @param remotePath  服务器文件路径
     * */
    public  void copyFromLocalFile(boolean deletsource,boolean overwrite, String remotePath, String... localPahes) {
        try   {
            if (StringUtils.isEmpty(localPahes)){
                Path[] paths =new Path[localPahes.length];
                for (int i = 0; i < localPahes.length; i++) {
                    paths[i] =new Path(localPahes[i]);
                }
                fileSystem.copyFromLocalFile(deletsource, overwrite,paths, new Path(remotePath));
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new  RuntimeException("上传失败");

        }
    }

    /**
     * 检查文件当前用户是否有权限操作
     * @param path  服务器文件路径
     * @param mode   需要判断的权限 rwx
     * */
    public  void access(String path, String mode) {
        try   {
                Assert.isTrue(mode!=null,"传入正确的权限字符串");
                Assert.isTrue(mode.length()!=3,"传入正确的权限字符串");
                FsAction a =  FsAction.getFsAction(mode);
                fileSystem.access(new Path(path),a);
        } catch (IOException e) {
            e.printStackTrace();
            throw new  RuntimeException("上传失败");

        }
    }
    /**
     * @param path  服务器文件路径
     * @param progressable   自定义进度如何报告，需要实现 progressable接口自定义报告逻辑
     * @param bufferSize  缓冲区的大小
     * */
    public FSDataOutputStream append(String path, int bufferSize, Progressable progressable){
        try   {
            return fileSystem.append(new Path(path),bufferSize,progressable);
        } catch (IOException e) {
            e.printStackTrace();
            throw new  RuntimeException("获取输出流失败");

        }
    }
    /**
     * @param path  服务器文件路径
     * @param bufferSize 指定缓冲区的大小
     * */
    public FSDataOutputStream append(String path, int bufferSize){
            return append( path,  bufferSize, null);
    }
    /**
     * @param f  服务器文件路径
     *           使用config 设置的默认的缓冲区的大小
     * */
    public FSDataOutputStream append(String f)  {
        try {
            return fileSystem.append(new Path(f));
        } catch (IOException e) {
            e.printStackTrace();
            throw new  RuntimeException("获取输出流失败");
        }
    }

    /**
     * 不知道干啥用的
     * */
    public FSDataOutputStreamBuilder appendFile(String f) {
        return fileSystem.appendFile(new Path(f));
    }
    /**@param f the path to delete.
     * @param recursive if path is a directory and set to
     * true, the directory is deleted else throws an exception. In
     * case of a file the recursive can be set to either true or false.
     * @return  true if delete is successful else false.
     * */
    public  boolean delete(String f, boolean recursive){
        try {
            return  fileSystem.delete(new Path(f),recursive);
        } catch (IOException e) {
            e.printStackTrace();
            throw new  RuntimeException("删除失败");
        }
    }

    public boolean deleteOnExit(String f) {
        try {
            return fileSystem.deleteOnExit(new Path(f));
        } catch (IOException e) {
            e.printStackTrace();
            throw new  RuntimeException("删除失败");
        }
    }
    public void deleteSnapshot(String path, String snapshotName) {
        try {
            fileSystem.deleteSnapshot(new Path(path),snapshotName);
        } catch (IOException e) {
            e.printStackTrace();
            throw new  RuntimeException("删除失败");
        }
    }
    /**
     * 取消删除
     * */
    public boolean cancelDeleteOnExit(String f) {
        return fileSystem.cancelDeleteOnExit(new Path(f));
    }
    /**
     * Move blocks from srcs to trg and delete srcs afterwards.
     * The file block sizes must be the same.
     *
     * @param trg existing file to append to
     * @param psrcs list of files (same block size, same replication)
     * @throws IOException
     */
    public void concat(  String trg,  String... psrcs) {
        final Path path =new Path(trg);
        final Path[] paths = new Path[psrcs.length];
        for (int i = 0; i <psrcs.length ; i++) {
            paths[i] = new Path(psrcs[i]);
        }
        try {
            fileSystem.concat(path,paths);
        } catch (IOException e) {
            e.printStackTrace();
            throw new  RuntimeException("串联文件失败");
        }
    }

    /**
     * Called when we're all done writing to the target.
     * A local FS will do nothing, because we've written to exactly the
     * right place.
     * A remote FS will copy the contents of tmpLocalFile to the correct target at
     * fsOutputFile.
     * @param fsOutputFile path of output file
     * @param tmpLocalFile path to local tmp file
     * @throws IOException IO failure
     * 按照本地文件同步倒HDFS文件，本地文件为标准
     */
    public void completeLocalOutput(String fsOutputFile, String tmpLocalFile) {
        try {
            fileSystem.completeLocalOutput(new Path(fsOutputFile), new Path(tmpLocalFile));
        } catch (IOException e) {
            e.printStackTrace();
            throw new  RuntimeException("同步文件失败");
        }
    }

    /**
     * The src file is under this filesystem, and the dst is on the local disk.
     * Copy it from the remote filesystem to the local dst name.
     * delSrc indicates if the src will be removed
     * or not. useRawLocalFileSystem indicates whether to use RawLocalFileSystem
     * as the local file system or not. RawLocalFileSystem is non checksumming,
     * So, It will not create any crc files at local.
     *
     * @param delSrc  是否删除源文件
     * @param src path
     * @param dst  path
     * @param useRawLocalFileSystem 是否使用原始本地文件系统作为本地文件系统
     *
     * @throws IOException for any IO error
     */
    public void copyToLocalFile(boolean delSrc, String src, String dst,  boolean useRawLocalFileSystem)  {
        try {
            fileSystem.copyToLocalFile(delSrc,new Path(src),new Path(dst),useRawLocalFileSystem);
        } catch (IOException e) {
            e.printStackTrace();
            throw new  RuntimeException("拷贝文件失败");
        }
    }
    /**
     * Copy it a file from the remote filesystem to the local one.
     * @param src path src file in the remote filesystem
     * @param dst path local destination
     * @throws IOException IO failure
     */
    public void copyToLocalFile(String src, String dst){
        copyToLocalFile(false, src, dst);
    }

    /**
     * Copy a file to the local filesystem, then delete it from the
     * remote filesystem (if successfully copied).
     * @param src path src file in the remote filesystem
     * @param dst path local destination
     * @throws IOException IO failure
     */
    public void moveToLocalFile(String src, String dst){
        copyToLocalFile(true, src, dst);
    }

    /**
     * Copy it a file from a remote filesystem to the local one.
     * delSrc indicates if the src will be removed or not.
     * @param delSrc whether to delete the src
     * @param src path src file in the remote filesystem
     * @param dst path local destination
     * @throws IOException IO failure
     */
    public void copyToLocalFile(boolean delSrc, String src, String dst) {
        copyToLocalFile(delSrc, src, dst, false);
    }
    /**
     * Create an FSDataOutputStream at the indicated Path.
     * @param f the file to create
     * @param overwrite if a path with this name already exists, then if true,
     *   the file will be overwritten, and if false an error will be thrown.
     * @param bufferSize the size of the buffer to be used.
     * @throws IOException IO failure
     * 其它创建文件的方法可能用处不多，要用的时候自己看API吧
     */
    public FSDataOutputStream create(Path f, boolean overwrite, int bufferSize ){
        try {
            return fileSystem. create(f, overwrite, bufferSize);
        } catch (IOException e) {
            e.printStackTrace();
            throw new  RuntimeException("创建文件失败");
        }
    }
    /**
     * Create a snapshot with a default name.
     * @param path The directory where snapshots will be taken.
     * @return the snapshot path.
     * @throws IOException IO failure
     * @throws UnsupportedOperationException if the operation is unsupported
     */
    public final Path createSnapshot(String path) {
        return createSnapshot(path, null);
    }

    /**
     * Create a snapshot.
     * @param path The directory where snapshots will be taken.
     * @param snapshotName The name of the snapshot
     * @return the snapshot path.
     * @throws IOException IO failure
     * @throws UnsupportedOperationException if the operation is unsupported
     */
    public Path createSnapshot(String path, String snapshotName) {
        try {
            return fileSystem.createSnapshot(new Path(path),snapshotName);
        } catch (IOException e) {
            e.printStackTrace();
            throw new  RuntimeException("创建快照");
        }
    }
    /**
     * Create a new FSDataOutputStreamBuilder for the file with path.
     * Files are overwritten by default.
     *
     * @param path file path
     * @return a FSDataOutputStreamBuilder object to build the file
     *
     * HADOOP-14384. Temporarily reduce the visibility of method before the
     * builder interface becomes stable.
     */
    public FSDataOutputStreamBuilder createFile(String path) {
        return fileSystem.createFile(new Path(path));
    }


    /**
     * Creates the given Path as a brand-new zero-length file.  If
     * create fails, or if it already existed, return false.
     * <i>Important: the default implementation is not atomic</i>
     * @param f path to use for create
     * @throws IOException IO failure
     */
    public boolean createNewFile(String f)  {
        try {
            return fileSystem.createNewFile(new Path(f));
        } catch (IOException e) {
            e.printStackTrace();
            throw new  RuntimeException("创建一个空的文件");
        }
    }

    /**
     * 本类只时封装了几类常用方法
     * 其它定义需要fileSystem自行实现
     * */
    public FileSystem getDefaultFileSystem(){
        return fileSystem;
    }

    /**
     *@param  localPath  获取本地文件路径的封装类此类
     * 如果传入的是目录下所有文件，和子目录下面的所有文件的文件path
     *
     * */
    public List<Path> getFilesPaths(String localPath) {
        File file =new File(localPath);
        Assert.isTrue(file.exists(),"文件不存在");
        List<Path> list = new ArrayList<>();
        if (file.isFile()){
            list.add(new Path(localPath));
        }else {
           File[] files =  file.listFiles();
            for (int i = 0; i < files.length; i++) {
                list.addAll(getFilesPaths(files[i].getPath()));
            }
        }
        return list;
    }

    /**
     *@param  localPath  获取本地文件路径的封装类此类
     * 如果传入的是目录下所有文件，和子目录下面的所有文件的文件path
     *
     * */
    public Path[] getFilesPath(String localPath) {
        List<Path> list =getFilesPaths(localPath);
        return list.toArray(new Path[list.size()]);
    }
    /**
     * @param deletsource 是否删除本地原文件
     * @param overwrite 是否覆盖
     * */
    public  boolean upfile(boolean deletsource,boolean overwrite, String remotePath, InputStream inputStream,String fileName) {
        Assert.isTrue(inputStream!=null,"nullException");
        Assert.isTrue(fileName!=null,"nullException");
        try {
            Assert.isTrue(fileSystem.exists(new Path(remotePath)),"文件路径不存在");
            Path path =new Path(remotePath+fileName);
            FSDataOutputStream fsDataOutputStream = null;
             if (!fileSystem.exists(path)){
                 fsDataOutputStream = fileSystem.create(path,overwrite);
             }else {
                 if (fileSystem.deleteOnExit(path)) {
                     fsDataOutputStream = fileSystem.create(path, overwrite);
                 }
             }
             byte[] bytes = getBytes(inputStream);
             fsDataOutputStream.write(bytes);
             //异步需要MapReduce的支持
            //fsDataOutputStream.hsync();
            // 这样有丢失数据的危险
             fsDataOutputStream.flush();
             fsDataOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new  RuntimeException("上传文件失败");
        }
    }
    /**
     * 下载文件，数据写入本地文件
     * */
    public  FSDataInputStream open(String remoteFilePath) {
        Path remotePath = new Path(remoteFilePath);
        try  {
            Path path =new Path(remoteFilePath);
            Assert.isTrue(fileSystem.exists(path),"文件不存在");
            FSDataInputStream fsDataInputStream =fileSystem.open(path);
            return fsDataInputStream;
        } catch (IOException e) {
            e.printStackTrace();
            throw new  RuntimeException("获取文件输入流失败");
        }
    }

    public  byte[] getBytes(InputStream inputStream) throws IOException {
        ByteOutputStream byteOutputStream =new ByteOutputStream();
        byteOutputStream.write(inputStream);
        return byteOutputStream.getBytes();
    }
}
