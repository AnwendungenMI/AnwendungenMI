package de.fh_dortmund.sonicphone.network_frame_provider.nalu;

import java.util.List;

/**
 * Created by TobyV2 on 06.12.2015.
 */
public interface INalUExtractor
{
    List<byte[]> extractNalUs(byte[] data, int length);
}
