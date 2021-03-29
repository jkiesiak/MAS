package environment.world.crumb;

import environment.Item;
import gui.video.Drawer;
import util.Debug;

/**
 *  A class for crumbs.
 */
public class Crumb extends Item<CrumbRep> {

    /**
     *  Initializes a new Crumb instance with a specified number.
     *
     * @param  x		x-coordinate of the new Crumb
     * @param  y		y-coordinate of the new Crumb
     * @param  number 	the initial value of the new Crumb
     */
    public Crumb(int x, int y, int number) {
        super(x, y);
        this.number = number;
        Debug.print(this, "Crumb created at " + x + " " + y);
    }

    /**
     *  Returns a representation of this Crumb.
     *
     * @return    A Crumb representation
     */
    @Override
    public CrumbRep getRepresentation() {
        return new CrumbRep(getX(), getY(), getNumber());
    }

    /**
     *  Draws this Crumb on the GUI.
     *
     * @param drawer  The visiting drawer
     */
    public void draw(Drawer drawer) {
        drawer.drawCrumb(this);
    }

    /**
     *  Returns the current number of crumbs in this Crumb.
     *
     * @return the current number of this Crumb
     */
    public int getNumber() {
        return number;
    }

    /**
     * Sets the number of crumbs of this Crumb.
     *
     * @param number the new number for this Crumb
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     *  The amount of crumbs in this Crumb.
     */
    protected int number;

}
