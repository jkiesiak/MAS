package agent;

import environment.ActiveItem;
import environment.Environment;
import environment.Perception;
import util.Debug;
import support.Outcome;
import synchronizer.Synchronization;
import util.Mutex;
import util.event.Listener;

import java.io.Serializable;
import java.util.Iterator;

/**
 * This class provides the basics of threading methods for active objects and
 * agents in the Environment.
 */
abstract public class ActiveImp implements Serializable, Runnable, Listener {

    public ActiveImp(int id) {
        ID = id;
        running = false;
        initialRun = true;
        firstCycle = true;
        suspendRequested = false;
        perceiving = false;
        talking = true;
        doing = false;
        lock = new Mutex();
    }

    //INTERFACE TO SYNCHRONIZER

    abstract protected void cleanup();

    /**
     * Starts this AgentImps execution
     */
    public void awake() {
        nbTurn = 0;
        Thread t = new Thread(this);
        running = true;
        t.start();
    }

    /**
     * Stops this agent.
     */
    public void finish() {
        //stopping thread
        running = false;
        requestResume();
    }

    //protected void cleanup() {
        //finishing up
    //}

    //THREAD CONTROL

    /**
     * Check whether a request for suspension of the thread associated with
     * this AgentImp instance is pending, and, if so, suspend this thread.
     */
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

    /**
     * Request the suspension of the thread associated with this AgentImp.
     */
    protected void requestSuspend() {
        suspendRequested = true;
    }

    /**
     * Request to wake up the thread associated with this AgentImp, if it is suspended.
     */
    protected void requestResume() {
        suspendRequested = false;
        synchronized (dummy) {
            dummy.notify();
        }
    }

    // INTERFACE TO RUNNING THREAD


    /**
     * The run cycle of the thread associated with this AgentImp.
     */
    public void run() {
        if (initialRun) {
            percept();
            initialRun = false;
        }
        while (running) {
            checkSuspended();
            if (running) {
                if (checkSynchronize()) {
                    synchronize();
                }
                executeCurrentPhase();
            }
        }
        cleanup();
    }

    /**
     * Ask a percept from Environment and retrieve information for
     * synchronisation from the View
     */
    protected void percept() {
        nbTurn++;
        setPerception(getEnvironment().getPerception(getID()));
        //synchroCandidates = getVisibleActiveItems();
        synchroCandidates = getAllActiveItemIDs();
        setSynctime(getEnvironment().getTime());
    }

    protected boolean checkSynchronize() {
        boolean result = firstCycle | perceiving;
        if (firstCycle) {
            firstCycle = false;
        }
        return result;
    }


    protected void executeCurrentPhase() {
        getLock().acquireLock();
        execCurrentPhase();
        //getLock().releaseLock();
    }

    /**
     * Implements the execution of a synchronization phase.
     */
    abstract protected void execCurrentPhase();

    protected void setNextPhase() {
        if (perceiving) {
            perceiving = false;
            talking = true;
        } else if (talking) {
            talking = false;
            doing = true;
        } else if (doing) {
            doing = false;
            perceiving = true;
        }
    }

    void activateNewPhase(boolean environmentPermissionForNextPhase) {
        if (environmentPermissionNeededForNextPhase()) {
            if (environmentPermissionForNextPhase) {
                setNextPhase();
            }
        } else {
            setNextPhase();
        }
        requestResume();
    }

    abstract protected boolean environmentPermissionNeededForNextPhase();

    /**
     * Recompute the syncset for this agentImp's next action-cycle.
     */
    protected void synchronize() {
        getSynchronizer().synchronize(getID(), getSynchroCandidates(),
                                      getSyncTime());
        setSyncSet(getSynchronizer().getSyncSet(getID()));
        Debug.print(this, "Syncset for " + getID() + " recomputed: [");
        for (int j : syncSet) {
            Debug.print(this, "" + j);
        }
        Debug.print(this, "]");

    }

    /**
     * Sends an Outcome to the Environment. This represents the intention of
     * the agent.
     */
    protected void concludePhaseWith(Outcome outcome) {
        if (outcome != null) {
            getEnvironment().collectOutcome(outcome);
        }
        requestSuspend();
        Debug.print(this, "Suspension of agentImp " + getID() + " requested");

        getLock().releaseLock();
    }

    abstract protected void action();


    /**
     *  Returns the IDs of all the ActiveItems in the Environment.
     *
     * @return    An array containing the ID's of all the ActiveItems in the
     *            Environment.
     */
    int[] getAllActiveItemIDs() {
        int[] allIDs = getEnvironment().getActiveItemIDs();
        int[] allIDsWithoutThis = new int[allIDs.length - 1];
        int j = 0;
        for (int allID : allIDs) {
            if (allID != getID()) {
                allIDsWithoutThis[j++] = allID;
            }
        }
        return allIDsWithoutThis;
    }

    /**
     *  Returns the IDs of all the ActiveItems this ActiveImp sees.
     *
     * @return    An array containing the ID's of all the ActiveItems in this
     *            ActiveImp's perception.
     */
    int[] getVisibleActiveItemIDs() {
        try {
            int[] aItemIDs = new int[getEnvironment().getActiveItems().size()];
            int startX = getPerception().getOffsetX();
            int startY = getPerception().getOffsetY();
            int endX = getPerception().getWidth();
            int endY = getPerception().getHeight();
            Iterator<ActiveItem<?>> iter = getEnvironment().getActiveItems().iterator();
            int i = 0;
            while (iter.hasNext()) {
                ActiveItem<?> aItem = iter.next();
                if (aItem.getX() > startX && aItem.getX() < endX &&
                    aItem.getY() > startY && aItem.getY() < endY) {
                    aItemIDs[i++] = aItem.getID();
                }
            }
            return aItemIDs;
        } catch (ClassCastException exc) {
            Debug.alert(this, "ClassCastException: " + exc.getMessage());
            return null;
        }
    }

    // GETTERS & SETTERS

    /**
     * Returns the current local view of the active object
     */
    public Perception getPerception() {
        return percept;
    }

    protected void setPerception(Perception percept) {
        this.percept = percept;
    }

    /**
     * Sets the unique ID number of this active object
     */
    public void setID(int ID) {
        this.ID = ID;
    }

    /**
     * Returns the unique ID number of this active object
     */
    public int getID() {
        return ID;
    }
    /**
     * Return the set of agent- and object-id's that candidate to be synchronized with.
     */
    protected int[] getSynchroCandidates() {
        return synchroCandidates;
    }

    /**
     * Return the syncTime of this AgentImp.
     */
    protected int getSyncTime() {
        return synctime;
    }

    protected int[] getSyncSet() {
        return syncSet;
    }

    private void setSyncSet(int[] set) {
        syncSet = set;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Synchronization getSynchronizer() {
        return synchronizer;
    }

    public void setSynchronizer(Synchronization synchro) {
        synchronizer = synchro;
    }

    protected void setSynctime(int time) {
        synctime = time;
    }

    Mutex getLock() {
        return lock;
    }

    private boolean suspendRequested, firstCycle;
    protected boolean running, initialRun, perceiving, talking, doing;
    private final Object dummy = new Object();
    protected Environment environment;
    private Synchronization synchronizer;
    protected Perception percept;
    protected int nbTurn;
    private int synctime;
    private int[] syncSet;
    protected int[] synchroCandidates;
    protected Mutex lock;
    protected int ID;
}