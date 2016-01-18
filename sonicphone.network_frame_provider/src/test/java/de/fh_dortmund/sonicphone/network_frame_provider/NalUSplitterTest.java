package de.fh_dortmund.sonicphone.network_frame_provider;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

import de.fh_dortmund.sonicphone.network_frame_provider.nalu.INalUSplitter;
import de.fh_dortmund.sonicphone.network_frame_provider.nalu.NalUSplitResult;
import de.fh_dortmund.sonicphone.network_frame_provider.nalu.NalUSplitterImpl;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isNull;

/**
 * Created by TobyV2 on 06.12.2015.
 */
public class NalUSplitterTest
{
    private static INalUSplitter nalUSplitter;
    private static byte[] threeByteDelimiterNalU;
    private static byte[] fourByteDelimiterNalU;
    private static byte[] incompleteNalU;
    private static byte[] completeHeadCompleteBody;
    private static byte[] incompleteHeadCompleteBody;
    private static byte[] completeHeadNoBody;
    private static byte[] incompleteHeadNoBody;

    @BeforeClass
    public static void initializeTests()
    {
        // Create test object
        nalUSplitter = new NalUSplitterImpl();

        // Create four byte delimiter NalU
        threeByteDelimiterNalU = new byte[100];
        System.arraycopy(new byte[]{0x00, 0x00, 0x01}, 0, threeByteDelimiterNalU, 0, 3);
        for(int i = 3; i < 100; i++)
            threeByteDelimiterNalU[i] = (byte)0xAB;

        // Create five byte delimiter NalU
        fourByteDelimiterNalU = new byte[100];
        System.arraycopy(new byte[]{0x00, 0x00, 0x00, 0x01}, 0, fourByteDelimiterNalU, 0, 4);
        for(int i = 4; i < 100; i++)
            fourByteDelimiterNalU[i] = (byte)0xCD;

        // Create incomplete NalU
        incompleteNalU = new byte[50];
        for(int i = 0; i < 50; i++)
            incompleteNalU[i] = (byte)0xEF;

        // Create block with only complete NalUs
        completeHeadCompleteBody = new byte[1000];
        for(int i = 0; i < 10; i++)
        {
            byte[] nalU = i %2==0? threeByteDelimiterNalU : fourByteDelimiterNalU;
            System.arraycopy(nalU, 0, completeHeadCompleteBody, i*100, 100);
        }

        // Create block with incomplete head and complete body
        incompleteHeadCompleteBody = new byte[1000];
        System.arraycopy(incompleteNalU, 0, incompleteHeadCompleteBody, 0, 50);
        for(int i = 0; i < 9; i++)
        {
            byte[] nalU = i %2==0? threeByteDelimiterNalU : fourByteDelimiterNalU;
            System.arraycopy(nalU, 0, incompleteHeadCompleteBody, i*100 + 50, 100);
        }

        // Create block with complete head and no body
        completeHeadNoBody = new byte[1000];
        System.arraycopy(threeByteDelimiterNalU, 0, completeHeadNoBody, 0, 100);

        // Create block with incomplete head and no body
        incompleteHeadNoBody = new byte[1000];
        System.arraycopy(incompleteNalU, 0, incompleteHeadNoBody, 0, 50);
    }

    @Test
    public void splittingCompleteHeadCompleteBodyBlockShouldGet9CompleteAnd1PartialEndPart()
    {
        NalUSplitResult result = nalUSplitter.splitBlock(completeHeadCompleteBody, 1000);

        // No partial start part expected
        assertNull("Partial start part existed unexpectedly!", result.getPartialStartPart());

        // Valid list with 9 complete NalUs expected
        assertNotNull("List with complete parts was null!", result.getCompleteNalUs());
        assertThat("List had invalid size!", result.getCompleteNalUs().size(), is(9));

        // Valid partial end part expected
        assertNotNull("Partial end part was null!", result.getPartialEndPart());
        assertThat("Partial end part was not parsed correctly!", Arrays.equals(result.getPartialEndPart(), fourByteDelimiterNalU), is(true));
    }

    @Test
    public void splittingIncompleteHeadWithCompleteBodyShouldGet1PartialStart8CompleteAnd1PartialEndPart()
    {
        NalUSplitResult result = nalUSplitter.splitBlock(incompleteHeadCompleteBody, 950);

        // Valid partial start part expected
        assertNotNull("No partial start part found!", result.getPartialStartPart());
        assertThat("Partial start part not parsed correctly!", Arrays.equals(result.getPartialStartPart(), incompleteNalU), is(true));

        // Valid list with 8 complete parts expected
        assertNotNull("List with complete parts was null!", result.getCompleteNalUs());
        assertThat("List had invalid size!", result.getCompleteNalUs().size(), is(8));

        // Valid partial end part expected
        assertNotNull("Partial end part was null!", result.getPartialEndPart());
        assertThat("Partial end part not parsed correctly!", Arrays.equals(result.getPartialEndPart(), threeByteDelimiterNalU), is(true));
    }

    @Test
    public void splittingCompleteHeadNoBodyShouldGet1PartialEndPart()
    {
        NalUSplitResult result = nalUSplitter.splitBlock(completeHeadNoBody, 100);

        // No partial start part expected
        assertNull("Partial start part existed unexpectedly!", result.getPartialStartPart());

        // No complete part expected
        assertThat("Too many complete parts!", result.getCompleteNalUs().size(), is(0));

        // Valid partial end part expected
        assertNotNull("Partial end part was null!", result.getPartialEndPart());
        assertThat("Partial end part not parsed correctly!", Arrays.equals(result.getPartialEndPart(), threeByteDelimiterNalU), is(true));
    }

    @Test
    public void splittingIncompleteHeadNoBodyShouldGet1PartialStartPart()
    {
        NalUSplitResult result = nalUSplitter.splitBlock(incompleteHeadNoBody, 50);

        // valid partial start part expected
        assertNotNull("Partial start part was null!", result.getPartialStartPart());
        assertThat("Partial start part not parsed correctly!", Arrays.equals(result.getPartialStartPart(), incompleteNalU), is(true));

        // No complete part expected
        boolean nalUsExisting = result.getCompleteNalUs() != null && result.getCompleteNalUs().size() > 0;
        assertThat("Too many complete parts!", nalUsExisting, is(false));

        // no partial end part expected
        assertNull("Partial end part was not null!", result.getPartialEndPart());
    }
}
