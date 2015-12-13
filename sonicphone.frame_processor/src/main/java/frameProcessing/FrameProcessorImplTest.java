package frameProcessing;

import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Mogi on 15.11.2015.
 */
public class FrameProcessorImplTest {


    public void startProcesssing() throws FileNotFoundException{
        byte[] nv12Bytes = loadNv12File();

        FrameProcessorImpl frameProcessor = new FrameProcessorImpl();

        Bitmap bmp = frameProcessor.processFrame(nv12Bytes);
    }

    public byte[] loadNv12File () throws FileNotFoundException{

        byte[] bytes = null;

        File nv12 = null;

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
}
