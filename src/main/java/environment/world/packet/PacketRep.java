package environment.world.packet;

import environment.Representation;

import java.awt.*;

/**
 *  A class for representations of packets.
 */
public class PacketRep extends Representation {

    Color color;

    /**
     *  Initializes a new PacketRep instance
     *
     * @param  x    X-coordinate of the Packet this representation represents
     * @param  y    Y-coordinate of the Packet this representation represents
     * @param  aColor   color of the Packet this representation represents
     */
    protected PacketRep(int x, int y, Color aColor) {
        super(x, y);
        setColor(aColor);
    }

    /**
     *  Gets the color of the Packet this PacketRep represents
     *
     * @return    This PacketRep's color
     */
    public Color getColor() {
        return color;
    }

    /**
     *  Sets the color of this PacketRep
     *
     * @param  aColor  The new color value
     */
    protected void setColor(Color aColor) {
        color = aColor;
    }

    public char getTypeChar() {
        return ('P');
    }

    @Override
    public boolean isWalkable() {
        return false;
    }
}
