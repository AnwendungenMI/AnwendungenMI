package de.fh_dortmund.sonicphone.network_frame_provider.nalu;

/**
 * Created by TobyV2 on 05.12.2015.
 */
public interface INalUSplitter {
    NalUSplitResult splitBlock(byte[] block, int length);
}
