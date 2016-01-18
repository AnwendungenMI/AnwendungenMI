package de.fh_dortmund.sonicphone.network_frame_provider;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fh_dortmund.sonicphone.network_frame_provider.nalu.INalUExtractor;
import de.fh_dortmund.sonicphone.network_frame_provider.nalu.INalUSplitter;
import de.fh_dortmund.sonicphone.network_frame_provider.nalu.NalUExtractorImpl;
import de.fh_dortmund.sonicphone.network_frame_provider.nalu.NalUSplitResult;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by TobyV2 on 07.12.2015.
 */
public class NalUExtractorTest
{
    private static byte[] threeByteDelimiterNalU;
    private static byte[] fourByteDelimiterNalU;
    private static byte[] halfNalUStart;
    private static byte[] halfNalUEnd;

    private static NalUSplitResult completeNalUs;
    private static byte[] completeNalUsByte;
    private static NalUSplitResult completeBodyHalfEnd;
    private static byte[] completeBodyHalfEndByte;
    private static NalUSplitResult halfStartCompleteBody;
    private static byte[] halfStartCompleteBodyByte;
    private static NalUSplitResult completeBodyPartialDelimiterAtEnd;
    private static byte[] completeBodyPartialDelimiterAtEndByte;
    private static NalUSplitResult partialDelimiterAtStartCompleteBody;
    private static byte[] partialDelimiterAtStartCompleteBodyByte;

    @BeforeClass
    public static void initializeTests() {
        // Create four byte delimiter NalU
        threeByteDelimiterNalU = new byte[100];
        System.arraycopy(new byte[]{0x00, 0x00, 0x01}, 0, threeByteDelimiterNalU, 0, 3);
        for (int i = 3; i < 100; i++)
            threeByteDelimiterNalU[i] = (byte) 0xAB;

        // Create five byte delimiter NalU
        fourByteDelimiterNalU = new byte[100];
        System.arraycopy(new byte[]{0x00, 0x00, 0x00, 0x01}, 0, fourByteDelimiterNalU, 0, 4);
        for (int i = 4; i < 100; i++)
            fourByteDelimiterNalU[i] = (byte) 0xCD;

        // Create partial NalU containing first half
        halfNalUStart = new byte[50];
        System.arraycopy(fourByteDelimiterNalU, 0, halfNalUStart, 0, 50);

        // Create partial NalU containing second half
        halfNalUEnd = new byte[50];
        System.arraycopy(fourByteDelimiterNalU, 50, halfNalUEnd, 0, 50);

        // Create NalUSplitResult containing complete NalUs (body & partial end part)
        List<byte[]> nalUBody = new ArrayList<>();
        completeNalUsByte = new byte[1000];

        for (int i = 0; i < 9; i++) {
            byte[] nalU = i % 2 == 0 ? threeByteDelimiterNalU : fourByteDelimiterNalU;
            nalUBody.add(nalU);
            System.arraycopy(nalU, 0, completeNalUsByte, i*100, 100);
        }
        System.arraycopy(fourByteDelimiterNalU, 0, completeNalUsByte, 900, 100);
        completeNalUs = new NalUSplitResult(null, nalUBody, fourByteDelimiterNalU);

        // Create NaluSpitResult containing complete body and half end part
        List<byte[]> nalUBody2 = new ArrayList<>();
        completeBodyHalfEnd = new NalUSplitResult(null, nalUBody2, halfNalUStart);
        completeBodyHalfEndByte = new byte[1100];
        for (int i = 0; i < 10; i++) {
            byte[] nalU = i % 2 == 0 ? threeByteDelimiterNalU : fourByteDelimiterNalU;
            nalUBody2.add(nalU);
            System.arraycopy(nalU, 0, completeBodyHalfEndByte, i*100, 100);
        }
        System.arraycopy(halfNalUStart, 0, completeBodyHalfEndByte, 1000, 50);

        // Create NaluSpitResult containing complete body and half start part
        halfStartCompleteBody = new NalUSplitResult(halfNalUEnd, nalUBody, fourByteDelimiterNalU);
        halfStartCompleteBodyByte = new byte[1100];
        System.arraycopy(halfNalUEnd, 0, halfStartCompleteBodyByte, 0, 50);
        for (int i = 0; i < 10; i++) {
            byte[] nalU = i % 2 == 0 ? threeByteDelimiterNalU : fourByteDelimiterNalU;
            System.arraycopy(nalU, 0, halfStartCompleteBodyByte, i*100+50, 100);
        }

        // Create NalUSplitResult containing complete body and partial end part with part of the delimiter at last position
        byte[] partialStartDelimiter = new byte[101];
        System.arraycopy(threeByteDelimiterNalU, 0, partialStartDelimiter, 0, 100);
        partialStartDelimiter[100] = (byte) 0x00;
        completeBodyPartialDelimiterAtEnd = new NalUSplitResult(null, nalUBody2, partialStartDelimiter);
        completeBodyPartialDelimiterAtEndByte = new byte[1200];
        for (int i = 0; i < 10; i++) {
            byte[] nalU = i % 2 == 0 ? threeByteDelimiterNalU : fourByteDelimiterNalU;
            System.arraycopy(nalU, 0, completeBodyPartialDelimiterAtEndByte, i*100, 100);
        }
        System.arraycopy(partialStartDelimiter, 0, completeBodyPartialDelimiterAtEndByte, 1000, 101);

        // Create NalUSplitResult containing complete body and partial start part with incomplete delimiter at start
        byte[] partialEndDelimiter = new byte[99];
        System.arraycopy(fourByteDelimiterNalU, 1, partialEndDelimiter, 0, 99);
        partialDelimiterAtStartCompleteBody = new NalUSplitResult(partialEndDelimiter, nalUBody, fourByteDelimiterNalU);
        completeBodyPartialDelimiterAtEndByte = new byte[1100];
        System.arraycopy(partialEndDelimiter, 0, completeBodyPartialDelimiterAtEndByte, 0, 99);
        for (int i = 0; i < 10; i++) {
            byte[] nalU = i % 2 == 0 ? threeByteDelimiterNalU : fourByteDelimiterNalU;
            System.arraycopy(nalU, 0, completeBodyPartialDelimiterAtEndByte, i*100+99, 100);
        }
    }

