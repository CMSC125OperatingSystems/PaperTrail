package cmsc125.lab3;

import org.junit.jupiter.api.Test;
import cmsc125.project3.services.*;

import static org.junit.jupiter.api.Assertions.*;

class SCTest {
    @Test
    void testSecondChanceLogic() {
        int[] reference = {0, 1, 2, 0, 3};
        int frameSize = 3;
        SC sc = new SC(reference, frameSize);

        // 1. Fill the frames [0, 1, 2]
        sc.simulateStep(); // 0
        sc.simulateStep(); // 1
        sc.simulateStep(); // 2

        // 2. Page 0 (Hit) - This sets the reference bit for index 0 to true
        sc.simulateStep(); 
        assertTrue(sc.getPageStatus(), "Page 0 should be a Hit");
        assertTrue(sc.getReferenceBits()[0], "Page 0 should have its reference bit set to true");

        // 3. Page 3 (Fault)
        sc.simulateStep();
        assertFalse(sc.getPageStatus(), "Page 3 should be a Fault");

        // Logic Check:
        // FIFO would have evicted Page 0 (index 0).
        // Second Chance skips 0 and evicts Page 1 (index 1).
        int[] frames = sc.getFrame();
        assertTrue(contains(frames, 0), "Page 0 should still be in memory (saved by second chance)");
        assertFalse(contains(frames, 1), "Page 1 should have been evicted");
        assertTrue(contains(frames, 3), "Page 3 should now be in memory at index 1");

        // The 'clock hand' should now be pointing at index 2 (the next victim)
        assertEquals(2, sc.getFramePointer(), "The clock hand should have moved to index 2");
    }

    @Test
    void testSecondChanceLongExample() {
        // Reference String from the image: 0, 4, 1, 4, 2, 4, 3, 4, 2, 4, 0, 4, 1, 4, 2, 4, 3, 4
        int[] reference = {0, 4, 1, 4, 2, 4, 3, 4, 2, 4, 0, 4, 1, 4, 2, 4, 3, 4};
        int frameSize = 3;

        SC sc = new SC(reference, frameSize);
        int totalFaults = 0;

        // Step-by-step verification based on image logic
        while (sc.simulateStep()) {
            if (!sc.getPageStatus()) {
                totalFaults++;
            }

            int currentRefIdx = sc.getReferencePointer() - 1; // current processing index
            int currentPage = reference[currentRefIdx];

            // LOGIC CHECK: Pass-5 (Ref index 4, Page 2)
            // Memory was [0, 4, 1]. Hand points to 0. Bit for 0 is 0.
            // 0 is replaced by 2. Hand moves to index 1.
            if (currentRefIdx == 4) {
                assertFalse(contains(sc.getFrame(), 0), "Page 0 should have been replaced by 2");
                assertTrue(contains(sc.getFrame(), 2), "Page 2 should now be in memory");
                assertEquals(1, sc.getFramePointer(), "Hand should point to index 1 (Page 4)");
            }

            // LOGIC CHECK: Pass-11 (Ref index 10, Page 0)
            // 2 and 4 had second chances. 3 did not.
            // Hand resets 2 and 4's bits, skips them, and replaces 3 with 0.
            if (currentRefIdx == 10) {
                assertFalse(contains(sc.getFrame(), 3), "Page 3 should have been replaced by 0");
                assertTrue(contains(sc.getFrame(), 0), "Page 0 should now be in memory");
                // Bits for 2 and 4 should have been reset to false during the search
                assertFalse(sc.getReferenceBits()[0], "Bit for Page 2 should be reset to 0");
                assertFalse(sc.getReferenceBits()[1], "Bit for Page 4 should be reset to 0");
            }
        }

        // FINAL VERIFICATION: Image shows Total Pf = 9
        assertEquals(9, totalFaults, "Total Page Faults should be 9 according to the image.");

        // FINAL STATE CHECK: Final frames should be [3, 4, 2]
        int[] finalFrames = sc.getFrame();
        assertTrue(contains(finalFrames, 3));
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
