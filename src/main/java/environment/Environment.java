package environment;

import agent.AgentImplementations;
import environment.world.agent.AgentWorld;
import environment.world.packet.PacketWorld;
import util.Debug;
import support.Outcome;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * A class that acts as an interface between the environment module and
 * other modules, and as a factory for making all needed objects in the
 * environment module during initialisation of the MAS.
 */

public class Environment {

    //--------------------------------------------------------------------------
    //		CONSTRUCTOR
    //--------------------------------------------------------------------------

    /**
     * Initializes an Environment with a given width, height and view.
     *
     * @param  widthAmount    the width of each world
     * @param  heightAmount   the height of each world
     */
    public Environment(int widthAmount, int heightAmount) {
        width = widthAmount;
        height = heightAmount;
        worlds = new ArrayList<>();
        aItems = new Vector<>();
    }

    //--------------------------------------------------------------------------
    //		INSPECTORS
    //--------------------------------------------------------------------------

    /**
     * Returns all worlds the environment had loaded.
     *
     * @return    A collection containing all worlds.
     */
    public Collection<World<?>> getWorlds() {
        return worlds;
    }

    /**
     * Returns a perception for the active item with the given ID, containing
     * representations of the items the object sees.
     *
     * @param  aItemID  the ID of the active item that requires the perception
     * @return          a perception for the ActiveItem with ID <code>aItemID</code>
     */
    public Perception getPerception(int aItemID) {
        for (ActiveItem<?> activeItem : getActiveItems()) {
            if (activeItem.getID() == aItemID) {
                return pReactor.getPerception(activeItem);
            }
        }
        Debug.alert(this, "No ActiveItem found by that ID.");
        return null;
    }

    /**
     * Returns a world from the worlds listed in <code>worlds</code>, that is
     * an instance of a class, named <code>worldClass<code>. If no such world
     * exists, we return <code>null</code>.
     *
     * @param worldClass  The class of which we require an instance
         * @return            A member of <code>worlds</code> that is an instance of
     *                    the <code>worldClass</code> class. Returns
     *                    <code>null</code> if no such member is found.
     */
    @SuppressWarnings("unchecked")
    public <T extends World<?>> T getWorld(Class<T> worldClass) {
        for (World<?> w : worlds) {
            if (w.getClass() == worldClass) {
                return (T) w;
            }
        }

        throw new RuntimeException("Could not find world class: " + worldClass.getName());
    }

    /**
     * Gets the amount of agents currently in the agentWorld of this
     * Environment.
     *
     * @return the number of agents currently in agentWorld
     */
    public int getNbAgents() {
        return getAgentWorld().getNbAgents();
    }

    /**
     * Returns all items on the (x,y)-position of all worlds.
     *
     * @param x  the x-coordinate
     * @param y  the y-coordinate
     * @return   a vector with of Items that stand on the specified coordinate
     */
    public Vector<Item<?>> getItemsOnPos(int x, int y) {
        if (x < getWidth() && y < getHeight()) {
            return getWorlds().stream().map(w -> w.getItem(x, y)).collect(Collectors.toCollection(Vector::new));
        } else {
            Debug.alert(this, "Index out of bounds.");
            return null;
        }
    }

    /**
     * Test whether the (x,y)-coordinate is empty.
     *
     * @param x  the x-coordinate
     * @param y  the y-coordinate
     * @return   (getItemsOnPos(x, y) == null)
     */
    public boolean isFreePos(int x, int y) {
        return getWorlds().stream().allMatch(w -> w.getItem(x, y) == null);
    }

    /**
     * Returns the number of worlds in this Environment.
     *
     * @return  getWorlds().size()
     */
    public int getNbWorlds() {
        return getWorlds().size();
    }

    //--------------------------------------------------------------------------
    //		MUTATORS
    //--------------------------------------------------------------------------

    /**
     * Creates the various components of this environment
     *
     * @post   all selected worlds are loaded
     * @post   a new clock is created
     * @post   a new reactor is created with a reference to the clock
     * @post   a new perceptionReactor is created with a reference to this environment
     * @post   a new postalService is created with a reference to the agentImplementations module
     * @post   a new EOPHandler is created
     * @post   a new collector is created with references to the agentImplementations module,
     *          the EOPHandler, the reactor and the postalService
     */
    public void createEnvironment() {
        Debug.print(this, "Start creating the environment");

        clock = new Clock();
        Debug.print(this, "clock set");

        reactor = new Reactor(this);
        Debug.print(this, "reactor set");

        pReactor = new PerceptionReactor(this);
        Debug.print(this, "perceptionReactor set");

        postalService = new PostalService(getAgentImplementations());
        Debug.print(this, "postalService set");

        eopHandler = new EOPHandler();
        Debug.print(this, "eopHandler set");

        collector = new Collector(getAgentImplementations(), eopHandler,
                                  reactor, postalService);
        Debug.print(this, "collector set");
    }

    /**
     * Clears all items on the (x,y)-coordinate.
     *
     * @param x  the x-coordinate
     * @param y  the y-coordinate
     * @post     isFreePos(x, y)
     */
    public void free(int x, int y) {
        getWorlds().stream().filter(w -> w.getItem(x, y) != null)
                .forEach(w -> w.free(x, y));
    }


