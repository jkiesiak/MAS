package environment.world.destination;

import environment.Representation;

import java.awt.*;

/**
 *  A class for representations of Destinations.
 */
public class DestinationRep extends Representation {

    private Color color;

    /**
     *  Initializes a new DestinationRep instance
     *
         * @param  x    X-coï¿½rdinate of the Destination this representation represents
         * @param  y    Y-coï¿½rdinate of the Destination this representation represents
     * @param  aColor   color of the Destination this representation represents
     */
    protected DestinationRep(int x, int y, Color aColor) {
        super(x, y);
        setColor(aColor);
    }

    /**
     *  Gets the color of the destination this DestinationRep represents
     *
     * @return    This DestinationRep's color
     */
    public Color getColor() {
        return color;
    }

    /**
     *  Sets the color of this DestinationRep
     *
     * @param  aColor  The new color value
     */
    protected void setColor(Color aColor) {
        color = aColor;
    }

    public char getTypeChar() {
        return ('D');
    }

    @Override
    public boolean isWalkable() {
        return false;
    }
}
