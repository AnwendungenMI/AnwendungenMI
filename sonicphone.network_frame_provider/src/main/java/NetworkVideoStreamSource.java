import interfaces.IVideoStreamSource;

/**
 * Created by TobyV2 on 15.11.2015.
 */
public class NetworkVideoStreamSource implements IVideoStreamSource
{
    @Override
    public int available() {
        return 0;
    }

    @Override
    public byte[] getNextAccessUnit() {
        return new byte[0];
    }
}
