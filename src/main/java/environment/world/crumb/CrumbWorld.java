package environment.world.crumb;

import environment.World;
import util.Debug;
import support.InfPickCrumb;
import support.InfPutCrumb;
import support.Influence;
import util.event.AgentActionEvent;
import util.event.EventManager;

import java.util.Collection;

/**
 *  A class for a CrumbWorld, being a layer of the total world, that contains Crumbs.
 */
public class CrumbWorld extends World<Crumb> {

    /**
     *  Initializes a new CrumbWorld instance
     */
    public CrumbWorld() {
        super();
    }

    public String toString() {
        return "CrumbWorld";
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
        if (inf instanceof InfPutCrumb) {
            putCrumb(inf.getX(), inf.getY(), ((InfPutCrumb)inf).getNumber());
        } else if (inf instanceof InfPickCrumb) {
            pickCrumb(inf.getX(), inf.getY(), ((InfPickCrumb)inf).getNumber());
        }
    }

    /**
     *  Puts a new Crumb on the given coordinates in this CrumbWorld
     *  with a specified initial number of crumbs. If there already is a
     *  Crumb on the specified coordinate, adds the value of number to that
     *  Crumb.
     *  Updates the gui to show the crumb.
     *
     * @param x      x-coordinate of the Crumb that we want to put down
     * @param y      y-coordinate of the Crumb that we want to put down
     * @param number the number of crumbs to be laid
     * @post         A new Crumb is created with the given coordinates
     *               and placed at the given location in this CrumbWorld.
     *               If a Crumb already exists at the given location,
     *               <code>number</code> is added to that Crumb.
     * @post         The Crumb is shown on the gui.
     */
    protected synchronized void putCrumb(int x, int y, int number) {
        put(x, y, number);

        AgentActionEvent event = new AgentActionEvent(this);
        event.setAction(AgentActionEvent.PUTCRUMB);
        //event.setFrom(fx,fy);
        EventManager.getInstance().throwEvent(event);
    }

    protected synchronized void pickCrumb(int x, int y, int number) {
        pick(x, y, number);

        AgentActionEvent event = new AgentActionEvent(this);
        event.setAction(AgentActionEvent.PICKCRUMB);
        //event.setFrom(fx,fy);
        EventManager.getInstance().throwEvent(event);
    }


    /**
     * Puts a new Crumb on a specified coordinate (x, y).
     * If a Crumb on that position already exists,
     * <code>number</code> is added to the number that Crumb.
     *
     * @param x      the x-coordinate for the new Crumb to put
     * @param y      the y-coordinate for the new Crumb to put
     * @param number the number of crumbs in the new Crumb to put
     */
    public void put(int x, int y, int number) {
        if (getItem(x, y) != null) {
            Crumb crumb = getItem(x, y);
            crumb.setNumber(crumb.getNumber() + number);
        } else {
            putItem(new Crumb(x, y, number));
        }
    }

    /**
     * Picks some crumbs from a Crumb on a specified coordinate (x, y).
     * If <code>number</code> is larger than the number of that Crumb,
     * Crumb is removed.
     *
     * @param x      the x-coordinate of the Crumb to pick from
     * @param y      the y-coordinate of the Crumb to pick from
     * @param number the number of crumbs to pick from the Crumb on (x, y)
     */
    public void pick(int x, int y, int number) {
        if (getItem(x, y) != null ) {
            Crumb crumb = getItem(x, y);
            if (crumb.getNumber() > number) {
                crumb.setNumber(crumb.getNumber() - number);
            } else {
                Debug.alert(this, "Not enough crumbs in this Crumb.");
            }
        } else {
            Debug.alert(this, "No Crumb to pick from");
        }
    }

    /**
     * Adds a Crumb to this CrumbWorld.
     *
     * @param crumb the Crumb to place in this world
     */
    @Override
    public void placeItem(Crumb crumb) {
        try {
            this.putItem(crumb);
        } catch (ClassCastException exc) {
            Debug.alert(this, "Can only place Crumbs in CrumbWorld.");
        }
    }

    /**
     * Adds Crumbs to this CrumbWorld.
     *
     * @param crumbs the Crumbs to place in this world
     */
    @Override
    public void placeItems(Collection<Crumb> crumbs) {
        crumbs.forEach(this::placeItem);
    }

}
