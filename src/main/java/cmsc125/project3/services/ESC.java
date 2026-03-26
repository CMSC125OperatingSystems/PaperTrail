package cmsc125.project3.services;

import java.util.Arrays;

/**
 * Enhanced Second Chance Paging Algorithm
 * Uses a (Reference, Modify) pair to determine the best victim through up to 4 passes.
 */
public class ESC extends FIFO {
    private boolean[] referenceBits;
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
            // (If it was already dirty from a previous write, it stays dirty).
            if (isWrite) {
                modifyBits[hitIndex] = true; 
            }
        } else {
            // FAULT: Find the best victim using the 4-pass sweep
            pageStatus = false;
            int victimIndex = findNextFramePointer();

            frame[victimIndex] = currentPage;
            
            // NEW PAGE LOAD:
            // Reference bit starts at 0 (per course requirements)
            referenceBits[victimIndex] = false; 
            
            // Modify bit reflects the current operation (True if Write, False if Read)
            modifyBits[victimIndex] = isWrite; 
            
            // Move the clock hand forward
            framePointer = (victimIndex + 1) % frame.length;
        }

        referencePointer++;
        return true;
    }

    @Override
    protected int findNextFramePointer() {
        // 0. Fill empty slots first
        for (int i = 0; i < frame.length; i++) {
            if (frame[i] == -1) return i;
        }

        int startPointer = framePointer;

        // PASS 1: Scan for (0, 0) - Not referenced, not modified.
        // Do NOT clear reference bits during this pass.
        for (int i = 0; i < frame.length; i++) {
            int curr = (startPointer + i) % frame.length;
            if (!referenceBits[curr] && !modifyBits[curr]) {
                return curr; // Found the perfect victim
            }
        }

        // PASS 2: Scan for (0, 1) - Not referenced, but modified.
        // CLEAR reference bits as we pass them to give the "Second Chance".
        for (int i = 0; i < frame.length; i++) {
            int curr = (startPointer + i) % frame.length;
            if (!referenceBits[curr] && modifyBits[curr]) {
                return curr; // Found an acceptable victim
            }
            referenceBits[curr] = false; // The actual "Second Chance" flip
        }

        // PASS 3: Scan for (0, 0) AGAIN. 
        // We might find one now because we cleared reference bits in Pass 2!
        for (int i = 0; i < frame.length; i++) {
            int curr = (startPointer + i) % frame.length;
            if (!referenceBits[curr] && !modifyBits[curr]) {
                return curr; 
            }
        }

        // PASS 4: Scan for (0, 1) AGAIN.
        // Guaranteed to find one if we reach this point.
        for (int i = 0; i < frame.length; i++) {
            int curr = (startPointer + i) % frame.length;
            if (!referenceBits[curr] && modifyBits[curr]) {
                return curr;
            }
        }

        return framePointer; // Failsafe fallback
    }
    
    // Getters so your UI can display the (R, M) status
    public boolean[] getReferenceBits() { return referenceBits; }
    public boolean[] getModifyBits() { return modifyBits; }
}
