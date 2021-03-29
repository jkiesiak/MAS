package environment.law;

import environment.CollisionMatrix;
import environment.Environment;
import environment.World;
import environment.world.agent.Agent;
import support.InfStep;
import support.Influence;
import util.Debug;

/**
 *  A law controlling the movement of agents.
 */
public class LawStep implements Law {

    private Environment env;

    /**
     *  Initializes a new LawStep instance
     */
    public LawStep() {}

    /**
     *  Check if this Law is applicable to the given Influence 'inf'
     *
     * @param  inf  The influence to check
     * @return  'true' if 'inf' is an instance of the InfStep class
     */
    public boolean applicable(Influence inf) {
        return inf instanceof InfStep;
    }

    /**
     *  Check if the given Influence 'inf' passes validation by this Law.
     *
     * @param  inf  The influence to check
     * @return  'true' if the coordinates given in 'inf' hold a Free Item on each World ('env.getAllWorlds()')
     */
    public boolean apply(Influence inf) {
        Agent agent = env.getAgentWorld().getAgent(inf.getID());
        int dist = World.distance(agent.getX(), agent.getY(), inf.getX(), inf.getY());

        if (dist != 1) {
            Debug.alert(this,
                        "Illegal action for this influence." +
                        " No move allowed from (" +
                        agent.getX() + "," + agent.getY() + ") to (" +
                        inf.getX() + "," + inf.getY() + ").");
            return false;
        }

        return (CollisionMatrix.AgentCanStandOn(env, inf.getX(), inf.getY()));
    }

    /**
     *  Sets the environment of this LawStep
     *
     * @param  env  The new environment value
     */
    public void setEnvironment(Environment env) {
        this.env = env;
    }
}
