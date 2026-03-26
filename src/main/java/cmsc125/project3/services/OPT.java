package cmsc125.project3.services;

public class OPT extends FIFO {

    public OPT(int[] reference, int frameSize) {
        super(reference, frameSize);
    }

    // We override this to look into the future
    @Override
    protected int findNextFramePointer() {
        // 1. Still prioritize empty slots
        for (int i = 0; i < frame.length; i++) {
            if (frame[i] == -1) return i;
        }

        int victimIndex = -1;
        int farthestUse = -1;

        for (int i = 0; i < frame.length; i++) {
            int nextUse = Integer.MAX_VALUE; // Default if never used again

            // Search forward in the reference string
            for (int j = referencePointer + 1; j < reference.length; j++) {
                if (frame[i] == reference[j]) {
                    nextUse = j;
                    break;
                }
            }

            // The page that is used farthest in the future (or never) is the victim
            if (nextUse > farthestUse) {
                farthestUse = nextUse;
                victimIndex = i;
            }
        }
        return victimIndex;
    }
}
