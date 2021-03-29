package environment.world.pheromone;

import environment.ClockEvent;
import environment.ClockListener;
import environment.Environment;
import environment.Item;
import gui.video.Drawer;
import util.Debug;

/**
 *  A class for pheromones.
 */
public class Pheromone extends Item<PheromoneRep> implements ClockListener {

    /**
     *  Initializes a new Pheromone instance with a specified lifetime.
     *
     * @param environ  the environment the new Pheromone is situated in
     * @param x        x-coordinate of the new Pheromone
     * @param y        y-coordinate of the new Pheromone
     * @param time     the initial strength of the new Pheromone
     * @pre   time > 0
     */
    public Pheromone(Environment environ, int x, int y, int time) {
        super(x, y);
        lifetime = time;
        env = environ;
        env.getClock().addListener(this); //register this pheromone to listen to the clock
        Debug.print(this, "Pheromone created at " + x + " " + y);
    }

    /**
     *  Initializes a new Pheromone instance with a default lifetime.
     *
     * @param  environ  the environment this Pheromone is situated in
     * @param  x    x-coordinate of the Pheromone
     * @param  y    y-coordinate of the Pheromone
     */
    public Pheromone(Environment environ, int x, int y) {
        this(environ, x, y, DEFAULT_LIFETIME);
    }

    /**
     *  Sets the environment of this Pheromone
     *
     * @param  environ  The environment
     */
    protected void setEnvironment(Environment environ) {
        this.env = environ;
    }

    /**
     *  Reduces the lifetime of the Pheromone
     *  When lifetime reaches 0 the Pheromone will be deregistered as a listener to the clock
     *  and will be removed from it's world.
     *
     * @param   event    the clockevent that was issued to the flag
     * @post    lifetime is decreased by one
     * @post    if lifetime is smaller than or equal to zero, this flags is deregistered as a
     *          listener at clock and is removed from it's world.
     */
    public void onClockEvent(ClockEvent event) {
        lifetime -= DECAY;
        if (lifetime <= 0) {
            env.getClock().removeListenerDelayed(this);
            env.getWorld(PheromoneWorld.class).free(getX(), getY());
            Debug.print(this, "removed");
        }
        Debug.print(this, "Received a clock event");
    }

    /**
     *  Returns a representation of this Pheromone.
     *
     * @return    A Pheromone-representation
     */
    @Override
    public PheromoneRep getRepresentation() {
        return new PheromoneRep(getX(), getY(), getLifetime());
    }

    /**
     *  Draws this Pheromone on the GUI.
     *
     * @param drawer  The visiting drawer
     */
    public void draw(Drawer drawer) {
        drawer.drawPheromone(this);
    }

    /**
     *  Returns the current lifetime of this Pheromone.
     *
     * @return the current strength of this Pheromone
     */
    public int getLifetime() {
        return lifetime;
    }

    /**
     * Sets the lifetime of this Pheromone.
     *
     * @param lifetime the new lifetime for this Pheromone
     */
    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }

    /**
     *  Prolongues the lifetime of this Pheromone with a given interval.
     *
     * @param amount  the time to be added to the lifetime of this Pheromone
     */
    public void reinforce(int amount) {
        lifetime += amount;
        if (lifetime > MAX_LIFETIME) {
            lifetime = MAX_LIFETIME;
        }
    }

    /**
     *  Prolongues the lifetime of this Pheromone with the default interval.
     */
    public void reinforce() {
        lifetime += REINFORCEMENT;
        if (lifetime > MAX_LIFETIME) {
            lifetime = MAX_LIFETIME;
        }
    }

    /**
     *  The amount of time this Pheromone will exist.
     */
    protected int lifetime;

    /**
     *  The environment this Pheromone is in.
     */
    protected Environment env;

    /**
     *  The maximum strength for a Pheromone.
     */
    protected static final int MAX_LIFETIME = 2000;


    /**
     *  The amount of time units to be substracted from lifetime each interval.
     */
    protected static final int DECAY = 1;

    /**
     *  The initial lifetime for a Pheromone.
     */
    protected static final int DEFAULT_LIFETIME = 100;

    /**
     *  The time period added to the lifetime of a Pheromone when
     *  reinforced.
     */
    protected static final int REINFORCEMENT = 75;

}
