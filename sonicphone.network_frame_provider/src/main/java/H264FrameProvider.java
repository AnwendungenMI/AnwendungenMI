import interfaces.IFrameProcessor;
import interfaces.IFrameProvider;
import interfaces.IVideoStreamSource;

/**
 * Created by TobyV2 on 15.11.2015.
 */
public class H264FrameProvider implements IFrameProvider
{
    private IVideoStreamSource streamSource;

    public H264FrameProvider(IVideoStreamSource streamSource)
    {
        this.streamSource = streamSource;
    }

    @Override
    public int available() {
        return 0;
    }

    @Override
    public byte[] getNextFrame() {
        return new byte[0];
    }
}