    /**
     * Adds a world to this Environment.
     *
     * @param world        the world to add
     */
    public <T extends World<?>> void addWorld(T world) {
        worlds.add(world);
    }

    /**
     *  Puts an outcome in the collector's in-buffer. If there's anything in
     *  that buffer, the collector-thread starts processing it.
     *
     * @param  outcome  the outcome to put in the collector's buffer
     */
    public void collectOutcome(Outcome outcome) {
        collector.collectOutcome(outcome);
    }

    /**
     *  Requests a resume at the collector
     */
    public void requestResume() {
        collector.requestResume();
    }

    /**
     *  Tells the collector to print the sphereSet
     */
    public void printSphereSet() { // only for testing
        collector.printSphereSet();
    }

    public void finish() {
        reactor.finish();
        eopHandler.finish();
        collector.finish();
        postalService.finish();
    }

    //--------------------------------------------------------------------------
    //		GETTERS & SETTERS
    //--------------------------------------------------------------------------

    /**
     *  Gets the value of agentImpl
     *
     * @return    the value of agentImpl
     */
    public AgentImplementations getAgentImplementations() {
        return this.agentImpl;
    }

    /**
     *  Returns the AgentWorld of this Environment.
     *
     * @return    (AgentWorld)getWorld("AgentWorld")
     */
    public AgentWorld getAgentWorld() {
        return this.getWorld(AgentWorld.class);
    }

    /**
     *  Returns the PacketWorld of this Environment.
     *
     * @return    (PacketWorld)getWorld("PacketWorld")
     */
    public PacketWorld getPacketWorld() {
        return this.getWorld(PacketWorld.class);
    }

    /**
     *  Gets the value of reactor
     *
     * @return    the value of reactor
     */
    protected Reactor getReactor() {
        return this.reactor;
    }

    /**
     *  Gets the height of each world in this Environment.
     *
     * @return    This Environment's size
     */
    public int getHeight() {
        return this.height;
    }

    /**
     *  Gets the width of each world in this Environment.
     *
     * @return    This Environment's size
     */
    public int getWidth() {
        return this.width;
    }

    /**
     *  Gets the time of this Environment's clock
     *
     * @return    clock.getTime()
     */
    public int getTime() {
        return clock.getTime();
    }

    /**
     *  Gets the clock of this Environment
     *
     * @return    This Environment's clock
     */
    public Clock getClock() {
        return this.clock;
    }


    /**
     *  Sets the value of agentImpl
     *
     * @param  agentImplementations  The new agentImplementations value
     */
    public void setAgentImplementations(AgentImplementations
                                        agentImplementations) {
        this.agentImpl = agentImplementations;
    }

    /**
     *  Sets the value of reactor
     *
     * @param  aReactor  The new reactor value
     */
    protected void setReactor(Reactor aReactor) {
        this.reactor = aReactor;
    }

    /**
     * Sets the width of each world in this Environment
     *
     * @param  width  The new width value
     */
    private void setWidth(int width) {
        this.width = width;
    }

    /**
     * Sets the height of each world in this Environment
     *
     * @param  height  The new height value
     */
    private void setHeight(int height) {
        this.height = height;
    }

    /**
     *  Sets the Vector of all worlds in this Environment
     *
     * @param  worlds  The new worlds vector
     */
    protected void setWorlds(List<World<?>> worlds) {
        this.worlds = worlds;
    }

    /**
     * Add an active item to this Environment
     *
     * @param aItem the ActiveItem to add
     */
    public void addActiveItem(ActiveItem<?> aItem) {
        aItems.add(aItem);
    }

    /**
     * Get all active items in this Environment
     *
     * @return a Vector of ActiveItems
     */
    public Vector<ActiveItem<?>> getActiveItems() {
        return new Vector<>(aItems);
    }

    /**
     *  Returns the IDs of all the Active Items in the Environment.
     *
     * @return    An array containing the ID's of all the ActiveItems in this
     *            Environment.
     */
    public int[] getActiveItemIDs() {
        try {
            int[] aItemIDs = new int[getActiveItems().size()];
            int i = 0;
            for (ActiveItem<?> activeItem : getActiveItems()) {
                aItemIDs[i++] = activeItem.getID();
            }
            return aItemIDs;
        } catch (ClassCastException exc) {
            Debug.alert(this, "ClassCastException: " + exc.getMessage());
            return null;
        }
    }


    //--------------------------------------------------------------------------
    //		ATTRIBUTES
    //--------------------------------------------------------------------------

    /**
     *  The interface to the agentImplementations module
     */
    protected AgentImplementations agentImpl;

    /**
     *  The reactor of this environment
     */
    protected Reactor reactor;

    /**
     *  The perceptionReactor of this environment
     */
    protected PerceptionReactor pReactor;

    /**
     *  The collector of this environment
     */
    protected Collector collector;

    /**
     *  EOPHandler
     */
    protected EOPHandler eopHandler;

    /**
     *  PostalService
     */
    protected PostalService postalService;

    /**
     *  Clock
     */
    protected Clock clock;

    /**
     *  The width of each world in this environment
     */
    protected int width;

    /**
     *  The height of each world in this environment
     */
    protected int height;


    /*
     *  All the worlds this environment contains
     */
    private List<World<?>> worlds;

    /**
     *  All the active items this environment contains
     *
     * invar: aItem contains only ActiveItems
     */
    private final Vector<ActiveItem<?>> aItems;

}
