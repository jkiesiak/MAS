package agent;

import agent.behaviour.Behaviour;
import agent.behaviour.BehaviourState;
import environment.CellPerception;
import environment.Mail;
import environment.Perception;
import environment.world.agent.Agent;
import environment.world.agent.AgentRep;
import environment.world.destination.DestinationRep;
import environment.world.packet.Packet;
import environment.world.packet.PacketRep;
import support.*;
import util.Debug;
import util.MyColor;
import util.event.Event;
import util.event.*;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This class represents the implementation of an Agent in the MAS. It
 * interacts with the Environment for new information of the world by running
 * a separate thread. The agent implementation contains the local view on the
 * world and any received messages. It also has a behaviour.
 */
abstract public class AgentImp extends ActiveImp {

    private ArrayList<DestinationRep> seenDestionations = new ArrayList<DestinationRep>();
    /**
     *  Initialize a new instance of AgentImp with id <ID>. Every new AgentImp
     *  instance is initialized with an empty buffer for incoming messages
     *  and an empty buffer for outgoing mails.
     * @param ID The id of this AgentImp instance.
     * @param maxBeliefs: number of maximum beliefs this AgentImp should be able to store
     * @post new.getName()==name
     * @post new.getID()==ID
     * @post new.getMailBuffer() <> null
     */
    public AgentImp(int ID, int maxBeliefs) {
        super(ID);
        //this.name = name;
        messages = new Vector<>(5);
        nbTurn = 0;
        //synchronize=false;
        outgoingMails = new MailBuffer();

        this.maxBeliefs = maxBeliefs;
        memory = new HashMap<>();

        EventManager.getInstance().addListener(this, AgentActionEvent.class);
    }



    /**
     * Create a mail from this AgentImp to <receiver> and with message <message> and add the resulting mail to the buffer of outgoing
     * mails.
     * @param receiver The representation of the agent to write the message to
     * @param message The message to send
     */
    public final void sendMessage(AgentRep receiver, String message) {
        this.sendMessage(receiver.getName(), message);
    }


    /**
     * Create a mail from this AgentImp to <to> and with message <message> and add the resulting mail to the buffer of outgoing
     * mails.
     * @param to The name of the agent to write the message to
     * @param message The message to send
     */
    private void sendMessage(String to, String message) {
        Debug.print(this, "agentImp" + getID() + "buffers a mail");

        Mail mail = new Mail(getName(), to, message);
        getMailBuffer().addMail(mail);
    }


    /**
     * Do the skip action (influence)
     */
    public final void skip() {
        Debug.print(this, "agent " + getID() + " proposes to do a skip");

        this.concludeWithCondition(this.hasSufficientEnergyDefault(),
                this.generateActionOutcome(new InfSkip(getEnvironment(), getID())));
    }

    /**
     * Creates an influence of the type InfStep and includes it in an
     * ActionOutcome. The Outcome will be sent to the Environment.
     * Influence InfStep: try to step to a target area.
     *
     * @param nx the x coordinate of the area to step to
     * @param ny the y coordinate of the area to step to
     */
    public final void step(int nx, int ny) {
        Debug.print(this, "agent " + getID() + " proposes to do a step");

        this.concludeWithCondition(this.hasSufficientEnergy(this.hasCarry() ? Agent.BATTERY_DECAY_STEP_WITH_CARRY : Agent.BATTERY_DECAY_STEP),
                this.generateActionOutcome(new InfStep(getEnvironment(), nx, ny, getID())));
    }

    /**
     * Creates an influence of the type InfPutPacket and includes it in an
     * ActionOutcome. The Outcome will be sent to the Environment.
     * Influence InfPutPacket: try to put the carry down on target area.
     *
     * @param tx the x coordinate of the target area
     * @param ty the y coordinate of the target area
     */
    public final void putPacket(int tx, int ty) {
        Debug.print(this, "agent " + getID() + " proposes to put a packet");

        this.concludeWithCondition(this.hasSufficientEnergyDefault(),
                this.generateActionOutcome(new InfPutPacket(getEnvironment(), tx, ty, getID())));
    }

