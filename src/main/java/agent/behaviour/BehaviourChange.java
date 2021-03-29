package agent.behaviour;


import agent.AgentImp;


abstract public class BehaviourChange {

    /**
     * Checks if the condition for this Behaviourchange is satisfied
     */
    abstract public boolean isSatisfied();

    /**
     * Updates this change. The conditions in this change are
     * computed/verified.
     */
    public abstract void updateChange();

    /**
     * Updates this change according to the information contained in the Agent.
     * If the update lets this Change fire and the precondition of the
     * target is true, then the owner Agent's currentBehaviourState is set to
     * the next state pointed by this BehaviourChange.
     */
    public boolean testChange() {
        if (isSatisfied() &&
            lnkBehaviourState.getBehaviour().precondition(getAgentImp())) {
            lnkAgentImp.getCurrentBehaviour().leave(lnkAgentImp);
            lnkAgentImp.setCurrentBehaviourState(lnkBehaviourState);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets the target behaviour, specified by its flyweight BehaviourState
     */
    public void setNextBehaviour(BehaviourState bs) {
        lnkBehaviourState = bs;
    }

    /**
     * Association to the owning AgentImp
     */
    public AgentImp getAgentImp() {
        return lnkAgentImp;
    }

    /**
     * Sets association to the owning AgentImp
     */
    public void setAgentImp(AgentImp lnkAgentImmp) {
        this.lnkAgentImp = lnkAgentImmp;
    }

    /**
     * DESTRUCTOR
     */
    public void finish() {
        lnkAgentImp = null;
        lnkBehaviourState.finish();
        lnkBehaviourState = null;
    }

    /**
     * @directed
     * @supplierCardinality 0..1
     */
    private BehaviourState lnkBehaviourState;

    /**
     * @directed
     */
    private AgentImp lnkAgentImp;
}
