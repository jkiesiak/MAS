package agent.behaviour.basic.change;

import agent.behaviour.BehaviourChange;

public class DestinationInPerception extends BehaviourChange {

    @Override
    public boolean isSatisfied() {

        return this.seeDestination;
    }
    @Override
    public void updateChange() {
        this.seeDestination = getAgentImp().seeDestination();
    }
    private boolean seeDestination = false;

}
