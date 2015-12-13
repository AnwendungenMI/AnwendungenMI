package de.fh_dortmund.sonicphone.network_frame_provider.nalu;

public class Helper {
    /**
     * Search the data byte array for the first occurrence 
     * of the byte array pattern.
     */
    public static int indexOf(byte[] data, byte[] pattern, int startIdx, int endIdx) {
        int[] failure = computeFailure(pattern);

        int j = 0;

        for (int i = startIdx; i <= endIdx; i++) {
            while (j > 0 && pattern[j] != data[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == data[i]) {
                j++;
            }
            if (j == pattern.length) {
                return i - pattern.length + 1;
            }
        }
        return -1;
    }

    public static int determineDelimiterPosition(byte[] data, int startIdx, int endIdx)
    {
        byte[] nalDelimiter = {0x00, 0x00, 0x01};
        int position = Helper.indexOf(data, nalDelimiter, startIdx, endIdx);
        if(position > 0 && data[position - 1] == 0x00)
            position--;

        return position;
    }

    /**
     * Computes the failure function using a boot-strapping process,
     * where the pattern is matched against itself.
     */
    private static int[] computeFailure(byte[] pattern) {
        int[] failure = new int[pattern.length];

        int j = 0;
        for (int i = 1; i < pattern.length; i++) {
            while (j>0 && pattern[j] != pattern[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == pattern[i]) {
                j++;
            }
            failure[i] = j;
        }

        return failure;
    }
}