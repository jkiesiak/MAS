package environment.world.flag;

import environment.Environment;
import environment.Item;
import gui.video.Drawer;
import util.Debug;

import java.awt.*;

/**
 *  A class for flags with a certain color.
 */
@SuppressWarnings("FieldCanBeLocal")
public class Flag extends Item<FlagRep> {

    /**
     *  Initializes a new Flag instance
     *
     * @param  environ  the environment this Flag is situated in
     * @param  x    x-coordinate of the Flag
     * @param  y    y-coordinate of the Flag
     * @param  col  the Flag's color
     */
    public Flag(Environment environ, int x, int y, Color col) {
        super(x, y);
        setEnvironment(environ);
        color = col;
        Debug.print(this, "Flag created at " + x + " " + y);
    }

    /**
     *  Sets the environment of this Flag.
     *
     * @param  environ  The environment
     */
    protected void setEnvironment(Environment environ) {
        this.env = environ;
    }

    /**
     *  Gets the color of this Flag.
     *
     * @return    This Flag's color
     */
    public Color getColor() {
        return color;
    }

    /**
     *  Returns a representation of this Flag.
     *
     * @return    A Flag-representation
     */
    @Override
    public FlagRep getRepresentation() {
        return (new FlagRep(getX(), getY(), getColor()));
    }

    /**
     *  Draws this Flag on the GUI.
     *
     * @param drawer  The visiting drawer
     */
    @Override
    public void draw(Drawer drawer) {
        drawer.drawFlag(this);
    }

    /**
     *  The color of this Flag.
     */
    private final Color color;

    /**
     *  The environment this Flag is situated in.
     */
    private Environment env;

}
