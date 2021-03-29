package environment.law;

import environment.Perception;

/**
 *  An interface for perceptionLaws, being objects used by PerceptionReactor
 *  to alter perception by Agents.
 */
public interface PerceptionLaw {

    /**
     *  Enforces this PerceptionLaw on a given Perception 'perc'.
     */
    Perception enforce(Perception perc);

}
