package agent.behaviour.structured.change;

import agent.behaviour.BehaviourChange;

/**
 *  This class represents a conditional whether the Packet is carried by the agent.
 *  If he picked up the package, change the state of behaviour.
 */

public class PacketCarried extends BehaviourChange {

    @Override
    public boolean isSatisfied() {
        return this.hasPacket;
    }

    @Override
    public void updateChange() {

        this.hasPacket = getAgentImp().hasCarry();   // agent pick Packet

    }
    private boolean hasPacket = true;

}