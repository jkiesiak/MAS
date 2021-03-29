package environment.law;

import environment.Environment;
import environment.world.crumb.CrumbWorld;
import support.InfPickCrumb;
import support.Influence;

/**
 *  A law controlling the picking up of crumbs.
 */
public class LawPickCrumb implements Law {

    private Environment env;

    /**
     *  Initializes a new LawPickPacket instance
     */
    public LawPickCrumb() {}

    /**
     *  Check if this Law is applicable to the given Influence 'inf'
     *
     * @param  inf  The influence to check
     * @return  'true' if 'inf' is an instance of the InfPickCrumb class
     */
    public boolean applicable(Influence inf) {
        return inf instanceof InfPickCrumb;
    }

    /**
     *  Check if the given Influence 'inf' passes validation by this Law.
     *
     * @param  inf  The influence to check
     * @return  'true' if there actually is a number of crumbs to pick up
     */
    public boolean apply(Influence inf) {
        return env.getWorld(CrumbWorld.class).getItem(inf.getX(), inf.getY()) != null;
    }

    /**
     *  Sets the environment of this LawPickPacket
     *
     * @param  env  The new environment value
     */
    public void setEnvironment(Environment env) {
        this.env = env;
    }

}
