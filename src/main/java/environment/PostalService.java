package environment;

import agent.AgentImplementations;
import util.Debug;
import util.event.EventManager;
import util.event.MsgSentEvent;

/**
 *A class for handling communicationOutcomes in a completed sphere. This class is given an own thread of execution. A postal-
 * Service is woken up by an incoming bag of mails and is responsible for instant delivery of these mails into the personal
 * mailboxes of the addressees.After this has been done, a postalService notifies the sphere that delivered the set.</p>
 */
public class PostalService extends Handler {

    private final AgentImplementations agentImplementations;

    /**
     *Initialize a new PostalService. At initialization, a postalService controls an empty set of MailBags and holds a reference
     * to the interface of the agentImplementations-package.
     *@param agentImps A reference to the interface of the agentImplementations-package.
     *@post
     */
    public PostalService(AgentImplementations agentImps) {
        super();
        agentImplementations = agentImps;
    }

    protected void process(ToHandle toBeHandled) {
        MailBag bag = null;
        try {
            bag = (MailBag) toBeHandled;
        } catch (ClassCastException e) {
            Debug.alert(this, "Wrong handler!!!!");
        }
        Mail[] mailset = bag.getMailSet();
        boolean[] turns = new boolean[mailset.length];
        for (int i = 0; i < mailset.length; i++) {
            Mail toDeliver = mailset[nextActive(turns)];
            String from = toDeliver.getFrom();
            String to = toDeliver.getTo();
            String msg = toDeliver.getMessage();
            try {
                getAgentImplementations().sendMessage(
                    getAgentImplementations().getAgentID(to),
                    toDeliver);
            } catch (IllegalArgumentException e) {
                Debug.alert(this, e.getMessage());
            }
            Debug.print(this,
                        "Mail from " + from + " to " + to +
                        " has been delivered");
            MsgSentEvent se = new MsgSentEvent(this);
            se.setMsg(toDeliver);
            EventManager.getInstance().throwEvent(se);
        }
        Debug.print(this, "" + mailset.length + " mails have been delivered");
        toBeHandled.getSendingSphere().setHandled(bag.getNbCorrespOutcomes());
    }

    AgentImplementations getAgentImplementations() {
        return agentImplementations;
    }

}
