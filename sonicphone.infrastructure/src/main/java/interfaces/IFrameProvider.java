package interfaces;

import android.graphics.Bitmap;

/**
 * Schnittstelle, welche decodierte Videoframes zur Verfügung stellt.
 */
public interface IFrameProvider
{
    /**
     * Ermittelt die Anzahl der verfügbaren Frames
     * @return Anzahl der verfügbaren Frames
     */
    int available();

    /**
     * @return Das nächste verfügbare Frame im nv12 Format (yuv420sp)
     */
    void registerFrameAvailableCallback(ParamRunnable<Bitmap> callback);

    void close();

    void connect();
}
