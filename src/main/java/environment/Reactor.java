package environment;

import environment.law.Law;
import environment.world.agent.Agent;
import environment.world.packet.PacketWorld;
import gui.setup.Setup;
import gui.video.VideoPanel;
import support.*;
import util.Debug;
import util.Variables;
import util.event.EventManager;
import util.event.GameOverEvent;
import util.event.WorldProcessedEvent;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.Vector;

/**
 *  A class for reactors. A reactor processes influenceBuffers.
 *  A reactors is a kind of handler (see Handler class).
 */
public class Reactor extends Handler {

    private Environment env;
    private int conflicts;
    private final Vector<Law> laws;

    /**
     *  Initializes a new Reactor object
     *
     * @param  environ  the environment which this Reactor is part of
     */
    public Reactor(Environment environ) {
        super();
        setEnvironment(environ);
        laws = new Vector<>();
        loadLaws();
    }

    /**
     *   Instantiate all Laws listed in the 'lawsoftheuniverse.properties' configurationFile and add them
     *   to the 'laws' vector.
     *   The configurationFile is read and we try to instantiate each class listed there and
     *   add it to the vector 'laws'.
     */
    public void loadLaws() {
        Properties universeLaws = new Properties();
        // open configuration file
        try {
            FileInputStream sf = new FileInputStream(Variables.LAWS_PROPERTIES_FILE);
            universeLaws.load(sf);
        } catch (Exception e) {
            Debug.alert(this,
                        "error with lawsoftheuniverse properties file: " + e);
        }
        int nbFound = 0;
        boolean stop = false;
        // vector for the strings we find in the configuration file
        Vector<String> found = new Vector<>();
        for (int i = 0; !stop; i++) {
            try {
                // add all worlds listed in the configuration file to 'found'
                found.addElement(universeLaws.getProperty( (Integer.valueOf(i + 1)).
                    toString(), "default"));
                if (! ( ( found.elementAt(i)).equals("default"))) {
                    nbFound++;
                    Debug.print(this, "found universe law: " + found.elementAt(i));
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
                Law law = (Law) Class.forName(found.elementAt(i)).getDeclaredConstructor()
                    .newInstance();

                law.setEnvironment(env);
                laws.addElement(law);
            } catch (Exception e) {
                Debug.alert(this, "Error setting laws of the universe" + e);
            }
        }
    }

    /**
     *  Sets the environment of this Reactor
     *
     * @param  environ  The new environment value
     */
    private void setEnvironment(Environment environ) {
        this.env = environ;
    }

    /**
     *  Processes a ToHandle.
     *  In this case we're talking about InfluenceSets. The reactor processes
     *  each Influence in the InfluenceSet one by one.
     *
     * @param   toBeHandled  the InfluenceSet that is to be processed.
     * @post    each Influence in toBeHandled is processed
     * @post    the clock is increased
     * @post    the sphere that sent this InfluenceSet gets notified of it's being processed.
     */
    protected void process(ToHandle toBeHandled) {
        Debug.print(this,
            "Reactor has received an InfluenceSet ------------------------------------");
        InfluenceSet influenceSet = null;
        try {
            influenceSet = (InfluenceSet) toBeHandled;
        } catch (ClassCastException e) {
            Debug.alert(this, "Wrong handler !!!");
        }

        for (int i = 0; i < influenceSet.getInfluenceSet().length; i++) {
            Influence inf = influenceSet.getInfluenceSet()[i];
            if (inf instanceof InfPickPacket) {
                boolean processed = false;
                for (int j = i; j < influenceSet.getInfluenceSet().length; j++) {
                    Influence inf2 = influenceSet.getInfluenceSet()[j];
                    if (inf2 instanceof InfPutPacket) {
                        Agent getter = env.getAgentWorld().getAgent(inf.getID());
                        Agent putter = env.getAgentWorld().getAgent(inf2.getID());
                        int fromX = inf.getX();
                        int fromY = inf.getY();
                        int toX = inf2.getX();
                        int toY = inf2.getY();
                        if (putter.getX() == fromX && putter.getY() == fromY &&
                            getter.getX() == toX && getter.getY() == toY) {
                            //A matching pair of InfPutPacket & InfPickPacket found
                            process(new InfPassPacket(inf.getEnvironment(),
                                inf2.getX(), inf2.getY(), inf2.getID()));

                            influenceSet.getInfluenceSet()[j] = new InfSkip(inf.getEnvironment(), inf2.getID()); //influence already dealt with
                            //Note: the influence cannot be null, use InfSkip(env) instead
                            processed = true;
                            break;
                        }
                    }
                }
                if (!processed) {
                    process( (influenceSet.getInfluenceSet())[i]);
                }
            } else if (inf instanceof InfPutPacket) {
                boolean processed = false;
                for (int j = i; j < influenceSet.getInfluenceSet().length; j++) {
                    Influence inf2 = influenceSet.getInfluenceSet()[j];
                    if (inf2 instanceof InfPickPacket) {
                        Agent getter = env.getAgentWorld().getAgent(inf2.getID());
                        Agent putter = env.getAgentWorld().getAgent(inf.getID());
                        int fromX = inf2.getX();
                        int fromY = inf2.getY();
                        int toX = inf.getX();
                        int toY = inf.getY();
                        if (putter.getX() == fromX && putter.getY() == fromY &&
                            getter.getX() == toX && getter.getY() == toY) {
                            //A matching pair of InfPutPacket & InfPickPacket found
                            process(new InfPassPacket(inf.getEnvironment(),
                                inf.getX(), inf.getY(), inf.getID()));

                            influenceSet.getInfluenceSet()[j] = new InfSkip(inf.getEnvironment(), inf2.getID()); //influence already dealt with
                            processed = true;
                            break;
                        }
                    }
                }
                if (!processed) {
                    process( (influenceSet.getInfluenceSet())[i]);
                }
            } else if (inf instanceof InfPassPacket) {
                Debug.alert(this, "Agents cannot use the InfPassPacket influence. " +
                            "They have to use the InfPickPacket and InfPickPacket influence instead.");
            } else if (inf != null) {
                process( (influenceSet.getInfluenceSet())[i]); // process each influence
            }
        }


        //End added


        Debug.print(this,
            "Reactor processed the InfluenceSet --------------------------------------");
        env.getClock().incrClock();
        update();
        if (running) {
            toBeHandled.getSendingSphere().setHandled(influenceSet.getNbCorrespOutcomes());
        }
    }

    /**
     *  Process a given Influence 'inf'.
     *  First 'inf' will be validated according to all known laws.
     *  If validation is successful it will be effectuated in the World it
     *  affects
     *
     * @param  inf  The influence that has to be processed.
     * @post    If validate(inf) returned true 'inf' is
     *          effectuated in it's area of effect.
     */
    private void process(Influence inf) {
        Debug.print(this,
                    "Reactor is processing " + inf.toString() + " from agent " +
                    inf.getID());
        boolean valid = validate(inf);
        try {
            if (valid) {
                inf.getAreaOfEffect().effectuate(inf);
            }
        } catch (NullPointerException exc) {
            System.err.println("No such world defined. Make sure you added the worlds needed for this behaviour.");
        }
    }

    /**
     *   Validate a given Influence 'inf'
     *   'inf' will be passed to all known laws, which are applied to it, when applicable
     *
     *   @param  inf The influence that has to be validated.
     *   @return 'true' if every applicable law applies successfully
     */
    private boolean validate(Influence inf) {
        for (int i = 0; i < laws.size(); i++) {
            if ( ( laws.elementAt(i)).applicable(inf)) {
                if (! ( ( laws.elementAt(i)).apply(inf))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     *	Repaints the GUI and updates any data that needs to be updated
     *	Puts the program thread to sleep for a period specified by the user
     *
     * @post	GUI is repainted
     */
    void update() {
        if (Setup.getInstance().guiOutput) {
            VideoPanel panel = VideoPanel.getInstance();
            panel.repaint();
        }

        EventManager.getInstance().throwEvent(new WorldProcessedEvent(this));
        if ( env.getWorld(PacketWorld.class).getNbPackets() == 0) {
            Setup.getInstance().stop();
            EventManager.getInstance().throwEvent(new GameOverEvent(this));
        }
        Setup.getInstance().checkSuspended();
    }

}
