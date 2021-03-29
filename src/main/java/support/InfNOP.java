package support;

import environment.Environment;
import environment.World;

public class InfNOP extends Influence {

    public InfNOP(Environment environment) {
        super(environment, -1, -1, -1, null);
    }

    @Override
    public World<?> getAreaOfEffect() {
        return getEnvironment().getAgentWorld();
    }
}
