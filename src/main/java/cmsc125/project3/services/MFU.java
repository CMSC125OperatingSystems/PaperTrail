package cmsc125.project3.services;

/**
 * MFU (Most Frequently Used) Paging Algorithm
 * Evicts the page with the highest access count. Uses FIFO for ties.
 */
public class MFU extends LFU {

    public MFU(int[] reference, int frameSize) {
        super(reference, frameSize);
    }

    // We only need to override the search logic to look for the MAXIMUM frequency!
    @Override
    protected int findNextFramePointer() {
        // 1. Fill empty slots first
        for (int i = 0; i < frame.length; i++) {
            if (frame[i] == -1) return i;
        }

        // 2. Find the MAXIMUM frequency currently in the frames
        int maxFreq = -1;
        for (int i = 0; i < frequencies.length; i++) {
            if (frequencies[i] > maxFreq) {
                maxFreq = frequencies[i];
            }
        }

        // 3. FIFO Tie-Breaker: Find the oldest arrival time among pages with maxFreq
        int victimIndex = -1;
        int oldestArrival = Integer.MAX_VALUE;

        for (int i = 0; i < frame.length; i++) {
            if (frequencies[i] == maxFreq) {
                if (arrivalTimes[i] < oldestArrival) {
                    oldestArrival = arrivalTimes[i];
                    victimIndex = i;
                }
            }
        }

        return victimIndex;
    }
}
