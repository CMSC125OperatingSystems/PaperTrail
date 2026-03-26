package cmsc125.project3.services;

/**
 * FIFO
 * this is a service algrithm for the
 * first in first out paging algorithm
 */
public class FIFO {
    int[] reference;
    int[] frame;
    int framePointer;
    int referencePointer;
    boolean pageStatus;


	public FIFO(int[] reference, int frameSize) {
        this.reference = reference;
        frame = new int[frameSize];
        framePointer = 0;
        referencePointer = 0;
        pageStatus = false;

        for (int f = 0; f < frameSize; f++) {
            frame[f] = -1;
        }
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

            framePointer = (framePointer + 1) % frame.length;
        } else {
            pageStatus = true;
        }

        referencePointer++;
        return true;
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
