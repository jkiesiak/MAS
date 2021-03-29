package environment.world.packet;

import environment.Item;
import gui.video.Drawer;
import util.Debug;
import util.MyColor;

import java.awt.*;

/**
 *  A class for packets with a certain color.
 */

public class Packet extends Item<PacketRep> {

    private final Color color;

    /**
     *  Initializes a new Packet instance
     *
     * @param  x    x-coordinate of the Packet
     * @param  y    Y-coordinate of the Packet
     * @param  col  the Packet's color
     */
    public Packet(int x, int y, Color col) {
        super(x, y);
        color = col;
        Debug.print(this, "Packet created at " + x + " " + y);
    }

    /**
     *  Initializes a new Packet instance
     *
     * @param  x    x-coordinate of the Packet
     * @param  y    Y-coordinate of the Packet
     * @param  colorstr  the Packet's color
     */
    public Packet(int x, int y, String colorstr) {
        this(x, y, MyColor.getColor(colorstr));
    }

    /**
     *  Gets the color of this Packet
     *
     * @return  This Packet's color
     */
    public Color getColor() {
        return color;
    }

    /**
     *  Returns a representation of this Packet
     *
     * @return  A packet-representation
     */
    @Override
    public PacketRep getRepresentation() {
        return new PacketRep(getX(), getY(), getColor());
    }

    /**
     *  Draws this Packet on the GUI.
     *
     * @param drawer  The visiting drawer
     */
    public void draw(Drawer drawer) {
        drawer.drawPacket(this);
    }

}
