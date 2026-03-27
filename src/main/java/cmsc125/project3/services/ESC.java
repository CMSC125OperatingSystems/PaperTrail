package cmsc125.project3.services;

import java.util.Arrays;

/**
 * Implements the Enhanced Second-Chance (ESC) paging algorithm.
 *
 * This algorithm improves upon the basic Second-Chance (Clock) algorithm by
 * considering both a reference bit (R) and a modify bit (M) for each page frame.
 * The pair (R, M) creates four classes of pages, which are checked in order to find
 * the best victim for replacement:
 *
 * 1. (0, 0): Not recently used, not modified (clean). - Best victim.
 * 2. (0, 1): Not recently used, but modified (dirty). - Good victim, but needs to be written to disk.
 * 3. (1, 0): Recently used, not modified. - Poor victim.
 * 4. (1, 1): Recently used and modified. - Worst victim.
 *
 * The algorithm sweeps through the frames in a circular manner (like a clock hand),
 * looking for the lowest-class page to replace. During the scan for a (0, 1) victim,
 * it clears the reference bits of all pages it passes, giving them a "second chance"
 * before they can be replaced.
 */
public class ESC extends FIFO {
    protected boolean[] referenceBits;
    private boolean[] modifyBits;
    private boolean[] isWriteOperation;

    // Overloaded constructor to accept the read/write operations array
    public ESC(int[] reference, boolean[] isWriteOperation, int frameSize) {
        super(reference, frameSize);
        this.isWriteOperation = isWriteOperation;

        referenceBits = new boolean[frameSize];
        modifyBits = new boolean[frameSize];
        Arrays.fill(referenceBits, false);
        Arrays.fill(modifyBits, false);
    }

    /**
     * Standard constructor for simple Reference String (defaults all to Read)
     */
    public ESC(int[] reference, int frameSize) {
        this(reference, null, frameSize);
    }

    @Override
    public boolean simulateStep() {
        if (referencePointer >= reference.length) {
            return false;
        }

        int currentPage = reference[referencePointer];

        // Check if the current operation is a Write. Default to Read (false) if array is missing.
        boolean isWrite = (isWriteOperation != null && referencePointer < isWriteOperation.length)
                           ? isWriteOperation[referencePointer] : false;

        int hitIndex = -1;

        // 1. Check for Hit
        for (int i = 0; i < frame.length; i++) {
            if (frame[i] == currentPage) {
                hitIndex = i;
                break;
            }
        }

        if (hitIndex != -1) {
            // HIT: Page is used again
            pageStatus = true;
            referenceBits[hitIndex] = true; // Gets a second chance!

            // If this specific access is a write, the page becomes dirty.
            if (isWrite) {
                modifyBits[hitIndex] = true;
            }
        } else {
            // FAULT
            pageStatus = false;
            int victimIndex = findNextFramePointer();

            frame[victimIndex] = currentPage;
            arrivalTimes[victimIndex] = referencePointer;

            // NEW PAGE LOAD:
            // Reference bit starts at 0 (per test expectation/course requirements)
            referenceBits[victimIndex] = false;

            // Modify bit reflects the current operation (True if Write, False if Read)
            modifyBits[victimIndex] = isWrite;

            // Move the clock hand forward to the position after the victim
            framePointer = (victimIndex + 1) % frame.length;
        }

        referencePointer++;
        return true;
    }

    @Override
    protected int findNextFramePointer() {
        // First, check for empty frames
        for (int i = 0; i < frame.length; i++) {
            int current = (framePointer + i) % frame.length;
            if (frame[current] == -1) {
                return current;
            }
        }

        // Loop indefinitely until a victim is found. This ensures all passes are made.
        while (true) {
            // PASS 1: Scan for (0, 0) - Not recently used, not modified.
            // Do NOT clear reference bits during this pass.
            for (int i = 0; i < frame.length; i++) {
                int current = (framePointer + i) % frame.length;
                if (!referenceBits[current] && !modifyBits[current]) {
                    return current; // Found the perfect victim
                }
            }

            // PASS 2: Scan for (0, 1) - Not recently used, but modified.
            // CLEAR reference bits as we pass them to give the "Second Chance".
            // This pass must complete its sweep to clear all R-bits, even if a victim is found.
            int potentialVictim = -1;
            for (int i = 0; i < frame.length; i++) {
                int current = (framePointer + i) % frame.length;
                if (!referenceBits[current] && modifyBits[current] && potentialVictim == -1) {
                    potentialVictim = current; // Mark the first (0,1) found as a potential victim
                }
                referenceBits[current] = false; // Clear the reference bit for every page encountered
            }

            // If a (0,1) victim was found in Pass 2, return it now.
            if (potentialVictim != -1) {
                return potentialVictim;
            }

            // If we reach here, all reference bits are now 0.
            // The `while(true)` loop will repeat, and the next Pass 1 is guaranteed to find a (0,0)
            // (which were originally (1,0) or (1,1) but now have R=0).
        }
    }

    public boolean[] getReferenceBits() { return referenceBits; }
    public boolean[] getModifyBits() { return modifyBits; }
}
