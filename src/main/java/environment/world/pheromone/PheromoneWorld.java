package environment.world.pheromone;

import environment.CellPerception;
import environment.World;
import support.*;
import util.Debug;
import util.event.AgentActionEvent;
import util.event.EventManager;

import java.util.Collection;

/**
 *  A class for a PheromoneWorld, being a layer of the total world, that contains Pheromones.
 */
public class PheromoneWorld extends World<Pheromone> {

    /**
     *  Initializes a new PheromoneWorld instance
     */
    public PheromoneWorld() {
        super();
    }

    public String toString() {
        return "PheromoneWorld";
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
        if (inf instanceof InfPutPheromone) {
            putPheromone(inf.getX(), inf.getY(), ((InfPutPheromone)inf).getLifeTime());
        } else if (inf instanceof InfPutDirPheromone) {
            InfPutDirPheromone dir = (InfPutDirPheromone)inf;
            putDirPheromone(inf.getX(), inf.getY(), dir.getLifeTime(), dir.getTarget());
        } else if (inf instanceof InfRemovePheromone) {
            removePheromone(inf.getX(), inf.getY());
        }
    }

    /**
     *  Puts a new Pheromone on the given coordinates in this PheromoneWorld
     *  with a specified initial lifetime. If there already is a Pheromone
     *  on the specified coordinate, reinforces this Pheromone with the
     *  specified lifetime amount.
     *  Updates the gui to show the pheromone.
     *
     * @param x        x-coordinate of the Pheromone that we want to put down
     * @param y        y-coordinate of the Pheromone that we want to put down
     * @param lifetime the initial lifetime for the Pheromone
     * @post           A new Pheromone is created with the given coordinates
     *                 and placed at the given location in this pheromoneworld.
     *                 If a Pheromone already exists at the given location,
     *                 that Pheromone is reinforced by <code>lifetime</code>.
     * @post           The Pheromone is shown on the gui.
     */
    protected synchronized void putPheromone(int x, int y, int lifetime) {
        if (lifetime <= 0) {
            put(x, y);
            //putAndPropagate(x, y);
        } else {
            put(x, y, lifetime);
            //putAndPropagate(x, y, lifetime);
        }

        AgentActionEvent event = new AgentActionEvent(this);
        event.setAction(AgentActionEvent.PUTPHEROMONE);
        //event.setFrom(fx,fy);
        EventManager.getInstance().throwEvent(event);
    }

    /**
     *  Puts a new DirPheromone on the given coordinates in this PheromoneWorld
     *  with a specified initial lifetime and target. If there already is a
     *  DirPheromone on the specified coordinate, reinforces this DirPheromone
     *  with the specified lifetime amount.
     *  Updates the gui to show the pheromone.
     *
     * @param x        x-coordinate of the DirPheromone that we want to put down
     * @param y        y-coordinate of the DirPheromone that we want to put down
     * @param lifetime the initial lifetime for the DirPheromone
     * @param target   the target for the Dirpheromone
     * @post           A new DirPheromone is created with the given coordinates
     *                 and placed at the given location in this pheromoneworld.
     *                 If a DirPheromone already exists at the given location,
     *                 that DirPheromone is reinforced by <code>lifetime</code>.
     * @post           The DirPheromone is shown on the gui.
     */
    protected synchronized void putDirPheromone(int x, int y, int lifetime, CellPerception target) {
        if (lifetime <= 0) {
            putDirected(x, y, target);
        } else {
            putDirected(x, y, lifetime, target);
        }

        AgentActionEvent event = new AgentActionEvent(this);
        event.setAction(AgentActionEvent.PUTPHEROMONE);
        //event.setFrom(fx,fy);
        EventManager.getInstance().throwEvent(event);
    }

    /**
     * Removes a pheromone from a given coordinate.
     *
     * @param x the x-coordinate of the pheromone to be removed
     * @param y the y-coordinate of the pheromone to be removed
     */
    protected synchronized void removePheromone(int x, int y) {
        free(x, y);
        AgentActionEvent event = new AgentActionEvent(this);
        event.setAction(AgentActionEvent.REMOVEPHEROMONE);
        EventManager.getInstance().throwEvent(event);
    }

    /**
     * Puts a new pheromone on a specified coordinate (x, y).
     * If at some time a pheromone on that position already exists, that
     * pheromone is strengthened.
     *
     * @param x the x-coordinate for the new pheromone
     * @param y the x-coordinate for the new pheromone
     */
    public void put(int x, int y) {
        if (getItem(x, y) != null) {
            getItem(x, y).reinforce();
        } else {
            putItem(new Pheromone(getEnvironment(), x, y));
        }
    }

