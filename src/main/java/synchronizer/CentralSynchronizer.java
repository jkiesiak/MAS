package synchronizer;

import agent.AgentImplementations;
import util.Debug;

/**
 *A class for objects synchronizing all agents in the system.
 */

public class CentralSynchronizer implements Synchronizer {

    /**
     *A container for id's of agents having this CentralSynchronizer instance as their synchronizer.
     *@invar for each i, j in 0..agents.length-1: getSynchronizer(i) == getSynchronizer(j)
     */
    private final int[] agents;

    /**
     *Initialize a central synchronizer, i.e. a synchronizer, connected to all the agents.
     *@post new.agents == getEnvironment().getAllAgentID()
     */
    public CentralSynchronizer(AgentImplementations agentImps) {
        agents = agentImps.getAllAgentID();
        //begin{TEST}
        Debug.print(this, "Synchronizer initialized on the following agentset:");
        for (int agent : agents) {
            Debug.print(this, agent + "  ");
        }
        //end{TEST}
    }

    /**
     *Return the syncSet of the agent with the name id. Under central synchronization, the returned syncset consists of the id's of
     * all other agents active in the system.
     *@param agent
     *       The name of the agent requesting its syncset.
     *@return The set of id's of agents with which the requesting agent is bound to synchronize.
     *        !(agent in getSyncSet(agent)) &&
     *        for each i in agents[i]: if agents[i] != agent, then agents[i] in getSyncSet(agent)
     */
    public synchronized int[] getSyncSet(int agent) {
        int[] sS = new int[agents.length - 1];
        int j = 0;
        for (int k : agents) {
            if (k != agent) {
                sS[j] = k;
                j++;
            }
        }
        return sS;
    }

    /**
     *Form the syncset for the agent with name agent, based on the set of candidates with which to synchronize and agent's syncTime
     * Under central synchronization, this method has no effect.
     *@param agent
     *       The id of the agent requesting formation of its syncset.
     *@param setOfCandidates
     *       The set of agent-id's that are candidate-members of the syncset to be formed.
     *@param time
     *       The syncTime of the requesting agent.
     *@post  new getSycnSet(agent)==getSyncSet(agent)
     */
    public void synchronize(int agent, int[] setOfCandidates, int time) {
        // NO-OP;
    }

}
