package de.fh_dortmund.sonicphone.network_frame_provider;

import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import interfaces.IVideoStreamSource;
import de.fh_dortmund.sonicphone.network_frame_provider.nalu.INalUExtractor;
import de.fh_dortmund.sonicphone.network_frame_provider.nalu.INalUSplitter;
import de.fh_dortmund.sonicphone.network_frame_provider.nalu.NalUExtractorImpl;
import de.fh_dortmund.sonicphone.network_frame_provider.nalu.NalUSplitterImpl;

/**
 * Created by TobyV2 on 05.12.2015.
 */
public class FileVideoStreamSource implements IVideoStreamSource {
    private BlockingQueue<byte[]> accessUnits;
    private INalUExtractor extractor;
    private INalUSplitter splitter;
    boolean threadRunning = false;
    private Object fileThreadLock = new Object();

    public FileVideoStreamSource()
    {
        accessUnits = new LinkedBlockingQueue<>();
        splitter = new NalUSplitterImpl();
        extractor = new NalUExtractorImpl(splitter);
    }

    public void connect()
    {
        threadRunning = true;
        new FileReadThread().start();
    }

    @Override
    public int available() {
        while (accessUnits.size() == 0 && threadRunning)
            try {
                synchronized (fileThreadLock)
                {
                    fileThreadLock.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        return accessUnits.size();
    }

    @Override
    public byte[] getNextAccessUnit() {
        try {
            return accessUnits.take();
        } catch (InterruptedException e) {
            Log.e("FileVideoStreamSource", e.getMessage());
        }

        return null;
    }

    private class FileReadThread extends Thread
    {

        @Override
        public void run() {
            long totalBytesRead = 0;
            try
            {
                FileOutputStream out = new FileOutputStream("/sdcard/testDataFrames/rawInput.264");
                FileOutputStream out2 = new FileOutputStream("/sdcard/testDataFrames/inputBlocks.264");
//                InputStream stream = new FileInputStream("/sdcard/testDataFrames/rawInput.264");
                Socket s = new Socket("192.168.0.108", 42000);
                InputStream stream = s.getInputStream();
                byte[] buffer = new byte[1000];
                int bytesRead = stream.read(buffer);
                while(bytesRead > 0 && bytesRead != -1)
                {
                    out.write(buffer, 0, bytesRead);
                    out2.write(buffer, 0, bytesRead);
                    out2.write(new byte[]{(byte) 0xCA, (byte)0xFE, (byte)0xBA, (byte)0xBE});
                    totalBytesRead += bytesRead;
//                    Log.i("FileVideoStreamSource", "Total bytes read via network: " + totalBytesRead);
                    List<byte[]> nalUs = extractor.extractNalUs(buffer, bytesRead);
                    for(byte[] nalU : nalUs) {
                        accessUnits.add(nalU);
                        synchronized (fileThreadLock)
                        {
                            fileThreadLock.notifyAll();
                        }
                    }

                    bytesRead = stream.read(buffer);
                }

                s.close();
                out.close();
                out2.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally {
                threadRunning = false;
                synchronized (fileThreadLock)
                {
                    fileThreadLock.notifyAll();
                }
            }
        }


    }
}
