package support;

import environment.Environment;
import environment.world.crumb.CrumbWorld;

/**
 *  A class for influences for picking up crumbs.
 */
public class InfPickCrumb extends Influence {

    /**
     *  Initializes a new InfPutCrumb object
     *  Cfr. super
     */
    public InfPickCrumb(Environment environment, int x, int y, int id, int number) {
        super(environment, x, y, id, null);
        this.number = number;
    }

    /**
     *  Gets the area of effect (the World it wants to effect) for this
     *  Influence
     *
     * @return    the CrumbWorld
     */
    @Override
    public CrumbWorld getAreaOfEffect() {
        return getEnvironment().getWorld(CrumbWorld.class);
    }

    public int getNumber() {
        return number;
    }

    private final int number;
}