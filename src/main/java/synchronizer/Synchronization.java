package synchronizer;

import agent.AgentImplementations;
import environment.Environment;

/**
 *An abstract superclass for interfaces of the synchronizer-package.
 */
public abstract class Synchronization {

    /**
     * A pointer towards the interface of the environment package.
     */
    private Environment env;

    /**
     *A pointer towards the interface of the agentImplementations package.
     */
    private AgentImplementations impl;

    /**
     *Return a reference to the interface of the environment package.
     */
    Environment getEnvironment() {
        return env;
    }

    /**
     *Associate this Synchronization instance with environment.
     *@param environment
     *       The interface of the environment-package
     *@pre   environment <> null
     *@post  new.getEnvironment()==environment
     */
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    /**
     *Return a reference to the interface of the agentImplementations package.
     */
    AgentImplementations getAgentImplementations() {
        return impl;
    }

    /**
     *Associate this Synchronization instance with implementations.
     *@param implementations
     *       The interface of the agentimplementations package.
     *@pre agentimplementations <> null
     *@post new.getAgentImplementations() == implementations
     */
    public void setAgentImplementations(AgentImplementations implementations) {
        impl = implementations;
    }

    /**
     *Return the syncSet of the agent with the name id.
     *@param id
     *       The name of the agent requesting its syncset.
     *@return The set of id's of agents with which the requesting agent is bound to synchronize.
     */
    public int[] getSyncSet(int id) {
        return getSynchronizer(id).getSyncSet(id);
    }

    /**
     *Form the syncset of the agent with name id, given the set setofCandidates of candidates with which to synchronize and given
     *syncTime time.
     *@param id
     *       The id of the agent requesting formation of its syncset.
     *@param setOfCandidates
     *       The set of agent-id's that are candidate-members of the syncset to be formed.
     *@param time
     *       The syncTime of the requesting agent.
     *@post
     */

    public void synchronize(int id, int[] setOfCandidates, int time) {
        getSynchronizer(id).synchronize(id, setOfCandidates, time);
    }

    /**
     *Return the synchronizer handling the requests of the agent with name id.
     *@param id
     *       The id of the agent issuing a request.
     *@return The synchronizer responsible for handling requests of the agent with name id.
     */
    protected abstract Synchronizer getSynchronizer(int id);

    /**
     *Create the instances of classes of the synchronizer package needed at startup time.
     */
    public abstract void createSynchroPackage();

}
