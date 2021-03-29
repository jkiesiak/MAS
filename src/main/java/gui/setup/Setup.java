package gui.setup;

import agent.AgentImplementations;
import environment.ActiveItem;
import environment.Environment;
import environment.Item;
import environment.World;
import environment.world.agent.Agent;
import environment.world.agent.AgentWorld;
import environment.world.destination.Destination;
import environment.world.destination.DestinationWorld;
import environment.world.energystation.EnergyStation;
import environment.world.energystation.EnergyStationWorld;
import environment.world.packet.Packet;
import environment.world.packet.PacketWorld;
import environment.world.wall.Wall;
import environment.world.wall.WallWorld;
import gui.video.BehaviourWatch;
import gui.video.EventHistoryMonitor;
import gui.video.VideoFrame;
import gui.video.VideoPanel;
import synchronizer.CentralSynchronization;
import synchronizer.Synchronization;
import util.AsciiReader;
import util.Debug;
import util.Variables;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 *  This is the main class for the GUI application.
 *  It allows for the setup and initialisation of the application.
 */
@SuppressWarnings("FieldCanBeLocal")
public class Setup {

    //--------------------------------------------------------------------------
    //		CONSTRUCTOR
    //--------------------------------------------------------------------------

    /**
     *  Initializes a new Setup object
     */
    protected Setup() {}

    public static Setup getInstance() {
        if (instance == null) {
            instance = new Setup();
        }
        return instance;
    }

    /**
     *  Real 'Main' method for the Setup class
     *
     *  Giving the arguments quick at the commandline starts the
     *  application with 4 agents of the 'ReactiveFlag' kind, a central
     *  synchronization scheme, a world of size 24, 3 different kinds of
     *  packets with 6 packets per kind, and a visibility range of 8 for
     *  the agents.
     *
     *  When no arguments are given the user is allowed to choose these
     *  parameters for himself in a gui window.
     *
     * @param  args  The command line arguments
     */
    public void start(String[] args) {
        //if the user gave any known arguments
        if ( (args.length == 1) && (args[0].equals("quick"))) {
            setImplementation(DEFAULT_IMPLEMENTATION);
            setSyncMode(DEFAULT_SYNCMODE);
            setNbAgents(DEFAULT_NBAGENTS);
            setWorldWidth(DEFAULT_WORLDWIDTH);
            setWorldHeight(DEFAULT_WORLDHEIGHT);
            setView(DEFAULT_VIEW);
            setNbPacketKinds(DEFAULT_NBPACKETKINDS);
            setNbPacketsPerKind(DEFAULT_NBPACKETSPERKIND);
            make(true);
            startGui();
        } else if ( (args.length == 2) && (args[0].equals("file"))) {
            String file = args[1];
            try {
                AsciiReader reader = new AsciiReader(file);
                reader.check("environment");
                String envFile = reader.readNext();
                reader.check("implementation");
                String impl = reader.readNext();

                setImplementation(impl);
                setEnvFile(envFile);
                make(false);
                startGui();
            } catch (IOException e) {
                System.err.println("Error when reading file: " + file + ". " + e.getMessage());
            }
        } else { // if there were no arguments or unknown arguments
            selectSettings();
        }
    }

    public void start() {
        selectSettings();
    }

    //--------------------------------------------------------------------------
    //		INSPECTORS
    //--------------------------------------------------------------------------

    public boolean isCustom() {
        return custom;
    }

    public synchronized void checkSuspended() {
        try {
            Thread.sleep(playSpeed);
        } catch (InterruptedException ignored) {}
        try {
            synchronized (dummy) {
                while (paused() || (stepMode && steps <= 0)) {
                    dummy.wait();
                }
            }
        } catch (InterruptedException ignored) {}

        if (steps > 0) {
            steps--;
        }
    }

    //--------------------------------------------------------------------------
    //		MUTATORS
    //--------------------------------------------------------------------------

    public void play() {
        if (stopped) {
            return;
        }
        stepMode = false;
        pause = false;
        synchronized (dummy) {
            dummy.notify();
        }
    }

    public void stop() {
        setPaused();
        stopped = true;
    }

    public void reset() {
        stopped = false;
        pause = false;

        synchronized (dummy) {
            dummy.notify();
        }
        env.finish();
        ais.finish();
        env = null;
        ais = null;
        sync = null;
        aObjects.clear();
        make(isCustom());

        if (this.ehm != null) {
            this.ehm.reset();
        }
    }

    public boolean paused() {
        return pause;
    }

    public void setPaused() {
        this.pause = true;
    }

