package environment;

import agent.AgentImplementations;
import util.Debug;
import support.Outcome;

import java.util.Vector;

/**
 *A class for objects responsible for the handling of Outcomes, i.e. completions of any phase in the action-cycle. The collector
 * of a mas is a capital synchronization tool, together with the spheres it manages. An incoming outcome is handled by either
 * integrating it and its syncset in the existing spheres, or by creating a new sphere. Further responsability for the handling of
 * the incoming outcome is left to the spheres.
 */
public class Collector implements Runnable {

    private Sphere[] spheres;
    private int nbSpheres;
    private final Vector<Outcome> inBuffer;
    private int nbInBuffer;

    private final Object dummy = new Object();
    private boolean suspendRequested;
    private boolean running;
    private int nbMerges;

    private final EOPHandler eOPHandler;
    private final Reactor reactor;
    private final PostalService postalService;
    private final AgentImplementations agentImplementations;

    /**
     *Initialize a new Collector with an empty set of Spheres and holding references to the three handlers the
     *environment. At initalization time, a new Thread is created associated with the new Collector instance.
     *@param eOPHandler The handler for end of perception tokens.
     *@param reactor The reactor of this environment
     *@param postalService The postalService of this environment
     *@post new.getNbSpheres()==0.
     *@post new.getAgentImplementations()==agentImplementations
     *@post new.getEOPHandler()==eOPHandler
     *@post new.getReactor()==reactor
     *@post new.getPostalService()==postalService
     */
    public Collector(AgentImplementations agentImplementations,
            EOPHandler eOPHandler,
            Reactor reactor,
            PostalService postalService) {
        spheres = new Sphere[0];
        nbSpheres = 0;
        inBuffer = new Vector<>(0);
        nbInBuffer = 0;
        nbMerges = 0;
        this.agentImplementations = agentImplementations;
        this.eOPHandler = eOPHandler;
        this.reactor = reactor;
        this.postalService = postalService;
        Thread t = new Thread(this);
        suspendRequested = true;
        t.start();
    }

    /**
     *Create a new sphere for the outcome <outc>. The set of spheres managed by this collector is extended by the new sphere.
     *@param outc The outcome for which a new sphere is initialized.
     *@post new.getNbSpheres()==getNbSpheres()+1
     */
    protected Sphere makeNewSphere(Outcome outc) {
        Sphere[] temp = new Sphere[nbSpheres + 1];
        if (nbSpheres >= 0) {
            System.arraycopy(spheres, 0, temp, 0, nbSpheres);
        }

        Sphere novel = new Sphere(this, getAgentImplementations(),
                                  getEOPHandler(), getPostalService(),
                                  getReactor());
        temp[nbSpheres] = novel;
        nbSpheres++;
        spheres = temp;
        novel.integrate(outc);
        return novel;
    }

    /**
     *Deposit the outcome <outcome> in this Collector's buffer and wake up the thread the latter is associated with, if sleeping.
     * @param outcome The outcome to be deposited in the Collector's buffer.
     * @post new.getNbInBuffer()==getNbInBuffer()+1
     */
    public synchronized void collectOutcome(Outcome outcome) {
        inBuffer.addElement(outcome);
        nbInBuffer++;
        if (suspendRequested) {
            requestResume();
        }
    }

    /**
     *  If not asleep, monitor the buffer of this Collector for outcomes
     *  to be handled.
     */
    public void run() {
        running = true;
        while (running) {
            checkSuspended();
            if (running) {
                monitorInBuffer();
            }
        }
    }

    /**
     *Process the outcome deposited in this Collector's buffer.
     */
    synchronized void monitorInBuffer() {
        if (inBuffer.isEmpty()) {
            requestSuspend();
        } else {
            Outcome first = null;
            try {
                first = inBuffer.elementAt(0);
                inBuffer.removeElementAt(0);
            } catch (IndexOutOfBoundsException exc) {
                exc.printStackTrace();
            }
            synchronized (first) {
                processOutcome(first);
            }
        }
    }

