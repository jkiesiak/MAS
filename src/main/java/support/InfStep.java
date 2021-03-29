package support;

import environment.Environment;
import environment.world.agent.AgentWorld;

/**
 *  A class for influences for stepping to a certain position.
 */
public class InfStep extends Influence {

    /**
     *  Initializes a new InfStep object
     *  Cfr. super
     */
    public InfStep(Environment environment, int x, int y, int agent) {
        super(environment, x, y, agent, null);
    }

    /**
         *  Gets the area of effect (the World it wants to effect) for this Influence
     *
     * @return    The AgentWorld
     */
    @Override
    public AgentWorld getAreaOfEffect() {
        return getEnvironment().getWorld(AgentWorld.class);
    }

}
