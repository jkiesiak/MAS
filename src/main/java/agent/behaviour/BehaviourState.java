package agent.behaviour;

import util.Debug;

import java.util.*;

/**
 * This class acts a [GoF]FlyWeight pattern for the Behaviour class. It enables
 * big Behaviours to appear a lot in behaviour graphs.
 */
public class BehaviourState {

    /**
     * Creates a new BehaviourState for the given Behaviour
     * @param b: the Behaviour to point
     */
    public BehaviourState(Behaviour b) {
        lnkBehaviourChange = new HashMap<>();
        lnkBehaviour = b;
    }

    /**
     * Returns the real Behaviour
     */
    public Behaviour getBehaviour() {
        return lnkBehaviour;
    }

    /**
     * Evaluates all succeeding BehaviourChanges in random order. When the
     * postcondition of the current Behaviour is false, no updates or changes
     * are done.
     * If a BehaviourChange fires then the current Behaviour State of the owner
     * Agent will change to the one pointed to by the last tested
     * 'positive/open' Change.
     */
    public void testBehaviourChanges() {
        if (lnkBehaviourChange.size() == 0 || !lnkBehaviour.postcondition()) {
            return;
        }

        for (var changes : lnkBehaviourChange.values()) {
            changes.forEach(BehaviourChange::updateChange);
        }

        TreeSet<Integer> priorities = new TreeSet<>(Comparator.reverseOrder());
        priorities.addAll(lnkBehaviourChange.keySet());

        for (int key : priorities) {
            var changes = lnkBehaviourChange.get(key);
            int[] rand = createRandom(changes.size());

            // Randomly try changes within the same priority level
            for (int i = 0; i < changes.size(); i++) {
                if (changes.get(rand[i]).testChange()) {
                    Debug.print(this, "changed with " + changes.get(rand[i]).toString());
                    return;
                }
            }

        }
    }


    /**
     * Adds a BehaviourChange with a given priority level (higher priority values implies a higher priority)
     */
    public void addChange(BehaviourChange bc, int priority) {
        if (!lnkBehaviourChange.containsKey(priority)) {
            lnkBehaviourChange.put(priority, new ArrayList<>());
        }

        lnkBehaviourChange.get(priority).add(bc);
    }

    private int[] createRandom(int length) {
        int[] result = new int[length];
        for (int i = 0; i < length; i++) {
            result[i] = -1;
        }
        int count = 0;
        int index;
        while (count < length) {
            index = rnd.nextInt(length);
            if (result[index] == -1) {
                result[index] = count;
                count++;
            }
        }
        return result;
    }

    /**
     * DESTRUCTOR. Cleans up this BehaviourState
     */
    public void finish() {
        if (closing) {
            return;
        }
        closing = true;
        lnkBehaviour.finish();
        lnkBehaviour = null;

        for (var changes : lnkBehaviourChange.values()) {
            changes.forEach(BehaviourChange::finish);
        }
        lnkBehaviourChange.clear();
        lnkBehaviourChange = null;
    }

    /**
     * @invariant lnkBehaviourChange.getElement instanceof BehaviourChange
     * @associates <{AgentImplementations.BehaviourChange}>
     * @directed
     * @supplierCardinality 0..*
     */
    private Map<Integer, List<BehaviourChange>> lnkBehaviourChange;

    /**
     * @directed
     * @supplierCardinality 1*/
    private Behaviour lnkBehaviour;

    //signals if this state is already closing.
    private boolean closing = false;

    static final Random rnd = new Random();
}
