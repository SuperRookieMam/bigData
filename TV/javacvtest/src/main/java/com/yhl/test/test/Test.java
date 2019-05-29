package com.yhl.test.test;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.*;

import javax.swing.*;

public class Test {

    static boolean exit  = false;
    public static void main(String[] args) throws Exception {
        System.out.println("start...");
        String rtmpPath = "rtmp://10.10.0.55:1935/live";
        String rtspPath = "C:\\Users\\Administrator\\Desktop\\test.mp4";

        int audioRecord =0; // 0 = 不录制，1=录制
        boolean saveVideo = false;
        push(rtmpPath,rtspPath,audioRecord,saveVideo);

        System.out.println("end...");
    }
    /**
     * @param rtspPath-该地址可以是网络直播/录播地址，也可以是远程/本地文件路径  拉流的地址
     *  @param rtmpPath  -该地址可以是网络直播/录播地址，也可以是远程/本地文件路径  推流的地址
     * */
    public static void push(String rtmpPath,String rtspPath,int audioRecord,boolean saveVideo ) throws Exception  {
        // 使用rtsp的时候需要使用 FFmpegFrameGrabber，不能再用 FrameGrabber
        int width = 640,height = 480;
        //FFmpegFrameGrabber可以理解为解码器，也可以理解为帧收集器，
        // 主要作用就是将视频流以帧的形式拉去到手机设备上。
        FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(rtspPath);
        // 使用tcp的方式，不然会丢包很严重
        grabber.setOption("rtsp_transport", "tcp");
        grabber.setImageWidth(width);
        grabber.setImageHeight(height);
        System.out.println("grabber start");
        grabber.start();

        //
        OpenCVFrameConverter.ToIplImage conveter = new OpenCVFrameConverter.ToIplImage();
        System.out.println("all start!!");

        //抓取一帧视频并将其转换为图像，至于用这个图像用来做什么？加水印，人脸识别等等自行添加
        opencv_core.IplImage grabbedImage = conveter.convert(grabber.grab());


        // 流媒体输出地址，分辨率（长，高），是否录制音频（0:不录制/1:录制）
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(rtmpPath,width,height, audioRecord);
        recorder.setInterleaved(true);
        //recorder.setVideoOption("crf","28");
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264); // 28
        recorder.setFormat("flv"); // rtmp的类型
        recorder.setFrameRate(25);
        recorder.setImageWidth(width);
        recorder.setImageHeight(height);
        recorder.setPixelFormat(0); // yuv420p
        System.out.println("recorder start");
        recorder.start();


        //create a frame for real-time image display 创建一个实时贞
        CanvasFrame frame = new CanvasFrame("camera", CanvasFrame.getDefaultGamma() / grabber.getGamma());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        //不知道为什么这里不做转换就不能推到rtmp
        Frame rotatedFrame=conveter.convert(grabbedImage);


        long startTime=0;
        long videoTS=0;
        while(frame.isVisible() && (grabbedImage = conveter.convert(grabber.grab())) != null){
            rotatedFrame = conveter.convert(grabbedImage);
            frame.showImage(rotatedFrame);
            if (startTime == 0) {
                startTime = System.currentTimeMillis();
            }

            //时间间隔毫秒*1000？
            videoTS = 1000 * (System.currentTimeMillis() - startTime);
            recorder.setTimestamp(videoTS);
            recorder.record(rotatedFrame);
            System.out.println("推流中。。。。。");
            Thread.sleep(40);
        }

        grabber.stop();
        grabber.release();
        recorder.stop();
        recorder.release();
    }

}