    public void step() {
        if (stopped) {
            return;
        }
        stepMode = true;
        pause = false;
        steps++;
        synchronized (dummy) {
            dummy.notify();
        }
    }

    /**
         *  Creates and shows a AgentSelectFrame for selecting the kind of agents to
     *  work with.
     */
    public void selectSettings() {
        JFrame WorldConfigurationFrame = new WorldConfigurationFrame(this);
        WorldConfigurationFrame.setVisible(true);
    }

    /**
     *  Initializes the core modules of the application
     *
     * @param isCustom  whether to create a custom world or not
     * @post    an Environment instance is created and the contents of the
     *          environment package are set up
     * @post    an Synchronization instance is created and the contents of the
     *          synchronization package are set up
     * @post    an AgentImplementations instance is created and the contents
     *          of the AgentImplementations package are set up
     * @post    A gui is built and shown containing the play grid and related
     *          items
     */
    public void make(boolean isCustom) {
        setCustom(isCustom);
        Environment env = null;
        try {
            if (isCustom) {
                env = new Environment(getWorldWidth(), getWorldHeight());
                createWorlds(getDefaultWorlds(), env, true);
            } else {
                env = createEnvFromFile(Variables.ENVIRONMENTS_PATH + getEnvFile() + ".txt");
            }
        } catch (Exception e) {
            Debug.alert(this, "Error during setup.make() " + e.getMessage());
        }
        if (env == null) {
            Debug.alert(this, "env is null !!");
        }
        setEnvironment(env);
        Synchronization sync = null;
        if (getSyncMode().equals("Central synchronization")) {
            sync = new CentralSynchronization();
        }

        if (sync == null) {
            Debug.alert(this, "sync is null !!");
        }
        setSynchronization(sync);
        AgentImplementations ais = new AgentImplementations();
        setAgentImplementations(ais);

        sync.setEnvironment(env);
        env.setAgentImplementations(ais);
        ais.setEnvironment(env);
        sync.setAgentImplementations(ais);
        ais.setSynchronizer(sync);
        env.createEnvironment();
        try {
            ais.createAgentImps(getNbAgents(), implementation);
            ais.createObjectImps(getActiveObjects());
            sync.createSynchroPackage();
        } catch (Exception e) {
            Debug.alert(this, "Error during setup.make() " + e.getMessage());
        }
    }

    /**
     * Instantiates the worlds.
     *
     * @param worldClasses the classes of the worlds to create
     * @param env          the environment
     * @param isCustom     whether to create a custom world or not
     */
    private void createWorlds(List<Class<? extends World<?>>> worldClasses, Environment env,
                              boolean isCustom) {
        for (Class<? extends World<?>> worldClass : worldClasses) {
            try {
                World<?> w = worldClass.getDeclaredConstructor().newInstance();
                w.initialize(getWorldWidth(), getWorldHeight(), env);

                env.addWorld(w);
                if (isCustom) {
                    if (worldClass == AgentWorld.class) {
                        ((AgentWorld) w).createWorld(getNbAgents(), getView());
                    } else if (worldClass == PacketWorld.class) {
                        ((PacketWorld) w).createWorld(getNbPacketKinds(), getNbPacketsPerKind());
                    } else if (worldClass == DestinationWorld.class) {
                        ((DestinationWorld) w).createWorld(getNbPacketKinds());
                    }
                }
                Debug.print(this, "Added to worlds: " + w);
            } catch (Exception e) {
                Debug.alert(this, "Error setting worlds: " + e);
            }
        }
    }

