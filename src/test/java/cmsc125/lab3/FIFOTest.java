package cmsc125.lab3;

import org.junit.jupiter.api.Test;

import cmsc125.project3.services.FIFO;

import static org.junit.jupiter.api.Assertions.*;

class FIFOTest {

    @Test
    void testBasicReplacement() {
        int[] ref = {1, 2, 3, 4};
        FIFO fifo = new FIFO(ref, 3);
        
        // Fill first 3 frames
        fifo.simulateStep(); // Page 1: Fault
        fifo.simulateStep(); // Page 2: Fault
        fifo.simulateStep(); // Page 3: Fault
        
        assertArrayEquals(new int[]{1, 2, 3}, fifo.getFrame());
        assertEquals(0, fifo.getFramePointer()); // Pointer wrapped back to 0

        // Step 4: Page 4 should replace Page 1
        fifo.simulateStep();
        assertArrayEquals(new int[]{4, 2, 3}, fifo.getFrame());
        assertFalse(fifo.getPageStatus()); // Should be a fault
        assertEquals(1, fifo.getFramePointer()); // Pointer moved to index 1
    }

    @Test
    void testPageHit() {
        int[] ref = {1, 2, 1};
        FIFO fifo = new FIFO(ref, 3);
        
        fifo.simulateStep(); // Page 1
        fifo.simulateStep(); // Page 2
        
        int pointerBeforeHit = fifo.getFramePointer();
        
        boolean hasNext = fifo.simulateStep(); // Page 1 (Hit)
        
        assertTrue(fifo.getPageStatus(), "Page 1 should be a Hit");
        assertEquals(pointerBeforeHit, fifo.getFramePointer(), "Pointer should not move on Hit");
        assertArrayEquals(new int[]{1, 2, -1}, fifo.getFrame()); // Assuming initialized to 0 or -1
    }

    @Test
    void testImageExampleFIFO() {
        int[] reference = {1, 3, 0, 3, 5, 6, 3};
        int frameSize = 3;
        FIFO fifo = new FIFO(reference, frameSize);

        // Manual initialization check (all should be -1)
        for(int val : fifo.getFrame()) {
            assertEquals(-1, val);
        }

        int totalFaults = 0;

        // Run the simulation step by step
        while (fifo.simulateStep()) {
            if (!fifo.getPageStatus()) {
                totalFaults++;
            }
        }

        // 1. Verify Total Page Faults
        assertEquals(6, totalFaults, "Total faults should be 6 according to the image.");

        // 2. Verify Final State of Frames
        // After the last '3' replaces '0':
        // 1 was replaced by 5
        // 3 was replaced by 6
        // 0 was replaced by 3
        int[] expectedFinalFrames = {5, 6, 3}; 
        assertArrayEquals(expectedFinalFrames, fifo.getFrame());
    }
}
