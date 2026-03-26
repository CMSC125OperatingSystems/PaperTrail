package cmsc125.lab3;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import cmsc125.project3.services.LFU;

public class LFUTest {

    @Test
    public void testLfuBasicHitsAndMisses() {
        int[] reference = {1, 2, 1, 3};
        int frameSize = 2;
        LFU lfu = new LFU(reference, frameSize);

        // Step 1: Request Page 1
        assertTrue(lfu.simulateStep());
        assertFalse(lfu.getPageStatus(), "Expected a page fault for 1");
        assertArrayEquals(new int[]{1, -1}, lfu.getFrame());

        // Step 2: Request Page 2
        assertTrue(lfu.simulateStep());
        assertFalse(lfu.getPageStatus(), "Expected a page fault for 2");
        assertArrayEquals(new int[]{1, 2}, lfu.getFrame());

        // Step 3: Request Page 1 again (HIT)
        assertTrue(lfu.simulateStep());
        assertTrue(lfu.getPageStatus(), "Expected a page hit for 1");
        assertArrayEquals(new int[]{1, 2}, lfu.getFrame());
        assertArrayEquals(new int[]{2, 1}, lfu.getFrequencies(), "Page 1 frequency should be 2, Page 2 should be 1");

        // Step 4: Request Page 3 (FAULT - Eviction needed)
        // LFU should evict Page 2 because it has the lowest frequency (1 vs 2)
        assertTrue(lfu.simulateStep());
        assertFalse(lfu.getPageStatus(), "Expected a page fault for 3");
        assertArrayEquals(new int[]{1, 3}, lfu.getFrame(), "Page 2 should be evicted, leaving 1 and 3");
        
        // Simulation should end here
        assertFalse(lfu.simulateStep(), "Simulation should be complete");
    }

    @Test
    public void testLfuFifoTieBreaker() {
        // Reference causes a tie in frequency, forcing the FIFO fallback
        int[] reference = {1, 2, 3};
        int frameSize = 2;
        LFU lfu = new LFU(reference, frameSize);

        lfu.simulateStep(); // 1 faults, frame=[1, -1], freq=[1, 0]
        lfu.simulateStep(); // 2 faults, frame=[1, 2], freq=[1, 1]

        // Step 3: Request Page 3
        // Both 1 and 2 have a frequency of 1.
        // Page 1 arrived at index 0, Page 2 arrived at index 1.
        // FIFO dictates evicting the oldest (Page 1).
        lfu.simulateStep(); 
        assertFalse(lfu.getPageStatus());
        assertArrayEquals(new int[]{3, 2}, lfu.getFrame(), "Page 1 should be evicted due to FIFO tie-breaker");
    }
}
