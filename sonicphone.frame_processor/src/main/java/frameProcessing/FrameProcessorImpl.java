package frameProcessing;

/**
 * Created by Mogi on 15.11.2015.
 */

import interfaces.IFrameProcessor;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;

import java.nio.ByteBuffer;

public class FrameProcessorImpl implements IFrameProcessor {

    // Breite des finalen Bitmap-Objekts
    private int width = 1024;

    // Hoehe des finalen Bitmap-Objekts
    private int height = 768;

    private Bitmap source = null;

    private Bitmap finalFrame = null;

    public FrameProcessorImpl() {

    }

    public Bitmap processFrame (byte[] rawFrame) {

        int[] pixels = convertNv12ToBmp(rawFrame, width, height);
        source = createBitmap(pixels, width, height);

        return null;
    }

    private Bitmap createBitmap(int[] pixels, int width, int height) {
        Bitmap bm = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
        return bm;
    }

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
        }

        return pixels;
    }

    private static int convertYUVtoRGB(int y, int u, int v) {
        int r,g,b;

        r = y + (int)1.402f*v;
        g = y - (int)(0.344f*u +0.714f*v);
        b = y + (int)1.772f*u;
        r = r>255? 255 : r<0 ? 0 : r;
        g = g>255? 255 : g<0 ? 0 : g;
        b = b>255? 255 : b<0 ? 0 : b;
        return 0xff000000 | (b<<16) | (g<<8) | r;
    }

    private byte[][] extractRelevantParts () {

        Bitmap UltrasonicScan = extractUltrasonicScan();
        Bitmap ColorScale = extractColorScale();





        return null;
    }

    private Bitmap extractUltrasonicScan () {
        Rect UltrasonicScanRegion = new Rect(173, 58, 800, 540);
        Bitmap UltrasonicScan = Bitmap.createBitmap(source, 173, 58, UltrasonicScanRegion.width(), UltrasonicScanRegion.height());
        // oder mit "drawBitmap"?

        return UltrasonicScan;
    }

    private Bitmap extractColorScale () {
        Rect ColorScaleRegion = new Rect(1, 1, 1, 1);
        Bitmap ColorScale = Bitmap.createBitmap(source, 1, 1, ColorScaleRegion.width(), ColorScaleRegion.height());

        return ColorScale;
    }

    private void buildFinalFrame() {

    }
}