    @Test
    public void completeNalUsShouldExtract9NalUs()
    {
        INalUSplitter splitterMock = mock(INalUSplitter.class);
        when(splitterMock.splitBlock(completeNalUsByte, 1000)).thenReturn(completeNalUs);

        INalUExtractor extractor = new NalUExtractorImpl(splitterMock);
        List<byte[]> extractedNalUs = extractor.extractNalUs(completeNalUsByte, 1000);

        // Expect the method to retrieve 9 NalUs, 10th unit is kept as "partial end part" until next block is passed
        assertThat("Wrong number of NalUs extracted!", extractedNalUs.size(), is(9));
    }

    @Test
    public void partialNalUsShouldBeReconstructedProperly()
    {
        INalUSplitter splitterMock = mock(INalUSplitter.class);
        when(splitterMock.splitBlock(completeBodyHalfEndByte, 1050)).thenReturn(completeBodyHalfEnd);
        when(splitterMock.splitBlock(halfStartCompleteBodyByte, 1050)).thenReturn(halfStartCompleteBody);

        INalUExtractor extractor = new NalUExtractorImpl(splitterMock);
        List<byte[]> firstExtraction = extractor.extractNalUs(completeBodyHalfEndByte, 1050);
        List<byte[]> secondExtraction = extractor.extractNalUs(halfStartCompleteBodyByte, 1050);

        // Expect the first extraction to retrieve 10 units
        assertThat("Wrong number of units on first extraction!", firstExtraction.size(), is(10));

        // Expect the second extraction to retrieve 10 units
        assertThat("Wrong number of units on second extraction!", secondExtraction.size(), is(10));

        // Expect the first unit of second extraction 10 be four byte delimiter NalU
        assertThat("Wrong NalU reconstructed from partials!", Arrays.equals(secondExtraction.get(0), fourByteDelimiterNalU), is(true));
    }

    @Test
    public void separatedDelimitersShouldBeParsedCorrectly()
    {
        INalUSplitter splitterMock = mock(INalUSplitter.class);
        when(splitterMock.splitBlock(completeBodyPartialDelimiterAtEndByte, 1101)).thenReturn(completeBodyPartialDelimiterAtEnd);
        when(splitterMock.splitBlock(partialDelimiterAtStartCompleteBodyByte, 1099)).thenReturn(partialDelimiterAtStartCompleteBody);

        INalUExtractor extractor = new NalUExtractorImpl(splitterMock);
        List<byte[]> firstExtraction = extractor.extractNalUs(completeBodyPartialDelimiterAtEndByte, 1101);
        List<byte[]> secondExtraction = extractor.extractNalUs(partialDelimiterAtStartCompleteBodyByte, 1099);

        // Expect the first extraction to retrieve 10 units
        assertThat("Wrong number of units on first extraction!", firstExtraction.size(), is(10));

        // Expect the second extraction to retrieve 10 units
        assertThat("Wrong number of units on second extraction!", secondExtraction.size(), is(11));

        // Expect the first unit of second extraction to be three byte delimiter NalU
        assertThat("Wrong NalU reconstructed from partials!", Arrays.equals(secondExtraction.get(0), threeByteDelimiterNalU), is(true));

        // Expect the second unit of second extraction to be four byte delimiter NalU
        assertThat("Wrong NalU reconstructed from partials!", Arrays.equals(secondExtraction.get(1), fourByteDelimiterNalU), is(true));
    }
}
