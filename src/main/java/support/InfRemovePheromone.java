package support;

import environment.Environment;
import environment.world.pheromone.PheromoneWorld;

/**
 *  A class for influences for removing pheromones.
 */
public class InfRemovePheromone extends Influence {

    /**
     *  Initializes a new InfPutPheromone object
     *  @see Influence
     */
    public InfRemovePheromone(Environment environment, int x, int y, int id) {
        super(environment, x, y, id, null);
    }

    /**
     *  Gets the area of effect (the World to effect) for this Influence
     *
     * @return    The PheromoneWorld
     */
    @Override
    public PheromoneWorld getAreaOfEffect() {
        return getEnvironment().getWorld(PheromoneWorld.class);
    }
}
