package environment.world.destination;

import environment.Item;
import gui.video.Drawer;
import util.Debug;
import util.MyColor;

import java.awt.*;

/**
 *  A class for destinations for certain packets.
 */

public class Destination extends Item<DestinationRep> {

    private final Color color;

    /**
     *  Initializes a new Destination instance
     *
     * @param  x    x-coordinate of the Destination
     * @param  y    y-coordinate of the Destination
     * @param  col  the Destination's color
     */
    public Destination(int x, int y, Color col) {
        super(x, y);
        color = col;
        Debug.print(this, "Destination created at " + x + " " + y);
    }

    /**
     *  Initializes a new Destination instance
     *
     * @param  x    x-coordinate of the Destination
     * @param  y    y-coordinate of the Destination
     * @param  colorstr  the Destination's color
     */
    public Destination(int x, int y, String colorstr) {
        this(x, y, MyColor.getColor(colorstr));
    }

    /**
     *  Gets the color of this Destination
     *
     * @return    This Destination's color
     */
    public Color getColor() {
        return color;
    }

    /**
     *  Returns a representation of this Destination
     *
     * @return  A Representation of this Destination
     */
    @Override
    public DestinationRep getRepresentation() {
        return new DestinationRep(getX(), getY(), getColor());
    }

    /**
     *  Draws this Destination on the GUI.
     *
     * @param drawer  The visiting drawer
     */
    public void draw(Drawer drawer) {
        drawer.drawDestination(this);
    }

}