    /**
     * Puts a directed pheromone in this world with the default lifetime
     *
     * @param x        the x-coordinate for the pheromone to put
     * @param y        the y-coordinate for the pheromone to put
     * @param target   the target for the pheromone to put
     */
    public void putDirected(int x, int y, CellPerception target) {
        if (getItem(x, y) != null) {
            //( (Pheromone) getItem(x, y)).reinforce();
            DirPheromone pher = (DirPheromone) this.getItem(x, y);
            pher.reinforce();
            pher.setTarget(target);
        } else {
            putItem(new DirPheromone(getEnvironment(), x, y, target));
        }
    }

    /**
     * Puts a new pheromone on a specified coordinate (x, y) with a given
     * lifetime.
     * If at some time a pheromone on that position already exists, thad
     * pheromone is strengthened with <code>lifetime</code>.
     *
     * @param x the x-coordinate for the pheromone to put
     * @param y the y-coordinate for the pheromone to put
     * @param lifetime the lifetime for the pheromone to put
     */
    public void put(int x, int y, int lifetime) {
        if (getItem(x, y) != null) {
            getItem(x, y).reinforce(lifetime);
        } else {
            putItem(new Pheromone(getEnvironment(), x, y, lifetime));
        }
    }

    /**
     * Puts a new directed pheromone on a specified coordinate (x, y) with
     * a given lifetime and target.
     * If at some time a pheromone on that position already exists, thad
     * pheromone is strengthened with <code>lifetime</code>.
     *
     * @param x the x-coordinate for the DirPheromone to put
     * @param y the y-coordinate for the DirPheromone to put
     * @param lifetime the lifetime for the DirPheromone to put
     * @param target the target for the DirPheromone to put
     */
    public void putDirected(int x, int y, int lifetime, CellPerception target) {
        if (getItem(x, y) != null) {
            //( (Pheromone) getItem(x, y)).reinforce(lifetime);
            DirPheromone pher = (DirPheromone) this.getItem(x, y);

            pher.reinforce(lifetime);
            pher.setTarget(target);
        } else {
            putItem(new DirPheromone(getEnvironment(), x, y, lifetime, target));
        }
    }

    /**
     * Puts a new pheromone on a specified coordinate and on all surrounding
     * areas. If at some time a Pheromone on that position already exists, that
     * Pheromone is strengthened.
     *
     * @param x the x-coordinate of the pheromone to put
     * @param y the y-coordinate of the pheromone to put
     */
    public void putAndPropagate(int x, int y) {
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (getItem(i, j) != null) {
                    getItem(i, j).reinforce();
                } else {
                    putItem(new Pheromone(getEnvironment(), i, j));
                }
            }
        }
    }

    /**
     * Puts a new pheromone on a specified coordinate with given lifetime
     * and on all surrounding areas with a diminished factor of lifetime.
     * If at some time a Pheromone on that position already exists, that
     * Pheromone is strengthened.
     *
     * @param x the x-coordinate of the pheromone to put
     * @param y the y-coordinate of the pheromone to put
     * @param lifetime the lifetime for the pheromone to put
     */
    public void putAndPropagate(int x, int y, int lifetime) {
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                try {
                    if (i == x && j == y) {
                        if (getItem(i, j) == null) {
                            putItem(new Pheromone(getEnvironment(), i, j, lifetime));
                        } else {
                            getItem(i, j).reinforce(lifetime);
                        }
                    } else {
                        if (getItem(i, j) == null) {
                            putItem(new Pheromone(getEnvironment(), i, j, lifetime / 10));
                        } else {
                            getItem(i, j).reinforce(lifetime / 10);
                        }
                    }
                } catch (IndexOutOfBoundsException exc) {
                    // (i, j) is no coordinate in this world
                    //No-op
                }
            }
        }
    }


    /**
     * Adds Pheromones to this PheromoneWorld.
     *
     * @param pheromones the array of pheromones to put in this world
     */
    @Override
    public void placeItems(Collection<Pheromone> pheromones) {
        pheromones.forEach(this::placeItem);
    }

    /**
     * Adds a Pheromone to this PheromoneWorld.
     *
     * @param pheromone the pheromone to put in this world
     */
    @Override
    public void placeItem(Pheromone pheromone) {
        try {
            this.putItem(pheromone);
        } catch (ClassCastException exc) {
            Debug.alert(this, "Can only place a Pheromone in PheromoneWorld.");
        }
    }
}
