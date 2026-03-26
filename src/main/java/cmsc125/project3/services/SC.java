package cmsc125.project3.services;

import java.util.Arrays;

/**
 * Second Chance (Clock) Paging Algorithm
 */
public class SC extends FIFO {
    private boolean[] referenceBits;

    public SC (int[] reference, int frameSize) {
        super(reference, frameSize);
        referenceBits = new boolean[frameSize];
        Arrays.fill(referenceBits, false);
    }

    @Override
    public boolean simulateStep() {
        if (referencePointer >= reference.length) {
            return false;
        }

        int currentPage = reference[referencePointer];
        int hitIndex = -1;

        // 1. Check for Hit
        for (int i = 0; i < frame.length; i++) {
            if (frame[i] == currentPage) {
                hitIndex = i;
                break;
            }
        }

        if (hitIndex != -1) {
            // HIT: Set the reference bit to true (Giving it a second chance)
            pageStatus = true;
            referenceBits[hitIndex] = true;
        } else {
            // FAULT: Use the Clock logic to find a victim
            pageStatus = false;
            int victimIndex = findNextFramePointer();
            
            frame[victimIndex] = currentPage;
            referenceBits[victimIndex] = false; // New pages start with bit 0
            
            // Move the clock hand forward for the NEXT fault
            framePointer = (victimIndex + 1) % frame.length;
        }

        referencePointer++;
        return true;
    }

    @Override
    protected int findNextFramePointer() {
        // 1. Fill empty slots first (Standard procedure)
        for (int i = 0; i < frame.length; i++) {
            if (frame[i] == -1) return i;
        }

        // 2. The Clock Search
        while (true) {
            if (referenceBits[framePointer]) {
                // Page has a second chance: clear it and move hand
                referenceBits[framePointer] = false;
                framePointer = (framePointer + 1) % frame.length;
            } else {
                // Page has NO second chance: This is our victim!
                return framePointer;
            }
        }
    }
    
    public boolean[] getReferenceBits() {
        return referenceBits;
    }
}