    /**
     * Creates an influence of the type InfPickPacket and includes it in an
     * ActionOutcome. The Outcome will be sent to the Environment.
     * Influence InfPickPacket: try to pick up a packet from the specified area.
     *
     * @param fx the x coordinate of the area of the packet
     * @param fy the y coordinate of the area of the packet
     */
    public final void pickPacket(int fx, int fy) {
        Debug.print(this, "agent " + getID() + " proposes to pick a packet");


        var packet = getEnvironment().getPacketWorld().getItem(fx, fy);
        if (packet == null) {
            throw new RuntimeException(String.format("No packet at location (%d,%d).", fx, fy));
        }
        var color = packet.getColor();
        var agentColor = getAgent().getColor();
        if (agentColor.isPresent() && agentColor.get() != color) {
            throw new RuntimeException(String.format("Agent %d cannot pick packet with color %s (agent restricted to color %s).",
                    getID(), MyColor.getName(color), MyColor.getName(agentColor.get())));
        }

        this.concludeWithCondition(this.hasSufficientEnergyDefault(),
                this.generateActionOutcome(new InfPickPacket(getEnvironment(), fx, fy, getID(), color)));
    }

    /**
     * Creates an influence of the type InfPutPheromone and includes it in an
     * ActionOutcome. The Outcome will be sent to the Environment.
     * Influence InfPutPheromone: try to put a pheromone on target area.
     *
     * @param tx       the x coordinate of the target area
     * @param ty       the y coordinate of the target area
     * @param lifetime the lifetime for the pheromone
     */
    public final void putPheromone(int tx, int ty, int lifetime) {
        Debug.print(this, "agent " + getID() + " proposes to put a pheromone");

        this.concludeWithCondition(this.hasSufficientEnergyDefault(),
                this.generateActionOutcome(new InfPutPheromone(getEnvironment(), tx, ty, getID())));
    }

    /**
     * Creates an influence of the type InfPutDirPheromone and includes it in an
     * ActionOutcome. The Outcome will be sent to the Environment.
     * Influence InfPutDirPheromone: try to put a directed pheromone on target area.
     *
     * @param tx       the x coordinate of the target area
     * @param ty       the y coordinate of the target area
     * @param lifetime the lifetime for the pheromone
     * @param target   the area the directed pheromone has to point to
     */
    public final void putDirectedPheromone(int tx, int ty, int lifetime, CellPerception target) {
        Debug.print(this, "agent " + getID() + " proposes to put a directed pheromone");

        this.concludeWithCondition(this.hasSufficientEnergyDefault(),
                this.generateActionOutcome(new InfPutDirPheromone(getEnvironment(), tx, ty, getID(), lifetime, target)));
    }

    /**
     * Creates an influence of the type InfRemovePheromone and includes it
     * in an ActionOutcome. The Outcome will be sent to the Environment.
     * Influence InfRemovePheromone: try to remove a pheromone from target
     * area.
     *
     * @param tx        the x coordinate of the target area
     * @param ty        the y coordinate of the target area
     */
    public final void removePheromone(int tx, int ty) {
        Debug.print(this, "agent " + getID() + " proposes to put a pheromone");

        this.concludeWithCondition(this.hasSufficientEnergyDefault(),
                this.generateActionOutcome(new InfRemovePheromone(getEnvironment(), tx, ty, getID())));
    }

    /**
     * Creates an influence of the type InfPutFlag and includes it in an
     * ActionOutcome. The Outcome will be sent to the Environment.
     * Influence InfPutFlag: try to put a flag with given color on target area.
     *
     * @param tx    the x coordinate of the target area
     * @param ty    the y coordinate of the target area
     * @param color the color of the new Flag
     */
    public final void putFlag(int tx, int ty, Color color) {
        Debug.print(this, "agent " + getID() + " proposes to put a flag");

        this.concludeWithCondition(this.hasSufficientEnergyDefault(),
                this.generateActionOutcome(new InfPutFlag(getEnvironment(), tx, ty, getID(), color)));
    }

    /**
     * Creates an influence of the type InfPutFlag and includes it in an
     * ActionOutcome. The Outcome will be sent to the Environment.
     * Influence InfPutFlag: try to put a flag on target area.
     *
     * @param tx    the x coordinate of the target area
     * @param ty    the y coordinate of the target area
     */
    public final void putFlag(int tx, int ty) {
        this.putFlag(tx, ty, Color.BLACK);
    }

    /**
     * Creates an influence of the type InfPutCrumb and includes it in an
     * ActionOutcome. The Outcome will be sent to the Environment.
     * Influence InfPutCrumb: try to put a crumb (or say: a number of crumbs)
     * on target area.
     *
     * @param tx     the x coordinate of the target area
     * @param ty     the y coordinate of the target area
     * @param number the number of crumbs to put on target area
     */
    public final void putCrumb(int tx, int ty, int number) {
        Debug.print(this, "agent " + getID() + " proposes to put a crumb");

        this.concludeWithCondition(this.hasSufficientEnergyDefault(),
                this.generateActionOutcome(new InfPutCrumb(getEnvironment(), tx, ty, getID(), number)));
    }

