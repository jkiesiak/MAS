package support;

import environment.Environment;
import environment.world.flag.FlagWorld;

import java.awt.*;

/**
 *  A class for influences for putting down flags.
 */
public class InfPutFlag extends Influence {

    /**
     *  Initializes a new InfPutFlag object
     *  Cfr. super
     */
    public InfPutFlag(Environment environment, int x, int y, int id, Color c) {
        super(environment, x, y, id, c);
    }

    /**
         *  Gets the area of effect (the World it wants to effect) for this Influence
     *
     * @return    The FlagWorld
     */
    @Override
    public FlagWorld getAreaOfEffect() {
        return getEnvironment().getWorld(FlagWorld.class);
    }

}