    private void checkSuspended() {
        try {
            synchronized (dummy) {
                while (suspendRequested) {
                    Debug.print(this, "Collectorthread puts himself to sleep");
                    dummy.wait();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestSuspend() {
        suspendRequested = true;
    }

    public void requestResume() {
        suspendRequested = false;
        synchronized (dummy) {
            Debug.print(this, "Collectorthread woken up by incoming outcome");
            dummy.notify();
        }
    }

    /**
     *Processes the Outcome <outcome>. This includes the creation of a new sphere or the merging of existing spheres, and integrating
     * the outcome in the new, resp. merged spheres. Also, the modified spheres are checked whether all its members have acted:
     * if so, that sphere is told do export itself to a matching handler.
     */
    protected void processOutcome(Outcome outcome) {
        Debug.print(this, "Collector starts dealing with the " + outcome.getType() + " from agent " + outcome.getAgentID());
        Debug.print(this, "Status of collector at start of processing outcome: ");
        printSphereSet();

        Sphere[] toBeMerged = getSpheresToBeMerged(outcome);
        Sphere changed;
        if (toBeMerged.length == 0) {
            synchronized (spheres) {
                changed = makeNewSphere(outcome);
            }
            Debug.print(this, "New Sphere for outcome made; after this, ");

        } else {
            changed = merge(toBeMerged);
            changed.integrate(outcome);
            Debug.print(this, "Existing spheres have been merged ");
        }
        printSphereSet();

        if (changed.allActed()) {
            changed.handleFullSphere();
        }
        Debug.print(this, "Processing the outcome has finished!!!");
        printSphereSet();
    }

    /**
     *Merges the spheres <toBeMerged>. One of the spheres to be merged is thereby extended with the contents of the others and
     *the latter are removed from the set of Spheres managed by this Collector.
     *@param toBeMerged The set of spheres to be merged.
     *@pre toBeMerged.length > 0
     */
    protected Sphere merge(Sphere[] toBeMerged) {
        Sphere basicSphere = toBeMerged[0];
        if (toBeMerged.length > 1) {
            for (Sphere sphere : toBeMerged) {
                basicSphere.incorporate(sphere);
                sphere.clear();
                nbMerges++;
            }
        }
        return basicSphere;
    }

    /**
     *Removes the sphere <toBeRemoved> from the set of spheres managed by this Collector.
     *@param toBeRemoved The Sphere to be removed from the set of Spheres managed by this Collector.
     *@pre for some i in 0..nbSpheres-1: toBeRemoved == spheres[i]
     *@post for each i in 0..nbSpheres-1: toBeRemoved != spheres[i]
     *@post new.nbSpheres == nbSpheres-1
     */
    protected synchronized void removeSphere(Sphere toBeRemoved) {
        Sphere[] temp = new Sphere[spheres.length - 1];
        int i = 0, j = i;
        while (i < spheres.length) {
            if (spheres[i] != toBeRemoved) {
                temp[j] = spheres[i];
                j++;
            }
            i++;
        }
        nbSpheres--;
        spheres = temp;
    }

    /**
     *Return the set of spheres that need to be merged for processing the Outcome <outc>.
     *@param outc The Outcome to be processed by this Collector.
         *@return The set of spheres that need to be merged in order to process <outc>.
     *@post Each element in the set of spheres returned by this method contains either the agent-ID of <outc> or the id of one of
     *      its syncSet-members.
     *@post None of the spheres managed by this collector that are not returned by this method, contain either the agent-ID of
     *      <outc> or the id of one of its syncSet-members.
     */
    protected Sphere[] getSpheresToBeMerged(Outcome outc) {
        int[] tempSet = new int[ (outc.getSyncSet()).length + 1];
        tempSet[0] = outc.getAgentID();
        for (int k = 0; k < outc.getSyncSet().length; k++) {
            tempSet[k + 1] = outc.getSyncSet()[k];
        }
        Vector<Integer> temp = new Vector<>(0);
        int i = 0;
        while (i < tempSet.length) {
            int j = 0;
            boolean found = false;
            while (!found && j < spheres.length) {
                if (spheres[j].containsOutcomeOf(tempSet[i])) {
                    found = true;
                    boolean notYetThere = true;
                    for (int l = 0; notYetThere && l < temp.size(); l++) {
                        if (j == temp.elementAt(l)) {
                            notYetThere = false;
                        }
                    }
                    if (notYetThere) {
                        temp.addElement(j);
                    }
                }
                j++;
            }
            i++;
        }
        Sphere[] result = new Sphere[temp.size()];
        for (int k = 0; k < result.length; k++) {
            result[k] = spheres[temp.elementAt(k)];
        }
        return result;
    }


    public void printSphereSet() {
        Debug.print(this, "Collector now manages " + nbSpheres + " spheres:");
        for (int i = 0; i < spheres.length; i++) {
            Debug.print(this, "  Sphere number " + i + " :");
            spheres[i].printSphere();
        }
    }


    int getNbSpheres() {
        return nbSpheres;
    }

    int getNbInBuffer() {
        return nbInBuffer;
    }

    /**
     *Return the Reactor of this environment.
     */
    Reactor getReactor() {
        return reactor;
    }

    /**
     *Return the PostalService of this environment.
     */
    PostalService getPostalService() {
        return postalService;
    }

    /**
     *Return the end of perception handler of this environment.
     */
    EOPHandler getEOPHandler() {
        return eOPHandler;
    }

    AgentImplementations getAgentImplementations() {
        return agentImplementations;
    }

    Sphere[] getSpheres() {
        return spheres;
    }

    public void finish() {
        running = false;
        requestResume();

    }
}