    /**
     * Creates an influence of the type InfPickCrumb and includes it in an
     * ActionOutcome. The Outcome will be sent to the Environment.
     * Influence InfPickCrumb: try to pick up a crumb (or say: a number of crumbs)
     * from target area.
     *
     * @param tx     the x coordinate of the target area
     * @param ty     the y coordinate of the target area
     * @param number the number of crumbs to get from target area
     */
    public final void pickCrumb(int tx, int ty, int number) {
        Debug.print(this, "agent " + getID() + " proposes to put a crumb");

        this.concludeWithCondition(this.hasSufficientEnergyDefault(),
                this.generateActionOutcome(new InfPickCrumb(getEnvironment(), tx, ty, getID(), number)));
    }





    private ActionOutcome generateActionOutcome(Influence influence) {
        return new ActionOutcome(getID(), true, getSyncSet(), influence);
    }

    private boolean hasSufficientEnergyDefault() {
        // Default actions such as putting crumbs, skipping, placing flags, ... all require a base amount of energy
        return this.hasSufficientEnergy(Agent.BATTERY_DECAY_SKIP);
    }

    private boolean hasSufficientEnergy(int required) {
        return this.getAgent().getBatteryState() >= required;
    }


    private void concludeWithCondition(boolean condition, Outcome onSuccess) {
        var onFail = this.generateActionOutcome(getAgent().getBatteryState() > 0 ?
                new InfSkip(getEnvironment(), getID()) : new InfNOP(this.getEnvironment()));

        this.concludePhaseWith(condition ? onSuccess : onFail);
    }





    //GET EN SETTERS

    /**
     * Returns the Item this agent is carrying or null
     *
     * @return getAgent().carry()
     */
    public Packet getCarry() {
        return getAgent().carry();
    }

    /**
     * Returns whether the agent is carrying something
     *
     * @return <code>true</code> if the agent has a carry, false otherwise
     */
    public boolean hasCarry() {
        return getAgent().carry() != null;
    }

    /**
     * Returns a copy of the Agent Item which represents this AgentImp in the
     * Environment
     *
     * @return getEnvironment().getAgentWorld().getAgent(getID())
     */
    protected Agent getAgent() {
        return getEnvironment().getAgentWorld().getAgent(getID());
    }

    /**
     * Returns the X coordinate of this agent
     */
    public int getX() {
        return getAgent().getX();
    }

    /**
     * Returns the Y coordinate of this agent
     */
    public int getY() {
        return getAgent().getY();
    }

    /**
     *Return the buffer for **outgoing** mails of this AgentImp.
     */
    private MailBuffer getMailBuffer() {
        return outgoingMails;
    }

    /**
     * Returns the current Behaviour
     */
    public Behaviour getCurrentBehaviour() {
        return lnkBehaviourState.getBehaviour();
    }

    /**
     * Assigns a new BehaviourState to this Agent implementation
     */
    public void setCurrentBehaviourState(BehaviourState bs) {
        lnkBehaviourState = bs;
    }



    /**
     * Returns the name of this agent
     */
    public String getName() {
        return getAgent().getName(); //""+getID();
    }


    /**
     * Indicates the number of synchronized cycles this agent has run through
     */
    public int getNbTurns() {
        return nbTurn;
    }

    /**
     *	Gets message at index from the message queue
     * @param index the index of the required message
     */
    public Mail getMessage(int index) {
        return messages.elementAt(index);
    }

    /**
     * Number of messages in the incoming message queue
     */
    public int getNbMessages() {
        return messages.size();
    }

    /**
     * Removes the message at index from the incoming message queue
     * @param index the index of the removed message
     */
    public void removeMessage(int index) {
        messages.removeElementAt(index);
    }


    /**
     * Retrieve the all the incoming messages.
     * @return A list with the received messages from other agents.
     */
    public Collection<Mail> getMessages() {
        return messages;
    }

    /**
     * Clear the incoming message queue.
     */
    public void clearMessages() {
        messages.clear();
    }





    public int getBatteryState() {
        return this.getAgent().getBatteryState();
    }


