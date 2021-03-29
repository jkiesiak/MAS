package environment.world.wall;

import environment.World;
import util.Debug;
import support.Influence;

import java.util.Collection;

/**
 *  A class for a WallWorld, being a layer of the total world, that contains Walls.
 */
public class WallWorld extends World<Wall> {

    /**
     *  Initializes a new WallWorld instance
     */
    public WallWorld() {
        super();
    }

    /**
     * Returns a string representation of this World
     *
     * @return "WallWorld"
     */
    public String toString() {
        return "WallWorld";
    }

    /**
     *  Brings a given influence in effect in this world.
     *  This method knows the effects of certain influences and realizes them
     *  in this world.
     *
     * @param inf  the influence to bring in effect
     */
    @Override
    public void effectuate(Influence inf) {
        //no-op, nothing is done in this world (yet)
    }

    /**
     * Adds Walls to this WallWorld.
     *
     * @param walls an array containing the walls to add to this world
     */
    @Override
    public void placeItems(Collection<Wall> walls) {
        walls.forEach(this::placeItem);
    }

    /**
     * Adds a Wall to this WallWorld.
     *
     * @param wall  the walls to place in this world
     */
    @Override
    public void placeItem(Wall wall) {
        try {
            this.putItem(wall);
        } catch (ClassCastException exc) {
            Debug.alert(this, "Can only place a Wall in WallWorld.");
        }
    }
}
