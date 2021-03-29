package environment;

import java.util.Arrays;
import java.util.Vector;

public abstract class Handler implements Runnable {

    protected Vector<ToHandle> inbox;
    protected int nbInInBox;
    protected final Object dummy = new Object();
    protected boolean suspendRequested;
    protected boolean running;

    public Handler() {
        inbox = new Vector<>(0);
        nbInInBox = 0;
        Thread t = new Thread(this);
        suspendRequested = true;
        t.start();
    }

    synchronized void deposit(ToHandle toBeHandled) {
        inbox.addElement(toBeHandled);
        nbInInBox++;
        if (suspendRequested) {
            requestResume();
        }
    }

    public void run() {
        running = true;
        while (running) {
            checkSuspended();
            if (running) {
                monitorIncomingToBeHandleds();
            }
        }
    }

    protected void checkSuspended() {
        try {
            synchronized (dummy) {
                while (suspendRequested) {
                    dummy.wait();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void requestSuspend() {
        suspendRequested = true;
    }

    void requestResume() {
        suspendRequested = false;
        synchronized (dummy) {
            dummy.notify();
        }
    }

    public synchronized void monitorIncomingToBeHandleds() {
        if (inbox.isEmpty()) {
            requestSuspend();
        } else {
            ToHandle first = null;
            try {
                first = inbox.elementAt(0);
                inbox.removeElementAt(0);
                nbInInBox--;
            } catch (IndexOutOfBoundsException exc) {
                exc.printStackTrace();
            }
            process(first);
        }
    }

    /**
     *This is the method to be implemented by each of the three handlers!!!!!
     */
    protected abstract void process(ToHandle toBehandled);

    // a random number generator method transported from the old ControllerImp
    protected int nextActive(boolean[] turns) {
        boolean hit = false;
        int next = 0;
        boolean candidate = false;
        while (!candidate & next < turns.length) {
            candidate = !turns[next++];
            //System.out.println(next+" (candidate) = "+candidate);
        }
        if (!candidate) {
            //System.out.println("no candidate -> clear turns");
            Arrays.fill(turns, false);
        }
        while (!hit) {
            next = (int) (Math.random() * turns.length);
            hit = !turns[next];
            //System.out.println(next+" (hit) = "+hit);
        }
        turns[next] = true;
        return next;
        //return (active + 1) % implementations.length;
    }

    public void finish() {
        running = false;
        requestResume();

    }

}
