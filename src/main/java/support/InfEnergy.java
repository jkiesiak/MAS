package support;

import environment.Environment;
import environment.world.agent.AgentWorld;

/**
 *  A class for influences that EnergyStations exercise on the Environment.
 */
public class InfEnergy extends Influence {

    /**
     *  Initializes a new InfEnergy object
     *  @see Influence
     *
     * @param  strength  the amount of energy this influence carries
     */
    public InfEnergy(Environment environment, int x, int y, int ID, int strength) {
        super(environment, x, y, ID, null);
        this.strength = strength;
    }

    /**
     *  Gets the area of effect (the World it wants to effect) for this Influence
     *
     * @return    the AgentWorld
     */
    @Override
    public AgentWorld getAreaOfEffect() {
        return getEnvironment().getWorld(AgentWorld.class);
    }


    /**
     * Returns the amount of energy that is transferred by this influence
     *
     * @return the strength of this influence
     */
    public int getLoadAmount() {
        return strength;
    }


    /**
     * The amount of energy that gets transferred to the effect area of this
     * influence
     */
    private final int strength;
}