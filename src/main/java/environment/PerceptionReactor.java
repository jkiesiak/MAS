package environment;

import environment.law.PerceptionLaw;
import environment.world.agent.AgentRep;
import util.Debug;
import util.Variables;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.Vector;

/**
 *  A class for PerceptionReactors. A PerceptionReactor sends perceptions to
 *  the agents. These perceptions are made according to perceptionLaws, a set
 *  of rules that govern what an agent can see.
 */

public class PerceptionReactor {

    private Environment env;
    private final Vector<PerceptionLaw> laws;

    /**
     *  Initializes a new PerceptionReactor object
     *
     * @param  environ  The environment this PerceptionReactor is part of.
     */
    public PerceptionReactor(Environment environ) {
        setEnvironment(environ);
        laws = new Vector<>();
        loadLaws();
    }

    /**
     *   Instantiate all PerceptionLaws listed in the 'perceptionlaws.properties'
     *   configuration file and add them to the 'laws' vector.
     *   The configuration file is read and we try to instantiate each class
     *   listed there and add it to the vector 'laws'.
     */
    public void loadLaws() {
        Properties perceptionLaws = new Properties();
        // open configuration file
        try {
            FileInputStream sf = new FileInputStream(Variables.PERCEPTION_LAWS_PROPERTIES_FILE);
            perceptionLaws.load(sf);
        } catch (Exception e) {
            Debug.alert(this, "error with perceptionlaws properties file: " + e);
        }
        int nbFound = 0;
        boolean stop = false;
        // vector for the strings we find in the configuration file
        Vector<String> found = new Vector<>();
        for (int i = 0; !stop; i++) {
            try {
                // add all worlds listed in the configuration file to 'found'
                found.addElement(perceptionLaws.getProperty( (Integer.valueOf(i + 1)).
                    toString(), "default"));
                if (! ( ( found.elementAt(i)).equals("default"))) {
                    nbFound++;
                    Debug.print(this, "found perception law: " + found.elementAt(i));
                } else {
                    stop = true;
                }
            } catch (Exception e) {
                Debug.print(this, "EOF (" + e + ")");
            }
        }
        // instantiate all the classes in 'found' and add them to 'laws'
        for (int i = 0; i < nbFound; i++) {
            try {
                PerceptionLaw pLaw = (PerceptionLaw) Class.forName(found.elementAt(i))
                        .getDeclaredConstructor()
                        .newInstance();
                laws.addElement(pLaw);
            } catch (Exception e) {
                Debug.alert(this, "Error setting perceptionlaws");
            }
        }
    }

    /**
     *  Sets the environment of this PerceptionReactor
     *
     * @param  environ  The environment value
     */
    private void setEnvironment(Environment environ) {
        this.env = environ;
    }

    /**
     *  Returns a perception for the given ActiveItem (including agents).
     *
     * @param   item  The perceiving ActiveItem
     * @pre     item != null
     * @return  A perception filled with representations of what 'item' can see
     *          after applying all the perceptionlaws listed in 'laws'
     */
    protected Perception getPerception(ActiveItem<?> item) {
        int view = item.getView();
        int width = env.getWidth();
        int height = env.getHeight();
        // we're talking about absolute coordinates here
        // we calculate the borders of the perception in the worlds
        int ax = item.getX();
        int ay = item.getY();
        int minX = Math.max(0, ax - view);
        int maxX = Math.min(width - 1, ax + view);
        int minY = Math.max(0, ay - view);
        int maxY = Math.min(height - 1, ay + view);
        // we initiate a Perception with the right dimensions
        Perception perception = new Perception(maxX - minX + 1, maxY - minY + 1,
                                         minX, minY);

        for (World<?> aWorld : env.getWorlds()) {
            for (int j = minX; j <= maxX; j++) {
                for (int k = minY; k <= maxY; k++) {
                    if (aWorld.getItem(j, k) != null) {
                        Representation tempRep = (aWorld.getItem(j, k)).getRepresentation();
                        perception.getCellAt(j - minX, k - minY).addRep(tempRep);

                        if (tempRep instanceof AgentRep && (((AgentRep) tempRep).getID() == item.getID())) {
                            perception.setSelfX(j - minX);
                            perception.setSelfY(k - minY);
                        }
                    }
                }
            }
        }

        // we enforce all known laws on 'perception'
        return enforceLaws(perception);
    }


    /**
     *   Enforce all known laws on a given Perception 'perception'.
     *
     *   @param perception The Perception to enforce all perceptionlaws upon
     *   @return A Perception that is become by enforcing all PerceptionLaws
     *           in 'laws' upon 'perception'
     */
    private Perception enforceLaws(Perception perception) {
        Perception temp = perception;
        for (int i = 0; i < laws.size(); i++) {
            temp = laws.elementAt(i).enforce(temp);
        }
        return temp;
    }
}
