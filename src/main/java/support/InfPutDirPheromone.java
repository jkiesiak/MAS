package support;

import environment.CellPerception;
import environment.Environment;
import environment.world.pheromone.PheromoneWorld;

/**
 *  A class for influences for putting down directed pheromones.
 */
public class InfPutDirPheromone extends Influence {

    /**
     *  Initializes a new InfPutDirPheromone object
     *  @see Influence
     */
    public InfPutDirPheromone(Environment environment, int x, int y, int id, CellPerception target) {
        super(environment, x, y, id, null);
        lifetime = -1;
        this.target = target;
    }

    /**
     *  Initializes a new InfPutPheromone object
     *  @see Influence
     */
    public InfPutDirPheromone(Environment environment, int x, int y, int id, int time, CellPerception target) {
        super(environment, x, y, id, null);
        lifetime = time;
        this.target = target;
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

    public CellPerception getTarget() {
        return target;
    }

    private final int lifetime;
    private final CellPerception target;

}
