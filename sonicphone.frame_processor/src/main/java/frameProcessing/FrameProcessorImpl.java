package frameProcessing;

/**
 * Created by Mogi on 15.11.2015.
 */

import interfaces.IFrameProcessor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

public class FrameProcessorImpl implements IFrameProcessor {

    private static Logger log = Logger.getLogger("FrameProcessorImpl");
    int idx = 0;

    //private int width = 1280;

    //private int height = 720;

    private Bitmap source = null; // 1024*768
    private Bitmap finalFrame = null; // 1024*768
    private String format = "rgb565";

    public FrameProcessorImpl() {

    }

    public Bitmap processFrame(Bitmap source) {
        this.source = source;

        if(isReady()) {
            this.finalFrame = buildFinalFrame();
            //this.source;
        }

//        if(this.finalFrame != null) {
//            writeToSDCard(source);
//        }

        return this.finalFrame;
    }

    public void clear() {
        this.source = null;
        this.finalFrame = null;
    }

    public boolean isReady() {
        boolean isReady = false;

        int pixel = source.getPixel(30, 600);
        if(pixel == Color.BLACK) {
            isReady = true;
        }

        return isReady;
        // TODO: Return real value!
//        return true;
    }


    public void writeToSDCard(Bitmap bitmap) {

        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut = null;
        File file = new File(path, "rgbfile_rotate" + idx++ + ".jpg"); // the File to save to
        try {
            fOut = new FileOutputStream(file);
            // source.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    public Bitmap processFrameOld(byte[] rawFrame) {

        int[] pixels = convertNv12ToBmp(rawFrame, width, height);
        source = createBitmap(pixels, width, height);

        source.getPixel(100, 100);

        ultraSonicScan = extractUltrasonicScan();

        return null;
    }
    */

    private Bitmap createBitmap(int[] pixels, int width, int height) {
        Bitmap bm = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
        return bm;
    }

    /*
    private static int[] convertNv12ToBmp(byte[] rawFrame, int width, int height) {
        int size = width*height;
        int offset = size;
        int[] pixels = new int[size];
        int u, v, y1, y2, y3, y4;


        for(int i=0, k=0; i < size; i+=2, k+=2) {
            y1 = rawFrame[i  ]&0xff;
            y2 = rawFrame[i+1]&0xff;
            y3 = rawFrame[width+i  ]&0xff;
            y4 = rawFrame[width+i+1]&0xff;

            u = rawFrame[offset+k  ]&0xff;
            v = rawFrame[offset+k+1]&0xff;
            u = u-128;
            v = v-128;

            pixels[i  ] = convertYUVtoRGB(y1, u, v);
            pixels[i+1] = convertYUVtoRGB(y2, u, v);
            pixels[width+i  ] = convertYUVtoRGB(y3, u, v);
            pixels[width+i+1] = convertYUVtoRGB(y4, u, v);

            if (i!=0 && (i+2)%width==0)
                i+=width;

            log.info("i: " + i);
        }

        return pixels;
    }
    */

    /*
    private static int convertYUVtoRGB(int y, int u, int v) {
        int r,g,b;


        r = y + (int)1.402f*v;
        g = y - (int)(0.344f*u +0.714f*v);
        b = y + (int)1.772f*u;
        r = r>255? 255 : r<0 ? 0 : r;
        g = g>255? 255 : g<0 ? 0 : g;
        b = b>255? 255 : b<0 ? 0 : b;

        return 0xff000000 | (r<<16) | (g<<8) | b;
    }
    */


    private Bitmap extractUltrasonicScan () {
        Rect UltrasonicScanRegion = new Rect(298, 60, 691, 507);
        Bitmap UltrasonicScan = Bitmap.createBitmap(source, 298, 60, UltrasonicScanRegion.width(), UltrasonicScanRegion.height());
        // oder mit "drawBitmap"?

        return UltrasonicScan;
    }

    private Bitmap extractColorScale () {
        Rect ColorScaleRegion = new Rect(978, 60, 1023, 245);
        Bitmap ColorScale = Bitmap.createBitmap(source, 978, 60, ColorScaleRegion.width(), ColorScaleRegion.height());

        return ColorScale;
    }

    private Bitmap extractValues() {
        Rect ValueRegion = new Rect(0, 60, 160, 410);
        Bitmap Values = Bitmap.createBitmap(source, 0, 60, ValueRegion.width(), ValueRegion.height());

        return Values;
    }

    private Bitmap buildFinalFrame() {

        Bitmap firstImage = extractUltrasonicScan();
        Bitmap secondImage = extractColorScale();
        Bitmap thirdImage = extractValues();

        Matrix matrix = new Matrix();
        matrix.postRotate(270);

        Matrix m180 = new Matrix();
        m180.postRotate(90);

        Bitmap rotColorScale = Bitmap.createBitmap(secondImage, 0, 0, secondImage.getWidth(), secondImage.getHeight(), m180, true);
        Bitmap rotUltraSonicScan = Bitmap.createBitmap(firstImage, 0, 0, firstImage.getWidth(), firstImage.getHeight(), matrix, true);
        Bitmap rotValues = Bitmap.createBitmap(thirdImage, 0, 0, thirdImage.getWidth(), thirdImage.getHeight(), m180, true);

        Bitmap result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), source.getConfig());
        Canvas canvas = new Canvas(result);

//        Rect UltrasonicScanRegion = new Rect(0, 0, 490, 386);
        Rect UltrasonicScanRegion = new Rect(0, 0, rotUltraSonicScan.getWidth(), rotUltraSonicScan.getHeight());
        Rect scaledUltrasonicRect = new Rect(0, 0, 873, 768);
//        this.writeToSDCard(rotUltraSonicScan);
        //Rect ValueRegion = new Rect(0, 0, 0, 0);
        //Rect scaledValueRect = new Rect(300, 630, 0, 0);

        //canvas.drawBitmap(rotUltraSonicScan, 0, 0, null);
        canvas.drawBitmap(rotUltraSonicScan, UltrasonicScanRegion, scaledUltrasonicRect, null);
        //canvas.drawBitmap(secondImage, 900, 50, null);
        canvas.drawBitmap(rotColorScale, 630, 50, null);
        canvas.drawBitmap(rotValues, 630, 600, null);

//        Matrix finalRotation = new Matrix();
//        finalRotation.postRotate(-90);
//        Bitmap finalBitmap = Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), finalRotation, true);
//        result.recycle();
//        result = null;
//        rotColorScale.recycle();
//        rotColorScale = null;
//        rotUltraSonicScan.recycle();
//        rotUltraSonicScan = null;
//        rotValues.recycle();
//        rotValues = null;
//        firstImage.recycle();
//        firstImage = null;
//        secondImage.recycle();
//        secondImage = null;
//        thirdImage.recycle();
//        thirdImage = null;

        return result;
    }



}
