package support;

import environment.Environment;
import environment.world.pheromone.PheromoneWorld;

/**
 *  A class for influences for putting down pheromones.
 */
public class InfPutPheromone extends Influence {

    /**
     *  Initializes a new InfPutPheromone object
     *  @see Influence
     */
    public InfPutPheromone(Environment environment, int x, int y, int id) {
        super(environment, x, y, id, null);
        lifetime = -1;
    }

    /**
     *  Initializes a new InfPutPheromone object
     *  @see Influence
     */
    public InfPutPheromone(Environment environment, int x, int y, int id, int time) {
        super(environment, x, y, id, null);
        lifetime = time;
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

    public int getLifeTime() {
        return lifetime;
    }

    private final int lifetime;

}
