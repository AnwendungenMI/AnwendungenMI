package de.fh_dortmund.sonicphone.network_frame_provider;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import interfaces.IFrameProcessor;
import interfaces.IFrameProvider;
import interfaces.IVideoStreamSource;
import interfaces.ParamRunnable;

/**
 * Created by TobyV2 on 15.11.2015.
 */
public class H264FrameProvider implements IFrameProvider
{
    private IVideoStreamSource streamSource;
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final String MIME_TYPE = "video/avc";
    private BlockingQueue<byte[]> frames;
    private int nalUIdx = 2;
    int inputAvailCalls = 0;
    int outputAvailCalls = 0;
    ParamRunnable<byte[]> frameAvailableCallback;
    private MediaCodec codec;
    private FileOutputStream dbgOutFile;

    public H264FrameProvider(IVideoStreamSource streamSource)
    {
        this.streamSource = streamSource;
        frames = new LinkedBlockingQueue<>();
        try {
            dbgOutFile = new FileOutputStream("sdcard/testDataFrames/onlineFile.bin");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        configureCodec();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void configureCodec() {
        try
        {
            streamSource.connect();
//            final List<byte[]> nalUs = new ArrayList<>();
//            while (streamSource.available() > 0)
//            {
//                nalUs.add(streamSource.getNextAccessUnit());
//            }
            codec = MediaCodec.createDecoderByType(MIME_TYPE);
            MediaFormat mOutputFormat; // member variable
            codec.setCallback(new MediaCodec.Callback() {

                @Override
                public void onInputBufferAvailable(MediaCodec codec, int index) {
//                    Log.i("H264FrameProvider", "onInputAvail_start: " + inputAvailCalls++);
                    if(streamSource.available() > 0) {
                        ByteBuffer inputBuffer = codec.getInputBuffer(index);
                        inputBuffer.clear();
                        final byte[] data = streamSource.getNextAccessUnit();
//                    if(nalUIdx < nalUs.size()) {
//                        ByteBuffer inputBuffer = codec.getInputBuffer(index);
//                        inputBuffer.clear();
//                        byte[] data = nalUs.get(nalUIdx++);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    dbgOutFile.write(data);
                                    dbgOutFile.write(new byte[]{(byte) 0xCA, (byte)0xFE, (byte)0xBA, (byte)0xBE});
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                        inputBuffer.put(data);
                        codec.queueInputBuffer(index, 0, data.length, 1, 0);
//                        Log.i("H264FrameProvider", "onInputAvail_success");
                    }
//                    else
//                        Log.i("H264FrameProvider", "onInputAvail_failure");

                }

                @Override
                public void onOutputBufferAvailable(MediaCodec codec, int index, MediaCodec.BufferInfo info) {
//                    Log.i("H264FrameProvider", "onOutputAvail: " + outputAvailCalls++);
                    ByteBuffer outputBuffer = codec.getOutputBuffer(index);
                    outputBuffer.position(info.offset);
                    outputBuffer.limit(info.offset + info.size);
                    final byte[] frameData = new byte[info.size];
                    outputBuffer.get(frameData);
                    codec.releaseOutputBuffer(index, false);
//                    frames.add(frameData);
                    if(frameAvailableCallback != null) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                frameAvailableCallback.run(frameData);
                            }
                        }).start();
                    }
                }

                @Override
                public void onError(MediaCodec codec, MediaCodec.CodecException e) {
                    int a = 10;
                }

                @Override
                public void onOutputFormatChanged(MediaCodec codec, MediaFormat format) {
                    int a = 10;
                }
            });

            MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, WIDTH, HEIGHT);

            byte[] sps = {  (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x67, (byte)0x64, (byte)0x00, (byte)0x1F,
                            (byte)0xAC, (byte)0xD9, (byte)0x40, (byte)0x50, (byte)0x05, (byte)0xBA, (byte)0x10, (byte)0x00,
                            (byte)0x00, (byte)0x03, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x01,
                            (byte)0xE0, (byte)0xF1, (byte)0x83, (byte)0x19, (byte)0x60};

            byte[] pps = {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x68, (byte)0xEB, (byte)0xEC, (byte)0xB2, (byte)0x2C};
//            while(streamSource.available() < 2);
//            byte[] sps = streamSource.getNextAccessUnit();
//            byte[] pps = streamSource.getNextAccessUnit();
            format.setByteBuffer("csd-0", ByteBuffer.wrap(sps));
            format.setByteBuffer("csd-1", ByteBuffer.wrap(pps));
            codec.configure(format, null, null, 0);
            codec.start();
        }
        catch (Exception e) {
            int a = 10;
        }
    }

    @Override
    public int available() {
        return frames.size();
    }

    @Override
    public void registerFrameAvailableCallback(ParamRunnable<byte[]> callback) {
        this.frameAvailableCallback = callback;
    }

    @Override
    public void close() {
        codec.release();
        try {
            dbgOutFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
