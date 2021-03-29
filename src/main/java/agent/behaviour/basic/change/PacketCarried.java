package agent.behaviour.basic.change;

import agent.behaviour.BehaviourChange;

public class PacketCarried extends BehaviourChange {

    @Override
    public boolean isSatisfied() {
        return this.hasPacket;
    }

    @Override
    public void updateChange() {

        this.hasPacket = getAgentImp().hasCarry();   // agent pick Packet

    }
    private boolean hasPacket = false;

}