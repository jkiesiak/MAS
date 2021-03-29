package environment;

import util.Debug;

public class EOPHandler extends Handler {

    public EOPHandler() {
        super();
    }

    protected void process(ToHandle toBeHandled) {
        EOPSet set = null;
        try {
            set = (EOPSet) toBeHandled;
        } catch (ClassCastException e) {
            Debug.alert(this, "Wrong handler!!!");
        }
        // NO-OP: the environment does not react to tokens of agents that they have ended perception; this class is merely there
        // for elegance (parallellism in structure).
        toBeHandled.getSendingSphere().setHandled(set.getNbCorrespOutcomes());
    }
}
