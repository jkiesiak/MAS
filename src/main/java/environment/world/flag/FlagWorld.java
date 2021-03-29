package environment.world.flag;

import environment.World;
import util.Debug;
import support.InfPutFlag;
import support.Influence;
import util.event.AgentActionEvent;
import util.event.EventManager;

import java.awt.*;
import java.util.Collection;

/**
 *  A class for a FlagWorld, being a layer of the total world, that contains Flags.
 */
public class FlagWorld extends World<Flag> {

    /**
     *  Initializes a new FlagWorld instance
     */
    public FlagWorld() {
        super();
    }

    public String toString() {
        return "FlagWorld";
    }

    /**
     *  Puts a new flag with a given color on the given coordinates in this FlagWorld.
     *  Updates the gui to show the flag.
     *
     * @param  x    X-coordinate of the Flag that we want to put down
     * @param  y    Y-coordinate of the Flag that we want to put down
     * @param  c    color of the Flag that we want to put down
         * @post    A new Flag is created with the given coordinates and color and placed
     *          at the given location in this FlagWorld
     * @post    The flag is shown on the gui.
     */
    protected synchronized void putFlag(int x, int y, Color c) {
        putItem(x, y, new Flag(getEnvironment(), x, y, c));

        AgentActionEvent event = new AgentActionEvent(this);
        event.setAction(AgentActionEvent.PUTFLAG);
        event.setTo(x, y);
        EventManager.getInstance().throwEvent(event);
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
        if (inf instanceof InfPutFlag) {
            putFlag(inf.getX(), inf.getY(), inf.getColor());
        }
    }

    /**
     * Adds Flags to this FlagWorld.
     *
     * @param flags a collection containing the flags to place in this world
     */
    @Override
    public void placeItems(Collection<Flag> flags) {
        flags.forEach(this::placeItem);
    }

    /**
     * Adds a Flag to this FlagWorld.
     *
     * @param flag the flag to place in this world
     */
    @Override
    public void placeItem(Flag flag) {
        try {
            this.putItem(flag);
        } catch (ClassCastException exc) {
            Debug.alert(this, "Can only place a Flag in FlagWorld.");
        }
    }
}