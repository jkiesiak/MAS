package support;

import environment.Environment;
import environment.world.packet.PacketWorld;

/**
 *  A class for influences for putting down packets.
 */
public class InfPutPacket extends Influence {

    /**
     *  Initializes a new InfPutPacket object
     *  Cfr. super
     */
    public InfPutPacket(Environment environment, int x, int y, int agent) {
        super(environment, x, y, agent, null);
    }

    /**
         *  Gets the area of effect (the World it wants to effect) for this Influence
     *
     * @return    The PacketWorld
     */
    @Override
    public PacketWorld getAreaOfEffect() {
        return getEnvironment().getWorld(PacketWorld.class);
    }

}
