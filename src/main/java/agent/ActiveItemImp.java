package agent;

import support.CommunicationOutcome;
import support.PerceptionOutcome;
import util.event.Event;

/**
 * This class represents the implementation of an active object in the MAS.
 * It is an object other than an agent that has an influence in the
 * Environment. It interacts with the Environment for new information of the
 * world by running a separate thread.
 */
abstract public class ActiveItemImp extends ActiveImp {
    public ActiveItemImp(int ID) {
        super(ID);
    }

    protected void cleanup() {}

    /**
     * Implements the execution of a synchronization phase.
     */
    protected void execCurrentPhase() {
        if (perceiving) {
            percept();
            PerceptionOutcome outc = new PerceptionOutcome(getID(), true,
                getSyncSet());
            concludePhaseWith(outc);

            nbTurn++;
            setSynctime(getEnvironment().getTime());
        } else if (talking) {
            CommunicationOutcome outc = new CommunicationOutcome(getID(), true,
                getSyncSet(), "EOC", new MailBuffer());

            //Influence infl = new InfSkip(getEnvironment());
            //ActionOutcome outc = new ActionOutcome(getID(), true, getSyncSet(),
            //                                       infl);
            concludePhaseWith(outc);
        } else if (doing) {
            action();
        }
    }

    protected boolean environmentPermissionNeededForNextPhase() {
        return true;
    }


    public void catchEvent(Event e) {
    }
}