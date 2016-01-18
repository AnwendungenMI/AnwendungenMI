package interfaces;

import android.graphics.Bitmap;

/**
 * Verarbeitet ein Rohdatenframe und extrahiert Metainformationen
 */
public interface IFrameProcessor
{
    /**
     * @param rawFrame Das Rohdatenframe
     * @return Das verarbeitete Frame
     */
    Bitmap processFrame(Bitmap rawFrame);
}
