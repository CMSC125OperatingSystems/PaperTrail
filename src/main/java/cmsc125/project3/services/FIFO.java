package cmsc125.project3.services;

import java.util.Arrays;

/**
 * FIFO
 * this is a service algrithm for the
 * first in first out paging algorithm
 */
public class FIFO {
    int[] reference;
    int[] frame;
    int[] arrivalTimes;
    int framePointer;
    int referencePointer;
    boolean pageStatus;


	public FIFO(int[] reference, int frameSize) {
        this.reference = reference;
        frame = new int[frameSize];
        arrivalTimes = new int[frameSize];
        framePointer = 0;
        referencePointer = 0;
        pageStatus = false;

        Arrays.fill(frame, -1);
        Arrays.fill(arrivalTimes, -1);
    }

    public boolean simulateStep() {
        if (referencePointer >= reference.length) {
            return false;
        }

        int currentPage = reference[referencePointer];
        boolean hit = false;

        for (int i = 0; i < frame.length; i++) {
            if (frame[i] == currentPage) {
                hit = true;
                break;
            }
        }

        if (!hit) {
            pageStatus = false;
            frame[framePointer] = currentPage;
            arrivalTimes[framePointer] = referencePointer; //reference pointer also functions as a "timer"

            framePointer = findNextFramePointer();
        } else {
            pageStatus = true;
        }

        referencePointer++;
        return true;
    }

    protected int findNextFramePointer() {
        int min = Integer.MAX_VALUE;
        int pointer = 0;
        int minPointer = 0;

        for (pointer = 0; pointer < arrivalTimes.length; pointer++) {
            if (arrivalTimes[pointer] == -1) {
                minPointer = pointer;
                break;
            }

            if (arrivalTimes[pointer] < min) {
                min = arrivalTimes[pointer];
                minPointer = pointer;
            }
        }
        return minPointer;
    }

    public int[] getReference() {
		return reference;
	}

	public int[] getFrame() {
		return frame;
	}

	public int getFramePointer() {
		return framePointer;
	}

	public int getReferencePointer() {
		return referencePointer;
	}

	public boolean getPageStatus() {
		return pageStatus;
	}
}
