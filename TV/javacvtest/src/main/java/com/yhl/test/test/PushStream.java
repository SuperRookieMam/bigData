package com.yhl.test.test;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.*;

import javax.swing.*;

public class PushStream {

    /**
     * 按帧录制本机摄像头视频（边预览边录制，停止预览即停止录制）
     *
     * @author eguid
     * @param outputFile -录制的文件路径，也可以是rtsp或者rtmp等流媒体服务器发布地址
     * @param frameRate - 视频帧率
     */
    public static void recordCamera(String outputFile ,double frameRate) throws Exception {
        Loader.load(opencv_objdetect.class);
        //本机摄像头默认0，这里使用javacv的抓取器，至于使用的是ffmpeg还是opencv，请自行查看源码
        FrameGrabber grabber = FrameGrabber.createDefault(0);
        //开启抓取器
        grabber.start();
        System.out.println("开始抓取视频流.....");
        //转换器
        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();

        //抓取一帧视频并将其转换为图像，至于用这个图像用来做什么？加水印，人脸识别等等自行添加
        opencv_core.IplImage grabbedImage = converter.convert(grabber.grab());
        int width = grabbedImage.width();
        int height = grabbedImage.height();

        //输出设置
        FrameRecorder recorder = FrameRecorder.createDefault(outputFile, width, height);
        // avcodec.AV_CODEC_ID_H264，编码
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        //封装格式，如果是推送到rtmp就必须是flv封装格式
        recorder.setFormat("flv");
        recorder.setFrameRate(frameRate);

        //开启录制器
        recorder.start();
        System.out.println("开始录制视频.....");


        long startTime=0;
        long videoTS=0;
        //create a frame for real-time image display 创建一个实时贞
        CanvasFrame frame = new CanvasFrame("camera", CanvasFrame.getDefaultGamma() / grabber.getGamma());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        //不知道为什么这里不做转换就不能推到rtmp
        Frame rotatedFrame=converter.convert(grabbedImage);
        //当贞被访问过后，并且被转换的grabbedImage不为空
        while (frame.isVisible() && (grabbedImage = converter.convert(grabber.grab())) != null) {
            rotatedFrame = converter.convert(grabbedImage);
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
        frame.dispose();
        recorder.stop();
        recorder.release();
        grabber.stop();

    }

    public static void main(String[] args) throws Exception{
        //recordCamera("output.mp4",1,25);
         recordCamera("rtmp://10.10.0.55/live/record1",25);
    }
}
