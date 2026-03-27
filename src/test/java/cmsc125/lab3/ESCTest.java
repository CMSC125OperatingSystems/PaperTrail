package cmsc125.lab3;

import org.junit.jupiter.api.Test;
import cmsc125.project3.services.*;

import static org.junit.jupiter.api.Assertions.*;

class ESCTest {
@Test
    void testEnhancedInitialLoadAndHit() {
        int[] reference = {1, 2, 1};
        // 1 is a Read, 2 is a Write, the second 1 is a Read
        boolean[] isWrite = {false, true, false}; 
        ESC esc = new ESC(reference, isWrite, 3);

        // Step 1: Load 1 (Read)
        esc.simulateStep();
        assertFalse(esc.getReferenceBits()[0], "New page should have Reference = 0");
        assertFalse(esc.getModifyBits()[0], "Read operation should have Modify = 0");

        // Step 2: Load 2 (Write)
        esc.simulateStep();
        assertFalse(esc.getReferenceBits()[1], "New page should have Reference = 0");
        assertTrue(esc.getModifyBits()[1], "Write operation should have Modify = 1");

        // Step 3: Hit on 1 (Read)
        esc.simulateStep();
        assertTrue(esc.getPageStatus(), "This should be a Hit");
        assertTrue(esc.getReferenceBits()[0], "Hit should set Reference = 1");
        assertFalse(esc.getModifyBits()[0], "A Read hit should not make a clean page dirty");
    }

    @Test
    void testEnhancedPriorityEviction() {
        int[] reference = {1, 2, 3};
        boolean[] isWrite = {false, true, false};
        ESC esc = new ESC(reference, isWrite, 2);

        // Load 1 (Read) and 2 (Write) into the 2 frames
        esc.simulateStep(); // Index 0 gets Page 1 (0, 0)
        esc.simulateStep(); // Index 1 gets Page 2 (0, 1)

        // Manually trigger a hit on Page 1 to make it (1, 0)
        // In a real string, this would be a second '1' in the reference array.
        esc.getReferenceBits()[0] = true; 
        
        // Frame state:
        // Index 0: Page 1 (1, 0) - Recently used, clean
        // Index 1: Page 2 (0, 1) - Not recently used, dirty
        // Clock hand points to index 0.

        // Step 3: Load 3 (Fault)
        esc.simulateStep();
        
        // Pass 1: Looks for (0,0). Fails.
        // Pass 2: Looks for (0,1). Checks index 0 (fails, flips ref to 0). Checks index 1 -> MATCH!
        // Page 2 should be evicted, Page 1 should be saved.
        int[] frames = esc.getFrame();
        assertTrue(contains(frames, 1), "Page 1 should be saved because it was recently used");
        assertFalse(contains(frames, 2), "Page 2 should be evicted because it wasn't recently used");
        assertTrue(contains(frames, 3), "Page 3 should now be in memory");
    }

    @Test
    void testEnhancedFourPassSweep() {
        int[] reference = {1, 2, 3, 1, 2, 3, 4};
        // Setup: 1 is Read, 2 is Write, 3 is Write. Subsequent hits don't matter as much.
        boolean[] isWrite = {false, true, true, false, false, false, false};
        ESC esc = new ESC(reference, isWrite, 3);

        // Fill frames and trigger hits so they all get Reference = 1
        for (int i = 0; i < 6; i++) {
            esc.simulateStep();
        }

        // Current Frame State (Pointer is at 0):
        // Index 0: Page 1 (1, 0)
        // Index 1: Page 2 (1, 1)
        // Index 2: Page 3 (1, 1)

        // Step 7: Load 4 (Fault)
        esc.simulateStep();

        // Pass 1: Looks for (0,0). Fails.
        // Pass 2: Looks for (0,1). Fails. (But flips all reference bits to 0 along the way).
        // Current state after Pass 2: Page 1(0,0), Page 2(0,1), Page 3(0,1).
        // Pass 3: Looks for (0,0). FINDS Page 1 at index 0!
        
        int[] frames = esc.getFrame();
        assertFalse(contains(frames, 1), "Page 1 should have been evicted on Pass 3");
        assertTrue(contains(frames, 4), "Page 4 should replace Page 1 at index 0");
        
        // Ensure the clock hand moved past the replacement
        assertEquals(1, esc.getFramePointer(), "Clock hand should advance to index 1");
    }    // Helper method to check if an array contains a value

    private boolean contains(int[] arr, int val) {
        for (int i : arr) {
            if (i == val) return true;
        }
        return false;
    }
}
