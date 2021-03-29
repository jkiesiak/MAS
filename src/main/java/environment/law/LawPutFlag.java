package environment.law;

import environment.CollisionMatrix;
import environment.Environment;
import support.InfPutFlag;
import support.Influence;

/**
 *  A law controlling the putting down of Flags.
 */
public class LawPutFlag implements Law {

    private Environment env;

    /**
     *  Initializes a new LawPutFlag instance
     */
    public LawPutFlag() {}

    /**
     * Check if this Law is applicable to the given Influence 'inf'
     *
     * @param  inf  The influence to check
     * @return  'true' if 'inf' is an instance of the InfPutFlag class
     */
    public boolean applicable(Influence inf) {
        return inf instanceof InfPutFlag;
    }

    /**
     *  Check if the given Influence 'inf' passes validation by this Law.
     *
     * @param  inf  The influence to check
     * @return  'true' if the coordinate given in 'inf' denotes a free area
     */
    public boolean apply(Influence inf) {
        return CollisionMatrix.FlagCanStandOn(env, inf.getX(), inf.getY());
    }

    /**
     *  Sets the environment of this LawPutFlag
     *
     * @param  env  The new environment value
     */
    public void setEnvironment(Environment env) {
        this.env = env;
    }

}
