package environment.law;

import environment.Environment;
import support.InfPassPacket;
import support.Influence;

/**
 *  A law controlling the passing of packets.
 */
public class LawPassPacket implements Law {

    private Environment env;

    /**
     *  Initializes a new LawPassPacket instance
     */
    public LawPassPacket() {}

    /**
     *  Check if this Law is applicable to the given Influence 'inf'
     *
     * @param  inf  The influence to check
     * @return  'true' if 'inf' is an instance of the InfPassPacket class
     */
    public boolean applicable(Influence inf) {
        return inf instanceof InfPassPacket;
    }

    /**
     *  Check if the given Influence 'inf' passes validation by this Law.
     *
     * @param  inf  The influence to check
     * @return 'true' if the coordinates given in 'inf' hold an Agent that
     *         has no carry
     */
    public boolean apply(Influence inf) {
        return env.getAgentWorld().getItem(inf.getX(), inf.getY()) != null &&
                env.getAgentWorld().getItem(inf.getX(), inf.getY()).carry() == null;
    }

    /**
     *  Sets the environment of this LawPassPacket
     *
     * @param  env  The new environment value
     */
    public void setEnvironment(Environment env) {
        this.env = env;
    }

}