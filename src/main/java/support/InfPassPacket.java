package support;

import environment.Environment;
import environment.world.packet.PacketWorld;

/**
 *  A class for influences for passing packet from one agent to another.
 */
public class InfPassPacket extends Influence {

    /**
     *  Initializes a new InfPickPacket object
     *  Cfr. super
     */
    public InfPassPacket(Environment environment, int x, int y, int agent) {
        super(environment, x, y, agent, null);
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
