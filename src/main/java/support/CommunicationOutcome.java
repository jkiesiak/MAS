package support;

import agent.MailBuffer;

public class CommunicationOutcome extends Outcome {

    private final String token;
    private final MailBuffer mailBuffer;

    public CommunicationOutcome(int agent, boolean acted, int[] syncSet,
                                String token, MailBuffer buffer) {
        super(agent, acted, syncSet);
        this.token = token;
        this.mailBuffer = buffer;
        setCorrespHandler("PostalService");
    }

    public String getToken() {
        return token;
    }

    public MailBuffer getMailBuffer() {
        return mailBuffer;
    }

    public boolean getVoteForContinuingWithNextPhase() {
        return !getToken().equals("CC");
    }

    public String getType() {
        return "communication";
    }
}
