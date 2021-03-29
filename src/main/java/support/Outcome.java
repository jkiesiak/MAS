package support;

/**
 *  A class representing the outcome of an action phase. Depending on the kind
 *  of activity performed in that phase, the outcome will subclass into a
 *  particular kind. The basic idea behind this kind of class is to provide a
 *  uniform wrapper for very different sort of outcomes, which allows for a
 *  generic collector dealing uniformily with them all.
 */

public abstract class Outcome {

    private final int agent;
    private final boolean acted;
    private final int[] syncSet;
    private String correspHandler;

    /**
     *  Initializes a new Outcome object
     *
     * @param  agent    the ID of the Agent involved
     * @param  acted    whether the agent has acted
     * @param  syncSet  the synchronisation set 'agent' belongs to
     */
    public Outcome(int agent, boolean acted, int[] syncSet) {
        this.agent = agent;
        this.syncSet = syncSet;
        this.acted = acted;
    }

    /**
     *  Gets the agentID of this Outcome
     *
     * @return    This agentID
     */
    public int getAgentID() {
        return agent;
    }

    /**
     *  Gets the syncSet of this Outcome
     *
     * @return    This syncSet
     */
    public int[] getSyncSet() {
        return syncSet;
    }

    /**
     *  Check if this Outcome's agent has acted or not
     *
     * @return  'true' if this agent has acted
     */
    public boolean hasActed() {
        return acted;
    }

    /**
     *  Gets the type of this Outcome
     *
     * @return    This Outcome's type
     */
    public abstract String getType();

    /**
     *  Sets the correspHandler of this Outcome
     *
     * @param  handler  The new correspHandler value
     */
    protected void setCorrespHandler(String handler) {
        correspHandler = handler;
    }

    /**
     *  Gets the correspHandler of this Outcome
     *
     * @return    This Outcome's correspHandler
     */
    public String getCorrespHandler() {
        return correspHandler;
    }

    /**
     *  Check if this Outcome is to be handled by the given handler 'handler' (by String)
     *
     * @param  handler  The handler for which we are checking if it can handle this Outcome
     * @return  'true' if this Outcome's corresponding handler equals 'handler'
     */
    public boolean toBeHandledBy(String handler) {
        return correspHandler.equals(handler);
    }

    /**
     *  Gets the vote for continuing with the next phase
     *
     * @return    'true'
     */
    public boolean getVoteForContinuingWithNextPhase() {
        return true;
    }

}
