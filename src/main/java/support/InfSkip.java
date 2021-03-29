package support;

import environment.Environment;
import environment.world.agent.AgentWorld;

/**
 *  A class for influences for skipping a turn.
 */
public class InfSkip extends Influence {

    /**
     *  Initializes a new InfSkip object
     *  Cfr. super
     */
    public InfSkip(Environment environment, int id) {
        super(environment, 0, 0, id, null);
    }

    public InfSkip(Environment environment) {
        this(environment, -1);
    }

    /**
     *  Gets the area of effect (the World it wants to effect) for this
     *  Influence. We return AgentWorld but in fact this influence doesn't
     *  affect any world. Nothing will happen at all.
     * @return    The AgentWorld
     */
    @Override
    public AgentWorld getAreaOfEffect() {
        return getEnvironment().getWorld(AgentWorld.class);
    }
}
