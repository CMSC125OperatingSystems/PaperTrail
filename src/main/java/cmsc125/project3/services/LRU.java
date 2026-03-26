package cmsc125.project3.services;

import java.util.Arrays;

public class LRU extends FIFO {
    private int[] lastAccessedTime;

    public LRU(int[] reference, int frameSize) {
        super(reference, frameSize);
        lastAccessedTime = new int[frameSize];
        Arrays.fill(lastAccessedTime, -1);
    }

    @Override
    public boolean simulateStep() {
        if (referencePointer >= reference.length) {
            return false;
        }

        int currentPage = reference[referencePointer];
        int hitIndex = -1;

        for (int i = 0; i < frame.length; i++) {
            if (frame[i] == currentPage) {
                hitIndex = i;
                break;
            }
        }

        if (hitIndex != -1) {
            pageStatus = true;
            lastAccessedTime[hitIndex] = referencePointer;
        } else {
            pageStatus = false;
            int victimIndex = findNextFramePointer();

            frame[victimIndex] = currentPage;
            lastAccessedTime[victimIndex] = referencePointer;

            framePointer = victimIndex;
        }

        referencePointer++;
        return true;
    }

    @Override
    protected int findNextFramePointer() {
        // 1. Still prioritize empty slots
        for (int i = 0; i < frame.length; i++) {
            if (frame[i] == -1) return i;
        }

        // 2. LRU Logic: Find the frame that hasn't been touched in the longest time
        int minTime = Integer.MAX_VALUE;
        int victimIndex = 0;

        for (int i = 0; i < lastAccessedTime.length; i++) {
            if (lastAccessedTime[i] < minTime) {
                minTime = lastAccessedTime[i];
                victimIndex = i;
            }
        }

        return victimIndex;
    }
}
