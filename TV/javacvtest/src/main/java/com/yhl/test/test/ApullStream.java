package com.yhl.test.test;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

public class ApullStream {
    /**
     * 按帧录制视频
     *
     * @param inputFile -该地址可以是网络直播/录播地址，也可以是远程/本地文件路径
     *
     * @param outputFile -该地址只能是文件地址，如果使用该方法推送流媒体服务器会报错，原因是没有设置编码格式
     *
     * @param audioChannel -是否录制音频（0:不录制/1:录制）
     */
    public static void frameRecord(String inputFile, String outputFile, int audioChannel) {
        // 该变量建议设置为全局控制变量，用于控制录制结束
        boolean isStart = true;
        // 获取视频源
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        // 使用tcp的方式，不然会丢包很严重
        grabber.setOption("rtsp_transport", "tcp");
        // 流媒体输出地址，分辨率（长，高），是否录制音频（0:不录制/1:录制）
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, 1280, 720, audioChannel);
        // 开始取视频源
        System.err.println(inputFile + "的推流已經開始推送，推送目標是：" + outputFile);
        recordByFrame(grabber, recorder, isStart);
    }

    private static void recordByFrame(FFmpegFrameGrabber grabber, FFmpegFrameRecorder recorder, Boolean status) {
        System.out.println ("开始启动无敌爬流线程");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    grabber.start();
                    recorder.setInterleaved(true);
                    // 该参数用于降低延迟
                    // recorder.setVideoOption("tune", "zerolatency");
                    // ultrafast(终极快)提供最少的压缩（低编码器CPU）和最大的视频流大小；
                    // 参考以下命令: ffmpeg -i '' -crf 30 -preset ultrafast
                    recorder.setVideoOption("preset", "ultrafast");
                    // 提供输出流封装格式(rtmp协议只支持flv封装格式)
                    recorder.setFormat("flv");
                    // video的编码格式
                    recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
                    recorder.setVideoOption("crf", "30");
                    // 不可变(固定)音频比特率
                    recorder.setAudioOption("crf", "0");
                    // 2000 kb/s, 720P视频的合理比特率范围
                    // recorder.setVideoBitrate(2000000);
                    recorder.setVideoQuality(0);
                    // 视频帧率(保证视频质量的情况下最低25，低于25会出现闪屏
                    recorder.setFrameRate(25);
                    // 关键帧间隔，一般与帧率相同或者是视频帧率的两倍
                    recorder.setGopSize(25 * 2);
                    recorder.setAudioQuality(0);
                    // 音频比特率
                    recorder.setAudioBitrate(192000);
                    // 音频采样率
                    recorder.setSampleRate(44100);
                    // 双通道(立体声)
                    recorder.setAudioChannels(2);
                    // 音频编/解码器
                    recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
                    recorder.start();

                    Frame frame = null;
                    long startTime = 0, videoTS = 0;
                    // frame会自动回收?
                    while (status && (frame = grabber.grabFrame()) != null) {
                        if (startTime == 0) {
                            startTime = System.currentTimeMillis();
                        }
                        videoTS = 1000 * (System.currentTimeMillis() - startTime);
                        recorder.setTimestamp(videoTS);
                        recorder.record(frame);
                        System.out.println(frame.toString());
                    }

                    System.err.println("推流已结束");
                    recorder.stop();
                    grabber.stop();
                } catch (org.bytedeco.javacv.FrameGrabber.Exception | org.bytedeco.javacv.FrameRecorder.Exception e) {
                    System.err.println("触发异常，回收grabber");
                    e.printStackTrace();
                    if (grabber != null) {
                        try {
                            grabber.stop();
                        } catch (org.bytedeco.javacv.FrameGrabber.Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                } finally {
                    if (grabber != null) {
                        try {
                            System.err.println("触发finally模块，回收grabber，frame自動回收无需处理");
                            grabber.stop();
                        } catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        });
        thread.setName("直播转点播" + recorder.toString());
        thread.start();
    }

    public static void main(String[] args) {
        String inputFile = "rtmp://10.10.0.55/live/record1";

        String outputFile = "test.flv";
        frameRecord(inputFile, outputFile, 1);
    }
}
