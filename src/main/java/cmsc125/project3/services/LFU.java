package cmsc125.project3.services;

import java.util.Arrays;

/**
 * LFU (Least Frequently Used) Paging Algorithm
 * Evicts the page with the lowest access count. Uses FIFO for ties.
 */
public class LFU extends FIFO {
    protected int[] frequencies; // 'protected' so MFU can reuse it!

    public LFU(int[] reference, int frameSize) {
        super(reference, frameSize);
        frequencies = new int[frameSize];
        Arrays.fill(frequencies, 0);
    }

    @Override
    public boolean simulateStep() {
        if (referencePointer >= reference.length) return false;

        int currentPage = reference[referencePointer];
        int hitIndex = -1;

        for (int i = 0; i < frame.length; i++) {
            if (frame[i] == currentPage) {
                hitIndex = i;
                break;
            }
        }

        if (hitIndex != -1) {
            // HIT: Increment the frequency counter
            pageStatus = true;
            frequencies[hitIndex]++; 
        } else {
            // FAULT: Find victim, reset frequency, record arrival time
            pageStatus = false;
            int victimIndex = findNextFramePointer();

            frame[victimIndex] = currentPage;
            frequencies[victimIndex] = 1; // New page has been used exactly once
            arrivalTimes[victimIndex] = referencePointer; // Record time for FIFO tie-breaker

            framePointer = victimIndex;
        }

        referencePointer++;
        return true;
    }

    @Override
    protected int findNextFramePointer() {
        // 1. Fill empty slots first
        for (int i = 0; i < frame.length; i++) {
            if (frame[i] == -1) return i;
        }

        // 2. Find the MINIMUM frequency currently in the frames
        int minFreq = Integer.MAX_VALUE;
        for (int i = 0; i < frequencies.length; i++) {
            if (frequencies[i] < minFreq) {
                minFreq = frequencies[i];
            }
        }

        // 3. FIFO Tie-Breaker: Find the oldest arrival time among pages with minFreq
        int victimIndex = -1;
        int oldestArrival = Integer.MAX_VALUE;

        for (int i = 0; i < frame.length; i++) {
            if (frequencies[i] == minFreq) {
                if (arrivalTimes[i] < oldestArrival) {
                    oldestArrival = arrivalTimes[i];
                    victimIndex = i;
                }
            }
        }

        return victimIndex;
    }
    
    public int[] getFrequencies() { return frequencies; }
}
