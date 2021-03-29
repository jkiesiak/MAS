package support;

import environment.Environment;
import environment.world.packet.PacketWorld;

import java.awt.*;

/**
 *  A class for influences for picking up packages.
 */
public class InfPickPacket extends Influence {

    /**
     *  Initializes a new InfPickPacket object
     *  Cfr. super
     */
    public InfPickPacket(Environment environment, int x, int y, int agent, Color color) {
        super(environment, x, y, agent, color);
    }

    /**
     *  Gets the area of effect (the World it wants to effect) for this Influence
     *
     * @return    the PacketWorld
     */
    @Override
    public PacketWorld getAreaOfEffect() {
        return getEnvironment().getWorld(PacketWorld.class);
    }

}
