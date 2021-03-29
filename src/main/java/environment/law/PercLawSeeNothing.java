package environment.law;

import environment.Perception;
import environment.world.agent.AgentRep;

/**
 *  A PerceptionLaw that doesn't allow an agent to perceive 'any'
 *  specific information. When this law is applied agents will
 *  perceive every Item as a Representation, which has no informative
 *  value for them, except that there is 'something'.
 */
public class PercLawSeeNothing implements PerceptionLaw {

    /**
     *  Initializes a new PercLawSeeNothing instance
     */
    public PercLawSeeNothing() {}

    /**
     *  Enforces this PerceptionLaw on a given Perception 'perc'.
     *  All CellPerceptions in 'perc' will be emptied, thus removing
     *  any specific information about items (representations) on them.
     *
     * @param  perc The perception on which we will enforce this perceptionlaw
     * @return  A perception containing only unspecified 'Representations'.
     *          The only information that will remain in these representations
         *          are the coordinates of the Items they represent, nothing further.
     */
    public Perception enforce(Perception perc) {
        Perception newPerc = new Perception(perc.getWidth(), perc.getHeight(),
                                            perc.getOffsetX(), perc.getOffsetY());
        int aX = perc.getSelfX();
        int aY = perc.getSelfY();

        newPerc.getCellAt(aX, aY).addRep(perc.getCellAt(aX, aY).getRepOfType(AgentRep.class));
        newPerc.setSelfX(aX);
        newPerc.setSelfY(aY);
        return newPerc;
    }

}