    /**
     * Returns the previous area the agent of this AgentImp stood on.
     *
     * @return a CellPerception of the previous position
     */
    public CellPerception getLastArea() {
        int lastX = getAgent().getLastX();
        int lastY = getAgent().getLastY();
        return getPerception().getCellPerceptionOnAbsPos(lastX, lastY);
    }

    /**
     * Returns whether this agent sees a destination of the same color of his
     * carry.
     *
     * @return <code>true</code> if this agent has a destination in sight
     * of the color matching to the color of the packet he's carrying,
     * <code>false</code> otherwise.
     */
    public boolean seeDestination() {
        int vw = getPerception().getWidth();
        int vh = getPerception().getHeight();
        if (hasCarry()) {
            for (int i = 0; i < vw; i++) {
                for (int j = 0; j < vh; j++) {
                    DestinationRep destRep = getPerception().getCellAt(i, j).getRepOfType(DestinationRep.class);
                    if (destRep != null && destRep.getColor() == getCarry().getColor()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /**
     * Returns all visible destinations regardless of their color
     *
     * @return <code>DestinationRep</code> closest destination rep. of the same color
     */
    public ArrayList<DestinationRep> getAllVisibleDestinations() {
        int vw = getPerception().getWidth();
        int vh = getPerception().getHeight();

        ArrayList<DestinationRep> visibleDestionations  = new ArrayList<DestinationRep>();

        if (hasCarry()) {
            for (int i = 0; i < vw; i++) {
                for (int j = 0; j < vh; j++) {
                    DestinationRep destRep = getPerception().getCellAt(i, j).getRepOfType(DestinationRep.class);
                    if (destRep != null) {
                        visibleDestionations.add(destRep);
                    }
                }
            }
        }
        return visibleDestionations;
    }
    /**
     * Returns closest destination that this agent sees of the same color of his
     * carry.
     *
     * @return <code>DestinationRep</code> closest destination rep. of the same color
     */
    public DestinationRep getClosestVisibleDestination() {
        int vw = getPerception().getWidth();
        int vh = getPerception().getHeight();

        DestinationRep closestDestination = null;
        int minDist = Integer.MAX_VALUE;

        if (hasCarry()) {
            for (int i = 0; i < vw; i++) {
                for (int j = 0; j < vh; j++) {
                    DestinationRep destRep = getPerception().getCellAt(i, j).getRepOfType(DestinationRep.class);
                    if (destRep != null && destRep.getColor() == getCarry().getColor()) {
                        DestinationRep tempDest = getPerception().getCellAt(i, j).getRepOfType(DestinationRep.class);
                        if(Perception.manhattanDistance(tempDest.getX(), tempDest.getY(), getX(), getY()) < minDist){
                            minDist = Perception.manhattanDistance(tempDest.getX(), tempDest.getY(), getX(), getY());
                            closestDestination = tempDest;
                        }
                    }
                }
            }
        }
        return closestDestination;
    }

    /**
     * Returns closest destination that this agent remembers of the same color of his
     * carry.
     *
     * @return <code>DestinationRep</code> closest remembered destination rep. of the same color
     */
    public DestinationRep getClosestDestinationMemory(){
        DestinationRep closestRep = null;
        int minDist = Integer.MAX_VALUE;

        for(DestinationRep dest : seenDestionations){
            if(dest.getColor() == getCarry().getColor()){
                int packageDist = Perception.manhattanDistance(getX(), getY(), dest.getX(), dest.getY());
                if(closestRep == null || packageDist < minDist ){
                    minDist = packageDist;
                    closestRep = dest;
                }
            }
        }
        return closestRep;
    }

    /**
     * Add destination to memory of destinations, if the destination is already remembered, do nothing.
     */
    public void addToMemory(DestinationRep dest){
        if(!seenDestionations.contains(dest))
            seenDestionations.add(dest);

        return;
    }

    /**
     * Returns whether this agent can see a packet.
     *
     * @return <code>true</code> if this agent has a packet in sight, <code>false</code> otherwise.
     */
    public boolean seePacket() {
        if (hasCarry()) return false;

        int vw = getPerception().getWidth();
        int vh = getPerception().getHeight();
        for (int i = 0; i < vw; i++) {
            for (int j = 0; j < vh; j++) {
                if (getPerception().getCellAt(i, j).getRepOfType(PacketRep.class) != null) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Returns closest visible packet (Manhattan distance)
     *
     * @return <code>PackageRep</code> closest visible packet
     */
    public PacketRep getClosestVisiblePacket(){

        PacketRep closestPacket = null;
        int minDist = Integer.MAX_VALUE;
        int vw = getPerception().getWidth();
        int vh = getPerception().getHeight();
        List<PacketRep> visiblePackets = new ArrayList<PacketRep>();
        for (int i = 0; i < vw; i++) {
            for (int j = 0; j < vh; j++) {
                if (getPerception().getCellAt(i, j).getRepOfType(PacketRep.class) != null) {
                    PacketRep tempPacket = getPerception().getCellAt(i, j).getRepOfType(PacketRep.class);
                    if(Perception.manhattanDistance(tempPacket.getX(), tempPacket.getY(), getX(), getY()) < minDist){
                        minDist = Perception.manhattanDistance(tempPacket.getX(), tempPacket.getY(), getX(), getY());
                        closestPacket = tempPacket;
                    }
                }
            }
        }
        return closestPacket;
    }

    /**
     * Returns visible packets sorted by their Manhattan distance
     *
     * @return <code>visiblePackets</code> sorted visible packets
     */
    public List<PacketRep> getVisiblePacketsSorted(){
        int vw = getPerception().getWidth();
        int vh = getPerception().getHeight();
        List<PacketRep> visiblePackets = new ArrayList<PacketRep>();
        for (int i = 0; i < vw; i++) {
            for (int j = 0; j < vh; j++) {
                if (getPerception().getCellAt(i, j).getRepOfType(PacketRep.class) != null) {
                    visiblePackets.add(getPerception().getCellAt(i, j).getRepOfType(PacketRep.class));
                }
            }
        }

        visiblePackets.sort(new Comparator<PacketRep>(){
            @Override
            public int compare(PacketRep p1, PacketRep p2){
                return ((Integer)Perception.manhattanDistance(p1.getX(), p1.getY(), getX(), getY()))
                        .compareTo(Perception.manhattanDistance(p2.getX(), p2.getY(), getX(), getY()));
            }
        });

        return visiblePackets;
    }

    public void catchEvent(Event e) {
        if (e instanceof AgentActionEvent) {
            AgentActionEvent aae = (AgentActionEvent) e;
            if (aae.getAgent() == getAgent()) {
                var agent = this.getAgent();

                if (aae.getAction() == AgentActionEvent.STEP) {
                    if (hasCarry()) {
                        agent.updateBatteryState(-Agent.BATTERY_DECAY_STEP_WITH_CARRY);
                    } else {
                        agent.updateBatteryState(-Agent.BATTERY_DECAY_STEP);
                    }
                } else if (aae.getAction() == AgentActionEvent.LOADENERGY) {
                    agent.updateBatteryState(aae.getValue());
                } else if (aae.getAction() == AgentActionEvent.IDLE_ENERGY) {
                    agent.updateBatteryState(-Agent.BATTERY_DECAY_SKIP);
                }
            }
        }
    }






    // Memory related methods

    /**
     * Adds a memory fragment to this agent (if memory is not full).
     *
     * @param key: the key associated with the memory fragment
     * @param data: the memory fragment itself
     * @precondition getNbBeliefs < getMaxNbBeliefs
     */
    public void addMemoryFragment(String key, String data) {
        if (getNbMemoryFragments() < getMaxNbMemoryFragments()) {
            memory.put(key, data);
        }
    }

    /**
     * Removes a memory fragment with given key from this agents memory.
     * @param key: the key of the belief to remove
     */
    public void removeMemoryFragment(String key) {
        memory.remove(key);
    }

    /**
     *  Gets a memory fragment with given key from this agents memory
     *  @param key: the key of the memory fragment to retrieve
     */
    public String getMemoryFragment(String key) {
        return memory.get(key);
    }

    /**
     * Returns the current number of memory fragments in memory of this agent
     */
    public int getNbMemoryFragments() {
        return memory.size();
    }

    /**
     * Returns the maximum number of memory fragments for this agent
     */
    public int getMaxNbMemoryFragments() {
        return maxBeliefs;
    }






    /**
     * Stops this agent. See superclass. Finishes also beliefBase.
     */
    public void finish() {
        super.finish();
        memory.clear();
        memory = null;
    }




    // Other

    /**
     *Deliver the current contents of this AgentImp's mailBuffer and request for another communication-phase.
     */
    public final void continueCommunication() {
        CommunicationOutcome outcome =
                new CommunicationOutcome(getID(), true, getSyncSet(), "CC",
                        (MailBuffer) getMailBuffer().clone());
        Debug.print(this, "agentImp " + getID() + " sending his communication to ");

        Mail mail;
        for (int i = 0; i < outcome.getMailBuffer().getMails().length; i++) {
            mail = outcome.getMailBuffer().getMails()[i];
            Debug.print(this, mail.getTo() + "with the following message: " + mail.getMessage() + "\n");
        }

        getMailBuffer().clear();
        concludePhaseWith(outcome);
    }

    /**
     * Deliver the current contents of this AgentImp's mailBuffer and request to enter the action-phase.
     */
    public final void closeCommunication() {
        CommunicationOutcome outcome = new CommunicationOutcome(getID(), true,
                getSyncSet(), "EOC", (MailBuffer) getMailBuffer().clone());

        Debug.print(this, "agentImp " + getID() + " sending his communication to: ");

        Mail mail;
        for (int i = 0; i < outcome.getMailBuffer().getMails().length; i++) {
            mail = outcome.getMailBuffer().getMails()[i];
            Debug.print(this, mail.getFrom() + " sends to " + mail.getTo() +
                    " the following message: " + mail.getMessage());
        }
        getMailBuffer().clear();
        concludePhaseWith(outcome);
    }

    /**
     * Returns true if agent is in communication phase
     *
     * @return true if agent is in communication phase, false otherwise
     */
    public boolean inCommPhase() {
        return talking;
    }

    /**
     * Returns true if agent is in action phase
     *
     * @return true if agent is in action phase, false otherwise
     */
    public boolean inActionPhase() {
        return doing;
    }

    /**
     * Triggers an update of the behaviour. If necessary the agent changes
     * behaviour.
     */
    public void updateBehaviour() {
        Debug.print(this, "Agent " + getName() + " testing behaviours");
        Debug.print(this, "Agent " + getName() + " from " + getCurrentBehaviour().getClass().getName());
        lnkBehaviourState.testBehaviourChanges();
        Debug.print(this, "Agent " + getName() + " to " + getCurrentBehaviour().getClass().getName());
        BehaviourChangeEvent event = new BehaviourChangeEvent(this);
        event.setAgent(getID());
        event.setBehavName(getCurrentBehaviour().getClass().getSimpleName());
        Debug.print(this, event.getBehavName());
        EventManager.getInstance().throwEvent(event);
    }



    /**
     *  Creates the graph of behaviours and changes for this
     *  agentImplementation.
     * @postconditions getCurrentBehaviour <> null
     */
    public abstract void createBehaviour();


    //INTERFACE TO SYNCHRONIZER

    protected void cleanup() {
        //finishing up
        messages.removeAllElements();
        lnkBehaviourState.finish();
        lnkBehaviourState = null;
    }

    /**
     * Adds a message to the messages queue
     */
    public void receiveMessage(Mail msg) {
        messages.addElement(msg);
    }



    // AGENT IMP INTERFACE TO RUNNING THREAD

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
     * Implements the execution of a synchronization phase.
     */
    protected void execCurrentPhase() {
        if (perceiving) {
            Debug.print(this, "AgentImp " + getID() + " starting perception");

            perception();
        } else if (talking) {
            Debug.print(this, "AgentImp " + getID() + " starting to talk");

            communication();
        } else if (doing) {
            Debug.print(this, "AgentImp " + getID() + " starting to do");

            action();
            AgentHandledEvent event = new AgentHandledEvent(this);
            event.setAgent(this);
            EventManager.getInstance().throwEvent(event);
        }
    }

    protected boolean environmentPermissionNeededForNextPhase() {
        return true;
    }

    /**
     * Perceive and notify Environment of the conclusion of this AgentImp's perception.
     */
    private void perception() {
        percept();
        PerceptionOutcome outcome = new PerceptionOutcome(getID(), true, getSyncSet());
        concludePhaseWith(outcome);
    }

    /**
     * Implements the communication phase
     */
    protected void communication() {
        updateBehaviour();
        getCurrentBehaviour().handle(this);
    }

    /**
     * Implements the action phase
     */
    protected void action() {
        getCurrentBehaviour().handle(this);
    }








    //ATTRIBUTES

    /**
     * @directed
     * @supplierCardinality 1
     * @label current behaviour
     */
    private BehaviourState lnkBehaviourState;
    private final Vector<Mail> messages;
    private final MailBuffer outgoingMails;


    /**
     * The memory of an agent has the form of a key mapped to a memory fragment (represented as String)
     * e.g.  "target" -> "3, 4"
     */
    private Map<String, String> memory;
    private final int maxBeliefs;

    public static final int DEFAULT_BELIEFS = 10;


}