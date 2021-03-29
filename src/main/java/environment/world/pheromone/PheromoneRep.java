package environment.world.pheromone;

import environment.Representation;

/**
 *  a class for representations of pheromones.
 */
public class PheromoneRep extends Representation {

    /**
     *  Initializes a new PheromoneRep instance
     *
     * @param  x         X-coordinate of the Pheromone this representation
     *                   represents
     * @param  y         Y-coordinate of the Pheromone this representation
     *                   represents
     * @param  lifetime  the lifetime of the Pheromone this representration
     *                   represents
     */
    protected PheromoneRep(int x, int y, int lifetime) {
        super(x, y);
        setLifetime(lifetime);
    }

    public char getTypeChar() {
        return ('~');
    }

    public int getLifetime() {
        return lifetime;
    }

    protected void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }

    @Override
    public boolean isWalkable() {
        return true;
    }

    private int lifetime;

}
