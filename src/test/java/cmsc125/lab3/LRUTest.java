package cmsc125.lab3;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import cmsc125.project3.services.LRU;

import static org.junit.jupiter.api.Assertions.*;

class LRUTest {

    @Test
    void testLRUReplacementWithHits() {
        // String where Page 1 is the oldest arrival but used recently
        int[] reference = {1, 2, 3, 1, 4};
        LRU lru = new LRU(reference, 3);

        // Step 1-3: Fill [1, 2, 3]
        lru.simulateStep(); // 1
        lru.simulateStep(); // 2
        lru.simulateStep(); // 3

        // Step 4: Page 1 (Hit) - This should reset Page 1's "age"
        lru.simulateStep(); 
        assertTrue(lru.getPageStatus(), "Step 4 should be a Hit");

        // Step 5: Page 4 (Fault)
        lru.simulateStep();
        assertFalse(lru.getPageStatus(), "Step 5 should be a Fault");

        // In FIFO, 1 would be evicted. In LRU, 2 must be evicted.
        int[] frames = lru.getFrame();
        assertTrue(contains(frames, 1), "Page 1 should still be in memory because of the hit");
        assertFalse(contains(frames, 2), "Page 2 should have been evicted as the Least Recently Used");
        assertTrue(contains(frames, 4), "Page 4 should now be in memory");
    }

    @Test
    @DisplayName("LRU: Image Example Test (7, 0, 1, 2...) with 4 Frames")
    void testLRUWithImageExample() {
        // Reference String from the image
        int[] reference = {7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2, 3};
        int frameSize = 4;

        LRU lru = new LRU(reference, frameSize);
        int totalFaults = 0;

        // Simulate the steps
        while (lru.simulateStep()) {
            if (!lru.getPageStatus()) {
                totalFaults++;
            }

            // Logic Check: When Page 3 arrives (index 5)
            // Memory state should be [3, 0, 1, 2] (7 was evicted)
            if (lru.getReferencePointer() == 6) { 
                assertFalse(contains(lru.getFrame(), 7), "Page 7 should be evicted for Page 3");
                assertTrue(contains(lru.getFrame(), 3), "Page 3 should now be in memory");
                assertTrue(contains(lru.getFrame(), 0), "Page 0 should still be in memory (Recent Hit)");
            }

            // Logic Check: When Page 4 arrives (index 7)
            // Memory state should be [3, 0, 4, 2] (1 was evicted)
            if (lru.getReferencePointer() == 8) {
                assertFalse(contains(lru.getFrame(), 1), "Page 1 should be evicted for Page 4");
                assertTrue(contains(lru.getFrame(), 4), "Page 4 should now be in memory");
            }
        }

        // Verify final results from the image
        assertEquals(6, totalFaults, "Total Page Faults should be 6 according to the image.");

        // Verify final frame contents: 2, 4, 0, 3
        int[] finalFrames = lru.getFrame();
        assertTrue(contains(finalFrames, 2));
        assertTrue(contains(finalFrames, 4));
        assertTrue(contains(finalFrames, 0));
        assertTrue(contains(finalFrames, 3));
    }

    private boolean contains(int[] arr, int val) {
        for (int i : arr) if (i == val) return true;
        return false;
    }
}
