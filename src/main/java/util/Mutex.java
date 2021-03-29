package util;

/**
 * Class needed for Synchronization
 */
public class Mutex {

    private boolean lockTaken;

    public Mutex() {
        lockTaken = false;
    }

    public synchronized void acquireLock() {
        Debug.print(this, "Requesting lock " + this.toString());
        while (lockTaken) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Debug.print(this, "Acquiring lock " + this.toString());
        lockTaken = true;
    }

    public synchronized void releaseLock() {
        lockTaken = false;
        Debug.print(this, "Releasing lock " + this.toString());
        notify();
    }

}
