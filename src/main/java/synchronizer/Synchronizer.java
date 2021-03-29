package synchronizer;

/**
 *A common interface for both central and personal synchronizers.
 * Note: this interface isn't really needed in the new structure and can be dispensed with in a later stage.
 */
public interface Synchronizer {

    int[] getSyncSet(int id);

    void synchronize(int agent_id, int[] setOfCandidates, int time);

}
