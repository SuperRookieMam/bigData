package com.yhl.test.test;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;

public class Abc {

	public static void main(String[] args) {
		String url = "rtsp://admin:dhs123456@htdhs.gnway.cc:30001/ch33/main/av_stream";
		try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(url);
				FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(
						"C:/Program Files (ext)/nginx-1.16.0/html/test.m3u8", 1);) {
			grabber.setOption("rtsp_transport", "tcp");
			grabber.start();
			recorder.setImageHeight(grabber.getImageHeight());
			recorder.setImageWidth(grabber.getImageWidth());
			recorder.setOption("hls_wrap", "5");
			recorder.setOption("hls_time", "10");
			recorder.setOption("start_number", "0");
			recorder.start(grabber.getFormatContext());
			avcodec.AVPacket packet = null;
			while ((packet = grabber.grabPacket()) != null) {
				recorder.recordPacket(packet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
