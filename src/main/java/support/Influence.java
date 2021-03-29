package support;

import environment.Environment;
import environment.World;

import java.awt.*;

/**
 *  A class for influences.
 */
public abstract class Influence {

    private final int x;
    private final int y;
    private final int ID;
    private final Color color;
    private final Environment env;

    /**
     *  Initializes a new Influence object
     *
     * @param  environment  the Environment instance
     * @param  x            an X-coordinate for this influence
     * @param  y            a Y-coordinate for this influence
     * @param  id           the ID of the agent or object this influence
     *                      originates from
     * @param  c            a color for this influence
     */
    public Influence(Environment environment, int x, int y, int id, Color c) {
        this.x = x;
        this.y = y;
        this.ID = id;
        this.color = c;
        this.env = environment;
    }

    /**
     *  Gets the x attribute of the Influence object
     *
     * @return    The x value
     */
    public int getX() {
        return x;
    }

    /**
     *  Gets the y attribute of the Influence object
     *
     * @return    The y value
     */
    public int getY() {
        return y;
    }

    /**
     *  Gets the ID attribute of the Influence object
     *
     * @return    The ID value
     */
    public int getID() {
        return ID;
    }

    /**
     *  Gets the color attribute of the Influence object
     *
     * @return    The color value
     */
    public Color getColor() {
        return color;
    }

    /**
     *  Returns the environment
     *
     * @return    The environment
     */
    public Environment getEnvironment() {
        return env;
    }

    /**
         *  Gets the area of effect (the World it wants to effect) for this Influence
     *
     * @return    This Influence's areaOfEffect
     */
    public abstract World<?> getAreaOfEffect();

}
