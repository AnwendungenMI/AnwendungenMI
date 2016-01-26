package frameProcessing;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Mogi on 15.11.2015.
 */
public class FrameProcessorImplTest {


    public void startProcesssing() throws FileNotFoundException{
        //byte[] nv12Bytes = loadNv12File();
        Bitmap bitmap = loadRGBFile();

        FrameProcessorImpl frameProcessor = new FrameProcessorImpl();

        //Bitmap bmp = frameProcessor.processFrameOld(nv12Bytes);
        Bitmap output = frameProcessor.processFrame(bitmap);
        frameProcessor.clear();
    }

    public Bitmap loadBMPFile() throws FileNotFoundException {
        Bitmap bitmap = null;

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        path = path + "/" + "source3.jpg";
        //path = path + "/" + "testfileRGB.bin";

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        bitmap = BitmapFactory.decodeFile(path, options);

        return bitmap;
    }

    public byte[] loadNv12File () throws FileNotFoundException{

        byte[] bytes = null;

        File nv12;

        File sdDir = Environment.getExternalStorageDirectory();

        nv12 = new File(sdDir, "frame_20.yuv");

        if(nv12.exists()) {

            FileInputStream fis = new FileInputStream(nv12);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            try {
                for (int readNum; (readNum = fis.read(buf)) != -1;) {
                    bos.write(buf, 0, readNum); //no doubt here is 0
                    //Writes len bytes from the specified byte array starting at offset off to this byte array output stream.
                    System.out.println("read " + readNum + " bytes,");
                }
            } catch (IOException ex) {

            }

            bytes = bos.toByteArray();

        }


        return bytes;
    }

    public Bitmap loadRGBFile() throws FileNotFoundException{

        Bitmap b = Bitmap.createBitmap(1024, 768, Bitmap.Config.RGB_565);

        byte[] bytes = null;

        File rgb;

        File sdDir = Environment.getExternalStorageDirectory();

        rgb = new File(sdDir, "testfileRGB.bin");

        if(rgb.exists()) {

            FileInputStream fis = new FileInputStream(rgb);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024*768*2];
            try {
                for (int readNum; (readNum = fis.read(buf)) != -1;) {
                    bos.write(buf, 0, readNum); //no doubt here is 0
                    //Writes len bytes from the specified byte array starting at offset off to this byte array output stream.
                    System.out.println("read " + readNum + " bytes,");
                }
            } catch (IOException ex) {

            }

            bytes = bos.toByteArray();

        }

        b.copyPixelsFromBuffer(ByteBuffer.wrap(bytes));

        return b;
    }
}
