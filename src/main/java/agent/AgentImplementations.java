package agent;

import environment.Environment;
import environment.Item;
import environment.world.energystation.EnergyStationRep;
import environment.Mail;
import synchronizer.Synchronization;
import util.Debug;
import util.Variables;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Stream;

/**
 * This class is a container for AgentImp. It acts as an interface
 * to the Environment and Synchronizer packages
 */
public class AgentImplementations {

    /**
     * Creates a AgentImplementations container
     */
    public AgentImplementations() {
        agents = new HashMap<>();
        energyStations = new HashMap<>();
    }

    //CONSTRUCTION

    /**
     * Creates the agentImps and related objects
     * postcondition: getNbAgents = count(all id: getAgentImp(id) <> null)
     */
    public void createAgentImps(int nbAgents, String behaviour) {
        try {
            Debug.print(this, "Nb AgentImps = " + nbAgents);
            AgentImp imp;

            String behaviourFile = Variables.IMPLEMENTATIONS_PATH + behaviour + ".txt";

            Debug.print(this, "Behaviour file " + behaviourFile + " opened.");
            for (int i = 0; i < nbAgents; i++) {
                //imp = (AgentImp) reader.readClassConstructor();
                imp = new FileAgentImp(i + 1, behaviourFile);

                imp.setEnvironment(getEnvironment());
                imp.setSynchronizer(getSynchronizer());
                imp.createBehaviour();
                agents.put(imp.getID(), imp);
            }
        } catch (Exception e) {
            Debug.alert(this, e.getMessage());
        }
    }

    public void createObjectImps(Vector<Item<?>> aObjects) {
        int idx = agents.keySet().stream().max(Integer::compare).orElse(0);

        for (Item<?> item : aObjects) {
            if (item.getRepresentation() instanceof EnergyStationRep) {
                EnergyStationImp imp = new EnergyStationImp(++idx, item.getX(), item.getY());
                imp.setEnvironment(getEnvironment());
                imp.setSynchronizer(getSynchronizer());

                energyStations.put(imp.getID(), imp);
            }
        }
    }

    //AGENT IMP METHODS

    /**
     * Starts the given AgentImps execution
     * @param agentID: the ID of the agent to get running
     */
    public void start(int agentID) {
        try {
            getActiveImp(agentID).awake();
        } catch (NullPointerException e) {
            System.err.println("AgentID " + agentID + " does not exist.");
        }
    }

    /**
     *Starts all AgentImps and ActiveObjectImps.
     */
    public void startAllAgentImps() {
        getActiveObjects().forEach(ActiveImp::awake);
    }

    /**
     * Grants permission to the AgentImp with id <code>agentID</code> to execute the next phase in it's action cycle.
     *
     * @param agentID The id of the AgentImp whose next phase should be executed.
     * @param next Whether or not environment allows the AgentImp involved to execute the next phase in line, or rather execute
     *  the same phase again.
     */
    public void activateNewPhase(int agentID, boolean next) {
        getActiveImp(agentID).activateNewPhase(next);
    }

    /**
     * Sends given message to the AgentImp with given ID
     * precondition: getAgentImp(agentID) <> null
     * @param agentID: the ID of the agent to send the message to
     * @param msg: the Mail to send
     */
    public void sendMessage(int agentID, Mail msg) {
        try {
            getAgentImp(agentID).receiveMessage(msg);
        } catch (NullPointerException e) {
            Debug.alert(this, "Message '" + msg + "' not delivered.");
            Debug.alert(this, "AgentID " + agentID + " does not exist.");
        } catch (ClassCastException e) {
            Debug.alert(this, "Only Agents can receive messages.");
        }
    }

    /**
     * Stops the AgentImp with given ID
     * precondition: getAgentImp(agentID) <> null
     *  @param agentID: the ID of the agent to stop
     */
    public void finish(int agentID) {
        getActiveImp(agentID).finish();
    }

    /**
     * Stops all agents
     */
    public void finish() {
        // finish all agents
        getActiveObjects().forEach(ActiveImp::finish);
        agents.clear();
        energyStations.clear();
    }

    /**
     * Needed for Synchronization
     */
    public void acquireLock(int agentID) {
        getActiveImp(agentID).getLock().acquireLock();
    }

    /**
     * Needed for Synchronization
     */
    public void releaseLock(int agentID) {
        try {
            getActiveImp(agentID).getLock().releaseLock();
        } catch (Exception exc) {
            //Normally NullPointerException or ArrayIndexOutOfBoundsException
            //NO-OP
            //This is not allowed to happen. However, this sometimes happens
            //on an abort in the BatchMAS, where it is allowed.
            //Need to find a clean solution however.
            Debug.alert(this, "Cannot release lock for agent " + agentID +
                        "; there is no agent with such an ID");
        }
    }

    //GET AND SETTERS


    private Stream<ActiveImp> getActiveObjects() {
        return Stream.concat(agents.values().stream(), energyStations.values().stream());
    }

    /**
     * Returns the agent or object with given ID
     *
     * @return the Agent or ActiveObject that has the specified ID, or
     *         null if no such Agent of ActiveObject found
     */
    protected ActiveImp getActiveImp(int ID) {
        return getActiveObjects().filter(o -> o.getID() == ID)
                .findFirst().orElse(null);
    }


    /**
     * Return the agent imp corresponding to the given ID.
     */
    protected AgentImp getAgentImp(int ID) {
        return agents.get(ID);
    }


    /**
     * Returns an array with the ID of all agents and objects contained in this AgentImplementations
     */
    public int[] getAllAgentID() {
        return Stream.concat(agents.keySet().stream(), energyStations.keySet().stream())
                .mapToInt(i -> i).toArray();
    }

    /**
     * Returns the number of AgentImps contained in this AgentImplementations
     */
    public int getNbAgents() {
        return agents.size();
    }

    /**
     * Returns the ID of the agentImp with the name <name>
     *
     * @param  name The name of the agent whose ID is being requested
     * @return the ID of the AgentImp with name <name>
     * @throws java.lang.IllegalArgumentException
     *         None of the AgentImps in lnkAgentImps carries the name <name>
     */
    public int getAgentID(String name) throws IllegalArgumentException {
        for (var entry : agents.entrySet()) {
            if (entry.getValue().getName().equals(name)) {
                return entry.getKey();
            }
        }

        throw new IllegalArgumentException("No agentId found matching the name " + name);
    }

    public Synchronization getSynchronizer() {
        return synchronizer;
    }

    public void setSynchronizer(Synchronization synchronizer) {
        this.synchronizer = synchronizer;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    //ATTRIBUTES

    /**
     * The Implementations (ID of an agent mapped to its implementation)
     */
    protected final Map<Integer, AgentImp> agents;
    protected final Map<Integer, EnergyStationImp> energyStations;

    private Synchronization synchronizer;
    private Environment environment;
}
