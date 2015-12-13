package de.fh_dortmund.sonicphone.network_frame_provider.nalu;

import java.util.List;

/**
 * Created by TobyV2 on 05.12.2015.
 */
public class NalUSplitResult
{
    private byte[] partialStartPart;
    private List<byte[]> completeNalUs;
    private byte[] partialEndPart;

    public NalUSplitResult(byte[] partialStartPart, List<byte[]> completeNalUs, byte[] partialEndPart) {
        this.partialStartPart = partialStartPart;
        this.completeNalUs = completeNalUs;
        this.partialEndPart = partialEndPart;
    }

    public byte[] getPartialStartPart() {
        return partialStartPart;
    }

    public void setPartialStartPart(byte[] partialStartPart) {
        this.partialStartPart = partialStartPart;
    }

    public List<byte[]> getCompleteNalUs() {
        return completeNalUs;
    }

    public void setCompleteNalUs(List<byte[]> completeNalUs) {
        this.completeNalUs = completeNalUs;
    }

    public byte[] getPartialEndPart() {
        return partialEndPart;
    }

    public void setPartialEndPart(byte[] partialEndPart) {
        this.partialEndPart = partialEndPart;
    }
}
