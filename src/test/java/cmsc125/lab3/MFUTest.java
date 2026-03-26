package cmsc125.lab3;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import cmsc125.project3.services.MFU;
public class MFUTest {

    @Test
    public void testMfuBasicHitsAndMisses() {
        int[] reference = {1, 2, 1, 3};
        int frameSize = 2;
        MFU mfu = new MFU(reference, frameSize);

        // Step 1: Request Page 1
        assertTrue(mfu.simulateStep());
        assertArrayEquals(new int[]{1, -1}, mfu.getFrame());

        // Step 2: Request Page 2
        assertTrue(mfu.simulateStep());
        assertArrayEquals(new int[]{1, 2}, mfu.getFrame());

        // Step 3: Request Page 1 again (HIT)
        assertTrue(mfu.simulateStep());
        assertTrue(mfu.getPageStatus(), "Expected a page hit for 1");
        // Page 1 now has freq 2, Page 2 has freq 1

        // Step 4: Request Page 3 (FAULT - Eviction needed)
        // MFU should evict Page 1 because it has the HIGHEST frequency (2 vs 1)
        assertTrue(mfu.simulateStep());
        assertFalse(mfu.getPageStatus(), "Expected a page fault for 3");
        assertArrayEquals(new int[]{3, 2}, mfu.getFrame(), "Page 1 should be evicted, leaving 3 and 2");
    }

    @Test
    public void testMfuFifoTieBreaker() {
        // Reference causes a tie in highest frequency, forcing the FIFO fallback
        int[] reference = {1, 2, 1, 2, 3};
        int frameSize = 2;
        MFU mfu = new MFU(reference, frameSize);

        mfu.simulateStep(); // 1 faults
        mfu.simulateStep(); // 2 faults
        mfu.simulateStep(); // 1 hits (freq = 2)
        mfu.simulateStep(); // 2 hits (freq = 2)

        // Step 5: Request Page 3
        // Both 1 and 2 have a max frequency of 2.
        // Page 1 arrived at index 0, Page 2 arrived at index 1.
        // FIFO dictates evicting the oldest (Page 1).
        mfu.simulateStep(); 
        assertFalse(mfu.getPageStatus());
        assertArrayEquals(new int[]{3, 2}, mfu.getFrame(), "Page 1 should be evicted due to FIFO tie-breaker");
    }
}
