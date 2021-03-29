package environment.world.agent;

import environment.ActiveItem;
import environment.Environment;
import environment.world.packet.Packet;
import gui.video.Drawer;
import util.MyColor;
import util.Debug;
import util.event.EnergyUpdateEvent;
import util.event.EventManager;

import java.awt.*;
import java.util.Optional;

/**
 * A class for agents, items that represent the body of agents in the
 * environment.
 */

public class Agent extends ActiveItem<AgentRep> {

    private final String name;
    private int points;
    private Packet carry;
    private Environment env;
    private final Color color;

    /**
     * Initializes a new Agent instance.
     *
     * @param  x        x-coordinate of the agent
     * @param  y        y-coordinate of the agent
     * @param  environ  the environment in which this agent is situated
     * @param  view     this agent's view range
     * @param  ID       this agent's ID
     * @param  name     this agent's name
     */
    public Agent(int x, int y, Environment environ, int view, int ID, String name, Color color) {
        super(x, y, view, ID);
        this.name = name;
        this.env = environ;
        this.batteryState = BATTERY_START;
        this.color = color;
        Debug.print(this, "Agent created at " + x + " " + y);
    }

    public Agent(int x, int y, Environment environ, int view, int ID, String name) {
        this(x, y, environ, view, ID, name, null);
    }


    /**
     * Initializes a new Agent instance.
     *
     * @param  x        x-coordinate of the agent
     * @param  y        y-coordinate of the agent
     * @param  view     this agent's view range
     * @param  ID       this agent's ID
     * @param  name     this agent's name
     */
    public Agent(int x, int y, int view, int ID, String name) {
        this(x, y, null, view, ID, name, null);
    }

    /**
     * Initializes a new Agent instance.
     *
     * @param  x        x-coordinate of the agent
     * @param  y        y-coordinate of the agent
     * @param  view     this agent's view range
     * @param  ID       this agent's ID
     * @param  name     this agent's name
     */
    public Agent(int x, int y, int view, int ID, String name, String color) {
        this(x, y, null, view, ID, name, MyColor.getColor(color));
    }

    /**
     * Gets the Environment of this Agent.
     *
     * @return    This agent's environment
     */
    public Environment getEnvironment() {
        return env;
    }

    /**
     * Sets the Environment of this Agent.
     *
     * @param  environ  The new environment value
     */
    public void setEnvironment(Environment environ) {
        this.env = environ;
    }

    /**
     * Lets this agent consume a given Packet.
     *
     * @param  p    the Packet to consume
     * @post        new.carry() == p
     */
    public void consume(Packet p) {
        carry = p;
        //when dropped packet, forget orientation (last visited area)
        if (p == null) {
            setLastX(-1);
            setLastY(-1);
        }
    }

    /**
     * Lets this agent consume a new X-coordinate.
     *
     * @param  nx   x-coordinate
     * @post        new.getX() = nx
     */
    protected void consumeX(int nx) {
        setLastX(getX());
        setX(nx);
    }

    /**
     * Lets this agent consume a new Y-coordinate.
     *
     * @param  ny   y-coordinate
     * @post        new.getY() = ny
     */
    protected void consumeY(int ny) {
        setLastY(getY());
        setY(ny);
    }

    /**
     * Returns the packet this agent is carrying. If the agent isn't carrying
     * any packet, it returns 'null'.
     *
     * @return  the value of carry
     */
    public Packet carry() {
        return carry;
    }


    /**
     * Check if the agent is carrying something.
     *
     * @return True if the agent carries something, false otherwise.
     */
    public boolean hasCarry() {
        return this.carry() != null;
    }

    /**
     * Returns 'true' if this agent is carrying a packet.
     *
     * @return  true if carry() != null
     */
    protected boolean carryPacket() {
        return carry() != null;
    }

    /**
     * Gets the name of this Agent.
     *
     * @return    The name value
     */
    public String getName() {
        return name;
    }


    /**
     * Get the color this Agent is limited to.
     *
     * @return The color this agent is limited to, Optional.empty() if the agent has no color restriction.
     */
    public Optional<Color> getColor() {
        return Optional.ofNullable(this.color);
    }


    /**
     * Returns a representation of this Agent.
     *
     * @return the representation of this agent with a representation of anything it carries
     */
    @Override
    public AgentRep getRepresentation() {
        AgentRep aRep = new AgentRep(getX(), getY(), getID(), getName());
        if (carryPacket()) {
            aRep.setCarry(carry().getRepresentation());
        }
        return aRep;
    }

    /**
     * Returns the x-coordinate of the previous position this Agent stood on.
     *
     * @return the previous x-coordinate
     */
    public int getLastX() {
        return lastX;
    }

    /**
     * Returns the y-coordinate of the previous position this Agent stood on.
     *
     * @return the previous y-coordinate
     */
    public int getLastY() {
        return lastY;
    }

    /**
     * Sets the last x-coordinate to the specified coordinate.
     *
     * @param x the new previous x-coordinate.
     */
    private void setLastX(int x) {
        lastX = x;
    }

    /**
     * Sets the last y-coordinate to the specified coordinate.
     *
     * @param y the new previous y-coordinate.
     */
    private void setLastY(int y) {
        lastY = y;
    }

    /**
     * Draws this Agent on the GUI.
     *
     * @param drawer  The visiting drawer
     */
    public void draw(Drawer drawer) {
        drawer.drawAgent(this);
    }


    public int getBatteryState() {
        return batteryState;
    }


    public void updateBatteryState(int load) {
        if (Agent.ENERGY_ENABLED) {
            int oldBatteryState = this.batteryState;

            batteryState += load;
            if (batteryState < BATTERY_MIN) batteryState = BATTERY_MIN;
            if (batteryState > BATTERY_MAX) batteryState = BATTERY_MAX;

            // Throw event if energy is either increased or decreased over or under 10% threshold

            int before = (int) Math.floor((oldBatteryState * 10.0) / (double) BATTERY_MAX);
            int after = (int) Math.floor((batteryState * 10.0) / (double) BATTERY_MAX);

            if (before != after) {
                var event = new EnergyUpdateEvent(this);
                event.setAgent(this);
                event.setEnergyPercentage(Math.max(after, before) * 10);
                event.setIncreased(after > before);

                EventManager.getInstance().throwEvent(event);
            }
        }
    }


    /**
     * The x-coordinate of the previous position.
     */
    private int lastX = -1;

    /**
     * The y-coordinate of the previous position.
     */
    private int lastY = -1;


    /**
     * The number of power units in the battery of this agent. This is an
     * additional property of the agent, it doesn't have to be used. Only
     * some strategies make use of it.
     */
    private int batteryState;
    public static final int BATTERY_MIN = 0;
    public static final int BATTERY_MAX = 1000;
    public static final int BATTERY_SAFE_MIN = 10;
    public static final int BATTERY_SAFE_MAX = 950;
    public static final int BATTERY_START = BATTERY_MAX;

    public static final int BATTERY_DECAY_STEP = 10;
    public static final int BATTERY_DECAY_STEP_WITH_CARRY = 20;
    public static final int BATTERY_DECAY_SKIP = 5;


    public static boolean ENERGY_ENABLED = true;

}
