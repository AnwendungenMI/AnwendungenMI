package de.fh_dortmund.sonicphone.network_frame_provider;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

import interfaces.IFrameProvider;
import interfaces.ParamRunnable;

/**
 * Created by TobyV2 on 02.02.2016.
 */
public class LZ4NetworkFrameProvider implements IFrameProvider
{
    private ParamRunnable<Bitmap> callback;
    LZ4Factory factory = LZ4Factory.safeInstance();
    LZ4FastDecompressor decompressor = factory.fastDecompressor();
    int width = 1024;
    int height = 768;

    @Override
    public int available() {
        return 0;
    }

    @Override
    public void registerFrameAvailableCallback(ParamRunnable<Bitmap> callback)
    {
        this.callback = callback;
    }

    @Override
    public void close() {

    }

    @Override
    public void connect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    byte[] countBytes = new byte[4];
                    byte[] dataBytes = new byte[500000];
                    byte[] decompressedData = new byte[width*height*2];

                    int dataSize = 0;
                    Socket s = new Socket(InetAddress.getByName("172.22.16.180"), 42000);
//                    Socket s = new Socket(InetAddress.getByName("192.168.0.113"), 42000);
                    InputStream in = s.getInputStream();

                    while(true)
                    {
                        dataSize = 0;

                        int bytesRead = in.read(countBytes, 0, 4);
                        int count = fromByte(countBytes);

                        while(dataSize < count && bytesRead != -1) {
                            bytesRead = in.read(dataBytes, dataSize, count - dataSize);
                            dataSize += bytesRead;
                        }

                        int res = decompressor.decompress(dataBytes, decompressedData, decompressedData.length);
                        final Bitmap b = getBitmapFromData(decompressedData, width, height);
                        if(callback != null)
                            callback.run(b);
                    }

//                    Log.i("MainActivity", "Duration: " + (end-start) + "mS");
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    Log.e("LZ4NetworkFrameProvider", "Error processing frame raw data: " + e.getMessage());
                }
            }
        }).start();
    }

    private Bitmap getBitmapFromData(byte[] decompressedData, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        b.copyPixelsFromBuffer(ByteBuffer.wrap(decompressedData));
        return b;
    }


    private int fromByte(byte[] data)
    {
        int value = 0;
        for(int i = 0; i < 4; i++)
            value |= (data[i] & 0xFF) << (8*i);
        return value;
    }
}
