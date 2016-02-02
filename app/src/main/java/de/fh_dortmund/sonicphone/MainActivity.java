package de.fh_dortmund.sonicphone;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.fh_dortmund.sonicphone.network_frame_provider.FileVideoStreamSource;
import de.fh_dortmund.sonicphone.network_frame_provider.H264FrameProvider;
import de.fh_dortmund.sonicphone.network_frame_provider.LZ4NetworkFrameProvider;
import de.fh_dortmund.sonicphone.network_frame_provider.nalu.Helper;
import frameProcessing.FrameProcessorImpl;
import interfaces.IFrameProcessor;
import interfaces.IFrameProvider;
import interfaces.IVideoStreamSource;
import interfaces.ParamRunnable;

public class MainActivity extends Activity {
    long frameStart = -1;
    int frameCount = 0;
    int frameNameIdx = 0;
    ImageView mainImageView;
    IFrameProvider provider;

    private synchronized int getFrameNameIdx()
    {
        return frameNameIdx++;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainImageView = (ImageView)findViewById(R.id.mainImageView);

//        int res = (int) (Math.random() * 10);
//        RandomClass.DivideByZero();
        provider = new LZ4NetworkFrameProvider();
        final IFrameProcessor processor = new FrameProcessorImpl();
        provider.registerFrameAvailableCallback(new ParamRunnable<Bitmap>() {
            @Override
            public void run(final Bitmap param) {
                if(frameStart == -1)
                    frameStart = System.currentTimeMillis();

                long now = System.currentTimeMillis();
                if(now-frameStart > 1000) {
                    Log.i("AnwendungenMI", "FPS: " + frameCount + "(" + frameNameIdx + " total)");
                    frameCount = 0;
                    frameStart = now;
                }
                frameCount++;
//                FileOutputStream out = null;
//
//                try {
//                    String name = "/sdcard/testDataFrames/frame_" +  getFrameNameIdx() + ".yuv";
//                    out = new FileOutputStream(name);
//                    out.write(param);
//                    out.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//
//
//                final Bitmap b = nv12ToGray(param, 1280, 720);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainImageView.setImageBitmap(processor.processFrame(param));
                        mainImageView.invalidate();
                    }
                });
            }
        });

        Button nextActivity = (Button)findViewById(R.id.frameViewer);
        nextActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent myIntent = new Intent(MainActivity.this, neueActivity.class);
//                Intent myIntent = new Intent(view.getContext(), FrameViewerActivity.class);
//                MainActivity.this.startActivity(myIntent);
                provider.connect();
            }

        });

//        IVideoStreamSource streamSource = new FileVideoStreamSource();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
//        provider.close();
        super.onDestroy();
    }

    private Bitmap nv12ToGray(byte[] nv12, int width, int height)
    {
        int grayscaleSize = (int) (nv12.length / 3.0*2.0);
        byte[] grayscale = new byte[grayscaleSize*4];

        for(int i = 0; i < height; i++)
        {
            for(int j = 0; j < width; j++)
            {
                int coeffStart = (i*width+j)*4;
                grayscale[coeffStart] = (byte) 0xFF;
                grayscale[coeffStart+1] = nv12[i*width + j];
                grayscale[coeffStart+2] = nv12[i*width + j];
                grayscale[coeffStart+3] = nv12[i*width + j];
            }
        }

        Bitmap b = Bitmap.createBitmap(1280, 720, Bitmap.Config.ARGB_8888);
        b.copyPixelsFromBuffer(ByteBuffer.wrap(grayscale));
        return b;
    }
}