    /**
     * Creates an environment from a file.
     *
     * @param  configFile a textfile with a configuration of an environment
     * @return             a new environment created from the description in
     *                     the specified file
     */
    public Environment createEnvFromFile(String configFile) {
        try {
            AsciiReader reader = new AsciiReader(configFile);
            Debug.print(this, "Starting to read environment configfile");
            //Initiate Worlds

            reader.check("width");
            setWorldWidth(reader.readInt());
            reader.check("height");
            setWorldHeight(reader.readInt());

            Environment env = new Environment(getWorldWidth(), getWorldHeight());
            setEnvironment(env);

            createWorlds(getDefaultWorlds(), env, false);

            //Initiate Items

            //Agents

            reader.check("nbAgents");

            int nbAgents = reader.readInt();
            setNbAgents(nbAgents);
            Debug.print(this, "making " + nbAgents + " agent(s)");

            Collection<Agent> agents = new ArrayList<>();
            for (int i = 0; i < nbAgents; i++) {
                Agent agent = (Agent) reader.readClassConstructor();
                agent.setEnvironment(getEnvironment());
                agents.add(agent);
                Debug.print(this, "added agent to collection");
            }

            Debug.print(this, "agents OK");

            //Packets
            reader.check("nbPackets");
            int nbPackets = reader.readInt();

            Collection<Packet> packets = new ArrayList<>();
            for (int i = 0; i < nbPackets; i++) {
                packets.add((Packet) reader.readClassConstructor());
            }

            Debug.print(this, "packets OK");

            //Destinations
            reader.check("nbDestinations");
            int nbDest = reader.readInt();

            Collection<Destination> destinations = new ArrayList<>();
            for (int i = 0; i < nbDest; i++) {
                destinations.add((Destination) reader.readClassConstructor());
            }
            Debug.print(this, "destination OK");

            //Walls
            reader.check("nbWalls");
            int nbWalls = reader.readInt();

            Collection<Wall> walls = new ArrayList<>();
            for (int i = 0; i < nbWalls; i++) {
                walls.add((Wall) reader.readClassConstructor());
            }
            Debug.print(this, "walls OK");

            // EnergyStations
            reader.check("nbEnergyStations");
            int nbBatteries = reader.readInt();

            Collection<EnergyStation> batteries = new ArrayList<>();
            for (int i = 0; i < nbBatteries; i++) {
                EnergyStation energyStation = (EnergyStation) reader.readClassConstructor();
                addActiveObject(energyStation);
                batteries.add(energyStation);
            }
            Debug.print(this, "EnergyStations OK");

            // If no energy stations in the world -> do not keep agent's battery in mind during execution of actions
            if (nbBatteries == 0) {
                Agent.ENERGY_ENABLED = false;
            }


            env.getWorld(AgentWorld.class).placeItems(agents);
            env.getWorld(PacketWorld.class).placeItems(packets);
            env.getWorld(DestinationWorld.class).placeItems(destinations);
            env.getWorld(WallWorld.class).placeItems(walls);
            env.getWorld(EnergyStationWorld.class).placeItems(batteries);
            return env;
        } catch (FileNotFoundException e) {
            System.err.println("Environment config file not found: " +
                               configFile +
                               "\n" + e.getMessage());
            return null;
        } catch (IOException e) {
            System.err.println("Something went wrong while reading: " +
                               configFile +
                               "\n" + e.getMessage());
            return null;
        } catch (NullPointerException e) {
            System.err.println(
                "Something went wrong while creating the environment. " +
                "Probably not all kinds of items are defined" +
                "in the config file.");
            return null;
        } catch (Exception e) {
            System.err.println(
                "Something went wrong while initializing the environment: \n" +
                e.getMessage());
            return null;
        }
    }

    /**
     *  Start the gui for this application
     *
     * @post    A VideoFrame containing the grid and it's contents is created and shown.
     */
    public void startGui() {
        if (!videoStarted) {
            videoStarted = true;

            BehaviourWatch bw = new BehaviourWatch();
            bw.initialize();
            bw.setVisible(true);

            this.ehm = new EventHistoryMonitor();
            ehm.initialize();
            ehm.setVisible(true);

            VideoFrame video = new VideoFrame(getEnvironment(), getSpeed());
            setVideoPanel(video.getVideoPanel());
            video.setVisible(true);
        } else {
            getVideoPanel().setEnvironment(getEnvironment());
        }
    }

    /**
     *  Loads a list of properties from a file into an array
     *
     * @param filename the name of the properties file
     * @return an array with the loaded properties
     */
    public static String[] getPropertiesFromFile(String filename) {
        Properties prop = new Properties();
        try {
            FileInputStream sf = new FileInputStream(filename);
            prop.load(sf);
        } catch (Exception e) {
            System.err.println("Error with properties file " + filename + " : " + e);
        }
        String[] propStrings = new String[prop.size()];
        for (int i = 1; i <= propStrings.length; i++) {
            propStrings[i - 1] = prop.getProperty(String.valueOf(i));
        }
        return propStrings;
    }

    //--------------------------------------------------------------------------
    //		GETTERS & SETTERS
    //--------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public List<Class<? extends World<?>>> getDefaultWorlds() {
        String[] worldNames = getPropertiesFromFile(Variables.WORLD_PROPERTIES_FILE);
        List<Class<? extends World<?>>> result = new ArrayList<>();

        for (String name : worldNames) {
            try {
                result.add((Class<? extends World<?>>) Class.forName(name));
            } catch (ClassNotFoundException | ClassCastException e) {
                e.printStackTrace();
                throw new RuntimeException("Could not add world with name " + name);
            }
        }

