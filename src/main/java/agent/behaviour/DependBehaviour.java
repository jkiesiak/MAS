package agent.behaviour;

import agent.AgentImp;

/**
 * This class represents a dependency role.
 */
public abstract class DependBehaviour extends Behaviour {

    /**
     * This method checks if this agent sees a sign that an agent who is
     * dependant on this agent, signals this dependency
     * (cfr methodology "Casseiopea", Alexis Drogoul)
     */
    public abstract boolean checkDependency(AgentImp agent);
}
