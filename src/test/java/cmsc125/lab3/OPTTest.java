package cmsc125.lab3;

import org.junit.jupiter.api.Test;

import cmsc125.project3.services.FIFO;
import cmsc125.project3.services.OPT;

import static org.junit.jupiter.api.Assertions.*;

class OPTTest {
    @Test
    void testOptimalReplacement() {
        // Reference string where '2' is used last in the future
        int[] ref = {1, 2, 3, 1, 2, 4}; 
        OPT opt = new OPT(ref, 3);

        // Fill the frames [1, 2, 3]
        for(int i = 0; i < 3; i++) opt.simulateStep();

        // Step 4: Page 1 (Hit)
        opt.simulateStep(); 
        assertTrue(opt.getPageStatus());

        // Step 5: Page 2 (Hit)
        opt.simulateStep();
        assertTrue(opt.getPageStatus());

        // Step 6: Page 4 (Fault)
        // Memory is [1, 2, 3]. 
        // Looking ahead: 1 and 2 are NOT in the future anymore.
        // Page 3 is also NOT in the future.
        // In this specific string, all are "infinite" distance after index 5.
        // But let's try a string where the difference is clear:

        int[] ref2 = {1, 2, 3, 1, 4};
        OPT opt2 = new OPT(ref2, 3);
        for(int i = 0; i < 4; i++) opt2.simulateStep(); // Memory: [1, 2, 3], current is index 3 (Page 1)

        opt2.simulateStep(); // Now processing Page 4

        // Page 1 is used at index 3 (already passed, so distance is infinity)
        // Page 2 and 3 are never used.
        // The algorithm should have replaced one of them.
        assertFalse(opt2.getPageStatus());
    }

    @Test
    void testImageExampleOPT() {
        // Reference String from the image
        int[] reference = {7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2, 3};
        int frameSize = 4;

        // Initializing our OPT class (Assuming it extends your FIFO structure)
        OPT opt = new OPT(reference, frameSize);

        int totalFaults = 0;

        // Run the simulation step by step
        while (opt.simulateStep()) {
            if (!opt.getPageStatus()) {
                totalFaults++;
            }

            // Specific Logic Check: After Page '3' arrives (index 5)
            // Memory was [7, 0, 1, 2]. 
            // 7 is never used again. 0, 1, and 2 appear later.
            // Therefore, 7 MUST be replaced by 3.
            if (opt.getReferencePointer() == 6) { // Just finished Page 3
                assertFalse(contains(opt.getFrame(), 7), "Page 7 should have been evicted for Page 3");
                assertTrue(contains(opt.getFrame(), 3), "Page 3 should now be in memory");
            }

            // Specific Logic Check: After Page '4' arrives (index 7)
            // 1 is never used again. 
            // Therefore, 1 MUST be replaced by 4.
            if (opt.getReferencePointer() == 8) { // Just finished Page 4
                assertFalse(contains(opt.getFrame(), 1), "Page 1 should have been evicted for Page 4");
                assertTrue(contains(opt.getFrame(), 4), "Page 4 should now be in memory");
            }
        }

        // 1. Verify Total Page Faults from image
        assertEquals(6, totalFaults, "Total page faults should be 6");

        // 2. Verify Final Frame State from image: [3, 0, 4, 2] 
        // (Order might vary depending on your implementation, so we check content)
        int[] finalFrames = opt.getFrame();
        assertTrue(contains(finalFrames, 3));
        assertTrue(contains(finalFrames, 0));
        assertTrue(contains(finalFrames, 4));
        assertTrue(contains(finalFrames, 2));
    }

    // Helper method to check if an array contains a value
    private boolean contains(int[] arr, int val) {
        for (int i : arr) {
            if (i == val) return true;
        }
        return false;
    }
}
