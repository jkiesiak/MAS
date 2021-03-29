/* Generated by Together */

package util.event;

import agent.AgentImp;

public class AgentHandledEvent extends Event {
    public AgentHandledEvent(Object thrower) {
        super(thrower);
    }

    public int getID() {
        return ID;
    }

    protected void setID(int ID) {
        this.ID = ID;
    }

    public AgentImp getAgent() {
        return agent;
    }

    public void setAgent(AgentImp agent) {
        this.agent = agent;
        setID(agent.getID());
    }

    private int ID;
    private AgentImp agent;
}
