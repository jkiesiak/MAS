package agent;

import agent.behaviour.Behaviour;
import agent.behaviour.BehaviourChange;
import agent.behaviour.BehaviourState;
import util.AsciiReader;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This Agent Implementation reads its behaviour graph from file
 * @see AgentImp
 */
public class FileAgentImp extends AgentImp {
    /**
     * Creates an agent with a behaviour that is specified through a configuration file
     * @preconditions file != null
     * @preconditions file != ""
     * @param file    the name of the file having the configuration for this Agent
     * @param ID      see superclass
     * @param maxBeliefs: number of maximum beliefs this AgentImp should be able to store
     */
    public FileAgentImp(int ID, final String file, int maxBeliefs) {
        super(ID, maxBeliefs);
        config = file;
    }

    public FileAgentImp(int ID, final String file) {
        this(ID, file, AgentImp.DEFAULT_BELIEFS);
    }

    /**
     * Creates this Agent's behaviour by loading a behaviour graph from file.
     */
    public void createBehaviour() {
        try {
            AsciiReader reader = new AsciiReader(config);
            reader.check("description");
            reader.readNext(); //skipping description
            reader.check("nbStates");
            int nbStates = reader.readInt();
            BehaviourState[] states = new BehaviourState[nbStates];
            for (int i = 0; i < nbStates; i++) {
                reader.check(Integer.toString(i + 1));
                states[i] = new BehaviourState( (Behaviour) reader.
                                               readClassConstructor());
            }
            reader.check("nbChanges");
            int nbChanges = reader.readInt();

            for (int i = 0; i < nbChanges; i++) {
                BehaviourChange change = (BehaviourChange) reader.readClassConstructor();
                change.setAgentImp(this);
                reader.check("priority");
                int priority = reader.readInt();

                reader.check("source");
                states[reader.readInt() - 1].addChange(change, priority);
                reader.check("target");
                change.setNextBehaviour(states[reader.readInt() - 1]);
            }
            setCurrentBehaviourState(states[0]);
        } catch (FileNotFoundException e) {
            System.err.println("Behaviour config file not found: " + config +
                               "\n" + e.getMessage());
        } catch (IOException e) {
            System.err.println("Something went wrong while reading: " + config +
                               "\n" + e.getMessage());
        } catch (Exception e) {
            System.err.println(
                "Something went wrong while loading behaviour: \n" +
                e.getMessage());
        }
    }

    private final String config;
}
