package environment.world.wall;

import environment.Environment;
import environment.Item;
import gui.video.Drawer;
import util.Debug;

/**
 *  A class for walls, being simple Items with a certain position.
 */
public class Wall extends Item<WallRep> {

    private Environment env;

    /**
     *  Initializes a new Wall instance
     *
     * @param  x    X-coordinate of the Wall
     * @param  y    Y-coordinate of the Wall
     */
    public Wall(int x, int y) {
        super(x, y);
        Debug.print(this, "Wall created at " + x + " " + y);
    }

    /**
     *  Returns a representation of this Wall
     *
     * @return  A wall-representation
     */
    @Override
    public WallRep getRepresentation() {
        return new WallRep(getX(), getY());
    }

    /**
     *  Draws this Packet on the GUI.
     *
     * @param drawer  The visiting drawer
     */
    public void draw(Drawer drawer) {
        drawer.drawWall(this);
    }
}
