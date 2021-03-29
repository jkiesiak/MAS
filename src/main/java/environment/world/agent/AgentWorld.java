package environment.world.agent;

import environment.Coordinate;
import environment.World;
import support.InfEnergy;
import support.InfNOP;
import support.InfStep;
import support.Influence;
import util.Debug;
import util.event.AgentActionEvent;
import util.event.EventManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *  A class for an AgentWorld, being a layer of the total world, that contains
 *  Agents.
 */

public class AgentWorld extends World<Agent> {

    //--------------------------------------------------------------------------
    //		CONSTRUCTOR
    //--------------------------------------------------------------------------

    /**
     *  Initializes a new AgentWorld instance
     */
    public AgentWorld() {
        super();
    }

    //--------------------------------------------------------------------------
    //		INSPECTORS
    //--------------------------------------------------------------------------

    /**
     *  Gets an array containing the Agents that are in this AgentWorld
     *
     * @return    This AgentWorld's agents
     */
    public List<Agent> getAgents() {
        return agents;
    }

    /**
     *  Gets the total amount of agents that are in this AgentWorld
     *
     * @return    This AgentWorld's nbAgents
     */
    public int getNbAgents() {
        return agents.size();
    }

    public String toString() {
        return "AgentWorld";
    }

    //--------------------------------------------------------------------------
    //		MUTATORS
    //--------------------------------------------------------------------------

    //--------------------------------------------------------------------------


    /**
     *  puts each agent in the given collection at it's correct location in this AgentWorld
     *
     * @param  agents   the array of Agents that should be placed
     */
    protected void putAgents(Collection<Agent> agents) {
        this.agents = new ArrayList<>(agents);
        for (Agent agent : agents) {
            putItem(agent);
        }
    }

    /**
     *  Make a given Agent in this AgentWorld step to the given coordinates
     *
     * @param  tx       x coordinate
     * @param  ty       y coordinate
     * @param  agent    The ID of the agent that is to step
     * @post    'agent' is situated on coordinate tx,ty
     * @post    agent gets informed of its new coordinates
     * @post    The agent's move is shown on the gui
     */
    protected synchronized void step(int tx, int ty, int agent) {
        AgentActionEvent event = new AgentActionEvent(this);
        event.setAction(AgentActionEvent.STEP);
        event.setFrom(getAgent(agent).getX(), getAgent(agent).getY());
        event.setTo(tx, ty);
        event.setPacket(getAgent(agent).carry());
        event.setAgent(getAgent(agent));

        moveAgent(getAgent(agent).getX(), getAgent(agent).getY(), tx, ty,
                  getAgent(agent));
        getAgent(agent).consumeX(tx);
        getAgent(agent).consumeY(ty);

        EventManager.getInstance().throwEvent(event);
    }

    protected synchronized void loadEnergy(int x, int y, int loadAmount) {
        if (inBounds(x, y) && getItem(x, y) != null) {
            AgentActionEvent event = new AgentActionEvent(this);
            event.setAction(AgentActionEvent.LOADENERGY);
            event.setAgent(getAgent(getItem(x, y).getID()));
            event.setValue(loadAmount);
            EventManager.getInstance().throwEvent(event);
        }
    }

    protected synchronized void idleEnergy(int agent) {
        AgentActionEvent event = new AgentActionEvent(this);
        event.setAction(AgentActionEvent.IDLE_ENERGY);
        event.setAgent(getAgent(agent));
        EventManager.getInstance().throwEvent(event);
    }

    /**
     *  Move a given Agent from and to the given coordinates
     *
     * @param   fromX    X coordinate to be moved from
     * @param   fromY    Y coordinate to be moved from
     * @param   toX      X coordinate to be moved to
     * @param   toY      Y coordinate to be moved to
     * @param   agent    The agent that is to be moved
     * @post    The position where the agent stood is free (A Free Item)
     * @post    The position where the agent moved to is taken up by the agent
     * @post    If the agent were carrying a packet, that packet gets notified
     *          of it's new coordinates.
     */
    protected void moveAgent(int fromX, int fromY, int toX, int toY,
                             Agent agent) {
        putItem(toX, toY, getItem(fromX, fromY)); // move the agent
        free(fromX, fromY); // make it's origin free
        if ( agent.carryPacket()) {
            agent.carry().moveTo(toX, toY);
        }
    }

    /**
     *  Gets the agent with the given 'ID'
     *
     * @param  ID   the number we gave to the agent we are looking for
     * @return      The agent with the given ID in this AgentWorld.
     *              Or, if there is no agent with that ID, we return null
     */
    public Agent getAgent(int ID) {
        return agents.stream().filter(a -> a.getID() == ID)
            .findFirst()
            .orElse(null);
    }

    /**
     *  Brings a given influence in effect in this world.
     *  This method knows the effects of certain influences and realizes them
     *  in this world.
     *
     * @param inf  the influence to bring in effect
     */
    @Override
    protected void effectuate(Influence inf) {
        if (inf instanceof InfStep) {
            step(inf.getX(), inf.getY(), inf.getID());
        } else if (inf instanceof InfEnergy) {
            loadEnergy(inf.getX(), inf.getY(), ((InfEnergy) inf).getLoadAmount());
        } else if (!(inf instanceof InfNOP)) {
            idleEnergy(inf.getID());
        }
    }

    /**
     * Adds a number of Agents randomly to this AgentWorld.
     *
     * @param nbAgents the number of agents to add to this world
     * @param view     the view range for the agents to add
     */
    public void createWorld(int nbAgents, int view) {
        List<Agent> agents = new ArrayList<>();

        for (int i = 0; i < nbAgents; i++) {
            boolean ok = false;
            while (!ok) {
                Coordinate c = getRandomCoordinate(getEnvironment().getWidth(),
                                                   getEnvironment().getHeight());
                if (getEnvironment().isFreePos(c.getX(), c.getY())) {
                    Agent agent = new Agent(c.getX(), c.getY(), getEnvironment(), view,
                                            i + 1, String.valueOf(i + 1));
                    placeItem(agent);
                    agents.add(agent);
                    ok = true;
                }
            }
        }
        this.agents = agents;
    }

    /**
     * Adds Agents to this AgentWorld.
     *
     * @param agents  a collection containing the agents to place in this world
     */
    @Override
    public void placeItems(Collection<Agent> agents) {
        for (Agent agent : agents) {
            placeItem(agent);
        }
        try {
            this.agents = new ArrayList<>(agents);
        } catch (ClassCastException exc) {
            Debug.alert(this, "Can only place Agents in AgentWorld.");
        }
    }

    /**
     * Adds an Agent to this AgentWorld.
     *
     * @param agent  the agent to place in this world
     */
    @Override
    public void placeItem(Agent agent) {
        try {
            putItem(agent);
            getEnvironment().addActiveItem(agent);
        } catch (ClassCastException exc) {
            Debug.alert(this, "Can only place an Agent in AgentWorld.");
        }
    }

    //--------------------------------------------------------------------------
    //		ATTRIBUTES
    //--------------------------------------------------------------------------

    private List<Agent> agents;
}
