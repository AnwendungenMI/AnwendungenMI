package de.fh_dortmund.sonicphone.network_frame_provider.nalu;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TobyV2 on 06.12.2015.
 */
public class NalUExtractorImpl implements INalUExtractor
{
    INalUSplitter splitter;
    byte[] previousPartialNalU;
    private int runIdx = 0;
    private long totalBytesOutExpected = 0;
    private long totalBytesOutActual = 0;

    public NalUExtractorImpl(INalUSplitter splitter)
    {
        this.splitter = splitter;
    }

    @Override
    public List<byte[]> extractNalUs(byte[] data, int length)
    {
        runIdx++;
        ArrayList<byte[]> nalUs = new ArrayList<>();
        NalUSplitResult result = splitter.splitBlock(data, length);

        // Handle incomplete NalUs from previous read
        if(previousPartialNalU != null)
        {
            byte[] nalUContinuation;
            boolean secondHalfIncomplete = false;

            if(result.getPartialStartPart() != null) {
                nalUContinuation = result.getPartialStartPart();
                secondHalfIncomplete = result.getCompleteNalUs().size() == 0 && result.getPartialEndPart() == null;
            }
            else if(result.getCompleteNalUs().size() > 0) {
                nalUContinuation = result.getCompleteNalUs().get(0);
                result.getCompleteNalUs().remove(0);
            }
            else
            {
                nalUContinuation = result.getPartialEndPart();
                secondHalfIncomplete = true;
            }

            byte[] completeNalu = new byte[previousPartialNalU.length + nalUContinuation.length];
            System.arraycopy(previousPartialNalU, 0, completeNalu, 0, previousPartialNalU.length);
            System.arraycopy(nalUContinuation, 0, completeNalu, previousPartialNalU.length, nalUContinuation.length);

            int delimiterIdx = Helper.determineDelimiterPosition(completeNalu, 2, completeNalu.length-1);
            // if delimiter was split
            if(delimiterIdx != -1)
            {
                // First NalU can be added to result list
                byte[] firstNalU = new byte[delimiterIdx];
                System.arraycopy(completeNalu, 0, firstNalU, 0, delimiterIdx);
                nalUs.add(firstNalU);

                // Handle second NalU
                byte[] secondNalU = new byte[completeNalu.length-delimiterIdx];
                System.arraycopy(completeNalu, delimiterIdx, secondNalU, 0, completeNalu.length-delimiterIdx);
                if(!secondHalfIncomplete) {
                    nalUs.add(secondNalU);
                    previousPartialNalU = result.getPartialEndPart();
                }
                else
                    previousPartialNalU = secondNalU;
            }
            else if(!secondHalfIncomplete)
            {
                nalUs.add(completeNalu);
                previousPartialNalU = result.getPartialEndPart();;
            }
            else
                previousPartialNalU = completeNalu;
        }
        else {
            previousPartialNalU = result.getPartialEndPart();
        }

        for(byte[] nalU : result.getCompleteNalUs()) {
            nalUs.add(nalU);
        }

        for(byte[] nalU : nalUs) {
            totalBytesOutActual+=nalU.length;
        }

        totalBytesOutExpected+= length;


        long absoluteOut = totalBytesOutActual + (previousPartialNalU != null? previousPartialNalU.length:0);
        if(totalBytesOutExpected != absoluteOut)
        {
            Log.e("NalUExtractorImpl", "Not all bytes were processed! This should not happen. You're probably fucked.");
        }

        return nalUs;
    }
}
