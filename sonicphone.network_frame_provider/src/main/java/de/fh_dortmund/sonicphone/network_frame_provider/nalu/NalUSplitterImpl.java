package de.fh_dortmund.sonicphone.network_frame_provider.nalu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TobyV2 on 05.12.2015.
 */
public class NalUSplitterImpl implements INalUSplitter
{

    @Override
    public NalUSplitResult splitBlock(byte[] block, int length)
    {
        byte[] partialStartPart = extractPartialStartPart(block, length);
        List<byte[]> completeNalUs = new ArrayList<>();
        byte[] partialEndPart = null;

        if(partialStartPart == null || partialStartPart.length < length)
        {
            int bodyStart = partialStartPart == null? 0 : partialStartPart.length;
            int lastNaluStart = extractCompleteNalUs(block, length, bodyStart, completeNalUs);
            partialEndPart = extractPartialEndPart(block, length, lastNaluStart);
        }

        return new NalUSplitResult(partialStartPart, completeNalUs, partialEndPart);
    }

    private byte[] extractPartialStartPart(byte[] block, int length)
    {
        byte[] partialStartPart = null;
        int bodyStartIdx = Helper.determineDelimiterPosition(block, 0, length-1);

        // Incomplete NalU with complete ending (= length less than complete block) at start detected
        if(bodyStartIdx != -1 && bodyStartIdx != 0)
        {
            partialStartPart = new byte[bodyStartIdx];
            System.arraycopy(block, 0, partialStartPart, 0, bodyStartIdx);
        }
        // Incomplete NalU without ending(= length equals block length) at start detected
        else if(bodyStartIdx != 0)
        {
            partialStartPart = new byte[length];
            System.arraycopy(block, 0, partialStartPart, 0, length);
        }

        return partialStartPart;
    }

    private int extractCompleteNalUs(byte[] block, int blockLength, int searchStart, List<byte[]> destination)
    {
        int previousNalUStartIdx = - 1;
        int nalUStartIdx = Helper.determineDelimiterPosition(block, searchStart, blockLength-1);
        while(nalUStartIdx != -1)
        {
            int nextNalUStartIdx = Helper.determineDelimiterPosition(block, nalUStartIdx + 2, blockLength-1);
            if(nextNalUStartIdx != -1)
            {
                int length = nextNalUStartIdx - nalUStartIdx;
                byte[] nalU = new byte[length];
                System.arraycopy(block, nalUStartIdx, nalU, 0, length);
                destination.add(nalU);
            }

            previousNalUStartIdx = nalUStartIdx;
            nalUStartIdx = nextNalUStartIdx;
        }

        return previousNalUStartIdx;
    }

    private byte[] extractPartialEndPart(byte[] block, int blockLength, int searchStart)
    {
        int length = blockLength-searchStart;
        byte[] partialEndPart = new byte[length];
        System.arraycopy(block, searchStart, partialEndPart, 0, length);

        return partialEndPart;
    }
}
