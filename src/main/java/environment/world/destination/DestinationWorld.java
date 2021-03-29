package environment.world.destination;

import environment.Coordinate;
import environment.World;
import util.Debug;
import support.Influence;
import util.MyColor;

import java.awt.*;
import java.util.Collection;

/**
 *  A class for a DestinationWorld, being a layer of the total world, that contains Destinations.
 */
public class DestinationWorld extends World<Destination> {

    /**
     *  Initializes a new DestinationWorld instance
     */
    public DestinationWorld() {
        super();
    }


    /**
     *  Get the destination, situated in this world, with the given color 'c'
     *  (There is only one destination per color)
     *
     * @param   c   the color of the Destination we are looking for
     * @return  The destination that has color c in this AgentWorld
     */
    public Destination getDestination(Color c) {
        for (int i = 0; i < getEnvironment().getWidth(); i++) {
            for (int j = 0; j < getEnvironment().getHeight(); j++) {
                if (getItem(i, j) != null && getItem(i, j).getColor() == c) {
                    return getItem(i, j);
                }
            }
        }
        return null;
    }

    public String toString() {
        return "DestinationWorld";
    }

    /**
     * Adds a number of Destinations randomly to this DestinationWorld.
     *
     * @param nbDestinations the number of destinations to add to this world
     */
    public void createWorld(int nbDestinations) {
        for (int i = 0; i < nbDestinations; i++) {
            boolean ok = false;
            while (!ok) {
                Coordinate c = getRandomCoordinate(getEnvironment().getWidth(),
                                               getEnvironment().getHeight());
                if (getEnvironment().isFreePos(c.getX(), c.getY())) {
                    placeItem(new Destination(c.getX(), c.getY(),
                                              MyColor.getColor(i)));
                    ok = true;
                }
            }
        }
    }

    /**
     * Adds Destinations to this DestinationWorld.
     *
     * @param destinations a collection containing the destinations to add to this world
     */
    @Override
    public void placeItems(Collection<Destination> destinations) {
        destinations.forEach(this::placeItem);
    }

    /**
     * Adds a Destination to this DestinationWorld.
     *
     * @param dest the destination to place in this world
     */
    @Override
    public void placeItem(Destination dest) {
        try {
            this.putItem(dest);
        } catch (ClassCastException exc) {
            Debug.alert(this, "Can only place a Destination in DestinationWorld.");
        }
    }

    /**
     *	No-op.
     *  Brings a given influence in effect in this world.
     *  This method knows the effects of certain influences and realizes them
     *  in this world.
     *
     * @param inf  the influence to bring in effect
     */
    @Override
    protected void effectuate(Influence inf) {}

}
