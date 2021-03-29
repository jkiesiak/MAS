package support;

/**
 *   A class for placeholders for outcomes
 */
public class PlaceHolderOutcome extends Outcome {

    public PlaceHolderOutcome(int agent) {
        super(agent, false, null);
        setCorrespHandler("");
    }

    public String getType() {
        return "placeholder";
    }
}
