package support;

/**
 *  A class representing the results (outcomes) of an Agent's perception-phase.
 */
public class PerceptionOutcome extends Outcome {

    /**
     *  Initializes a new PerceptionOutcome object
     *
     * @param  agent    the ID of the Agent involved
     * @param  acted    whether the agent has acted
     * @param  syncSet  the synchronisation set 'agent' belongs to
     */
    public PerceptionOutcome(int agent, boolean acted, int[] syncSet) {
        super(agent, acted, syncSet);
        setCorrespHandler("EOPHandler");
    }

    /**
     *  Gets the type of this PerceptionOutcome
     *
     * @return    This PerceptionOutcome's type
     */
    public String getType() {
        return "perception";
    }
}
