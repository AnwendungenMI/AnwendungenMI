package de.fh_dortmund.sonicphone;


/**
 * Created by JW on 23.11.2015.
 */
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Die MainActivity der Anwendung übernimmt die Darstellung der Bitmaps in der ImageView.
 * Die ImageView wird im Vollbild Modus angezeigt.
 * Die Gliederung ist in 3 Bereiche Unterteilt:
 * 1. Die Decodierung der Bitmaps -> Frame-Stream Verarbeitung (Frame -> Bitmap)
 * 2. Die Anzeige der Bitmaps -> Frame-Daten werden an die ImageView weiter gegeben; Thread ist solange gelockt bis das vorherige Frame vollständig angezeigt wird
 * 3. Das Recylen der Bitmaps -> Nachdem das Frame angezeigt wurde, kann es entfernt und der Speicher freigegeben werden
 */

public class FrameViewerActivity extends Activity {

    private int l = 0;

    private Bitmap mDecodingBitmap;
    private Bitmap mShowingBitmap;
    private Bitmap mRecycledBitmap;

    private final Object lock = new Object();

    private volatile boolean ready = true;

    ArrayList<Bitmap> images = new ArrayList<Bitmap>();
    int position = 0;
    // TODO: Pfad für die Frames muss auf den Decodierer und die Verarbeitung umgestellt werden!
    String imagePath="/storage/emulated/0/SonicPhone/frame-0";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        setContentView(R.layout.frameviewer);



        for( int i = 1; i<9; i++){
            // Angezeigte Bitmaps werden in einem Array verwaltet
            images.add(BitmapFactory.decodeFile(imagePath + i + ".png"));
        }



        final MyImageView imageView = (MyImageView) findViewById(R.id.showImages);
        imageView.setOnDrawFinishedListener(new MyImageView.OnDrawFinishedListener() {

            @Override
            public void onOnDrawFinish() {
                /*
                 * Die Anzeige eines Bitmaps auf der ImageView wurde abegeschlossen
                 * das Bitmap kann anschließend recycled werden und die Verarbeitung des nächsten Bitmaps
                 * kann durchgeführt werden
                 */
                mRecycledBitmap = mShowingBitmap;
                mShowingBitmap = null;
                synchronized (lock) {
                    ready = true;
                    lock.notifyAll();
                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                    Log.d("Timestamp", currentDateTimeString + " Nummer: " + l + " Nummer des Bildes " + position);
                    l++;
                }
            }
        });

        final Button goButton = (Button) findViewById(R.id.show);

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 1;

                            if (mDecodingBitmap != null) {
                                options.inBitmap = mDecodingBitmap;
                            }

                            mDecodingBitmap = images.get(position);

                            // mDecodingBitmap = BitmapFactory.decodeResource(
                            //        getResources(), images.get(position),
                            //       options);

                            /*
                             * Um die Anzeige der Bitmaps der Reihe nach durchzuführen, befindet sich
                             * an dieser Stelle der Synchronized Block.
                             * "Lock" wird nach der Decodierung durchgeführt
                             * Die Bitmap wird an den die ImageView weitergegeben sobald der Thread "ready" ist
                             */
                            synchronized (lock) {
                                while (!ready) {
                                    try {
                                        lock.wait();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                ready = false;
                            }

                            if (mShowingBitmap == null) {
                                mShowingBitmap = mDecodingBitmap;
                                mDecodingBitmap = mRecycledBitmap;
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mShowingBitmap != null) {
                                        imageView
                                                .setImageBitmap(mShowingBitmap);

                                        /*
                                         * An dieser Stelle wurde noch nichts auf die ImageView gezeichnet
                                         * Lediglich die Daten werden an die ImageView übergeben
                                         *
                                         */
                                    }
                                }
                            });

                            try {
                                Thread.sleep(5);
                            } catch (InterruptedException e) {
                            }

                            position++;
                            if (position >= images.size())
                                position = 0;
                        }
                    }
                };
                Thread t = new Thread(runnable);
                t.start();
            }
        });

    }
}