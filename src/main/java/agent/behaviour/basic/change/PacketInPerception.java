package agent.behaviour.basic.change;

import agent.behaviour.BehaviourChange;

public class PacketInPerception extends BehaviourChange {

    @Override
    public void updateChange() {

        this.seePacket = getAgentImp().seePacket();

    }
    @Override
    public boolean isSatisfied() {
        return this.seePacket;
    }
    private boolean seePacket = false;
}

