package agent.behaviour;

import agent.AgentImp;

import java.util.Vector;

/**
 * This class represents a role for an agent. It contains the actions the agent
 * does when playing this role.
 */
abstract public class Behaviour {

    /**
     * Enables to create multiple instance of a behaviour. This is only
     * recommended for light Behaviour classes.
     * @deprecated
     */
    @Deprecated
    protected Behaviour(boolean singleinstance) {
    }

    protected Behaviour() {
        this(true);
    }

    /**
     * This method triggers the behaviour to take action. If there are any
     * Behaviours that are dependent on this behaviour and are signalling
     * their need to help in the dependancy, then the dependancy Behaviour
     * will take priority. If the dependant Behaviour has not fully handled,
     * then this behaviour can still take action.
     */
    public void handle(AgentImp agent) {
        hasHandled = false;
        for (int i = 0; i < dependers.size(); i++) {
            if (getDepender(i).checkDependency(agent)) {
                getDepender(i).handle(agent);
            }
            if (getDepender(i).hasHandled()) {
                this.hasHandled = true;
            }
        }
    }

    /**
     * This method checks if the precondition of the behaviour is fulfilled
     * Note: subclasses should always call superclass precondition first
     */
    public boolean precondition(AgentImp agent) {
        return true;
    }

    /**
     * This method checks if the postcondition of the behaviour is fulfilled
     * Note: subclasses should always call superclass postcondition first
     */
    public boolean postcondition() {
        return true;
    }

    /**
     * Returns the depending behaviour at index
     */
    protected DependBehaviour getDepender(int index) {
        return dependers.elementAt(index);
    }

    /**
     * adds a dependent behaviour
     */
    protected void addDependency(DependBehaviour dep) {
        dependers.addElement(dep);
    }

    /**
     * Checks if this role has done for this synchronization cycle
     */
    public boolean hasHandled() {
        return hasHandled;
    }

    /**
     * Returns a description of this role
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets a description for this role
     */
    protected void setDescription(String description) {
        this.description = description;
    }

    /**
     * DESTRUCTOR. Clears this role.
     */
    public void finish() {
        if (closing) {
            return;
        }
        closing = true;
        for (int i = 0; i < dependers.size(); i++) {
            dependers.elementAt(i).finish();
        }
        dependers.removeAllElements();
        dependers = null;
        description = null;
    }

    /**
     * Actions to take before leaving this role
     */
    public void leave(AgentImp agent) {
    }

    //attributes
    private Vector<DependBehaviour> dependers = new Vector<>();
    private String description;
    public static boolean singleInstance = true;
    protected boolean hasHandled;
    private boolean closing = false;
}