        return result;
    }

    public String getImplementation() {
        return implementation;
    }

    public String getSyncMode() {
        return syncMode;
    }

    public int getNbPacketKinds() {
        return nbPacketKinds;
    }

    public int getNbAgents() {
        return nbAgents;
    }

    public int getNbPacketsPerKind() {
        return nbPacketsPerKind;
    }

    public Vector<Item<?>> getActiveObjects() {
        return aObjects;
    }

    public int getWorldHeight() {
        return worldHeight;
    }

    public int getWorldWidth() {
        return worldWidth;
    }

    public int getView() {
        return view;
    }

    public int getSpeed() {
        return playSpeed;
    }

    private String getEnvFile() {
        return envFile;
    }

    public Environment getEnvironment() {
        return env;
    }

    private Synchronization getSynchronization() {
        return sync;
    }

    public AgentImplementations getAgentImplementations() {
        return ais;
    }

    /**
     *  Sets the implementation of this Setup
     *
     * @param  s  The new implementation value
     */
    public void setImplementation(String s) {
        implementation = s;
    }

    /**
     *  Sets the syncMode of this Setup
     *
     * @param  s  The new syncMode value
     */
    public void setSyncMode(String s) {
        syncMode = s;
    }

    /**
     *  Sets the amount of different kinds of packets
     *
     * @param  nbKinds  The new nbPacketKinds value
     */
    public void setNbPacketKinds(int nbKinds) {
        nbPacketKinds = nbKinds;
    }

    /**
     *  Sets the number of agents
     *
     * @param  nbAgents  the new nbAgents value
     */
    public void setNbAgents(int nbAgents) {
        this.nbAgents = nbAgents;
    }

    /**
     *  Sets the amount of packets per kind of packet
     *
     * @param  nb  the new nbPacketsPerKind value
     */
    public void setNbPacketsPerKind(int nb) {
        nbPacketsPerKind = nb;
    }

    /**
     *  Add an active object to the environment
     *
     * @param aObject the newly created active object to add
     */
    public void addActiveObject(ActiveItem<?> aObject) {
        aObjects.add(aObject);
    }

    /**
     *  Sets the width of all worlds
     *
     * @param  width  The new worldWidth value
     */
    public void setWorldWidth(int width) {
        worldWidth = width;
    }

    /**
     *  Sets the height of all worlds
     *
     * @param  height  The new worldHeight value
     */
    public void setWorldHeight(int height) {
        worldHeight = height;
    }

    /**
     *  Sets the range of view for agents
     *
     * @param  v  The new view value
     */
    public void setView(int v) {
        view = v;
    }

    public void setEnvFile(String fileName) {
        envFile = fileName;
    }

    public void setSpeed(int s) {
        playSpeed = s;
    }

    public void setCustom(boolean cust) {
        this.custom = cust;
    }

    public void setEnvironment(Environment env) {
        this.env = env;
    }

    private void setSynchronization(Synchronization sync) {
        this.sync = sync;
    }

    private void setAgentImplementations(AgentImplementations ais) {
        this.ais = ais;
    }

    private VideoPanel getVideoPanel() {
        return videoPanel;
    }

    private void setVideoPanel(VideoPanel vPanel) {
        videoPanel = vPanel;
    }

    //--------------------------------------------------------------------------
    //		ATTRIBUTES
    //--------------------------------------------------------------------------

    /**
     * @label singleton
     */
    private static Setup instance = null;

    private final String DEFAULT_IMPLEMENTATION = "basicbehaviour2";
    private final String DEFAULT_SYNCMODE = "Central synchronization";
    private final int DEFAULT_NBAGENTS = 4;
    private final int DEFAULT_WORLDWIDTH = 24;
    private final int DEFAULT_WORLDHEIGHT = 24;
    private final int DEFAULT_VIEW = 8;
    private final int DEFAULT_NBPACKETKINDS = 3;
    private final int DEFAULT_NBPACKETSPERKIND = 6;

    private String implementation;
    private String envFile;
    private String syncMode = DEFAULT_SYNCMODE;
    private int worldWidth;
    private int worldHeight;
    private int nbPacketKinds;
    private int nbPacketsPerKind;
    private int nbAgents;
    private final Vector<Item<?>> aObjects = new Vector<>();
    private int view;
    private boolean custom;

    private Environment env = null;
    private Synchronization sync = null;
    private AgentImplementations ais = null;

    private final Object dummy = new Object();
    private int steps = 0;
    private boolean pause = true;
    private boolean stepMode = false;
    private boolean stopped = false;
    private int playSpeed = 1000;
    private boolean videoStarted = false;
    private VideoPanel videoPanel;
    public boolean guiOutput = true;

    private EventHistoryMonitor ehm = null;

}
