package synchronizer;

public class DummySynchronizer implements Synchronizer {

    public DummySynchronizer() {
    }

    public synchronized int[] getSyncSet(int agent) {
        return new int[0];
    }

    public void synchronize(int agent, int[] setOfCandidates, int time) {
        // NO-OP;
    }
}
