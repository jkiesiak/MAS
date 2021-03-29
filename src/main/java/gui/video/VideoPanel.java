package gui.video;

import environment.Environment;
import environment.Item;
import environment.World;
import environment.world.agent.Agent;
import environment.world.destination.Destination;
import environment.world.energystation.EnergyStationWorld;
import environment.world.packet.Packet;
import environment.world.pheromone.PheromoneWorld;
import gui.setup.Setup;
import util.Debug;
import util.event.Event;
import util.event.EventManager;
import util.event.Listener;
import util.event.WorldProcessedEvent;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.List;


/**
 * Panel for the videopanel containing the grid of the world.
 */
public class VideoPanel extends JPanel implements Listener {

    //--------------------------------------------------------------------------
    //		CONSTRUCTOR
    //--------------------------------------------------------------------------

    protected VideoPanel() {
        EventManager.getInstance().addListener(this, WorldProcessedEvent.class);
        drawer = new ItemDrawer();
    }

    public static VideoPanel getInstance() {
        if (instance == null) {
            instance = new VideoPanel();
        }
        return instance;
    }

    //--------------------------------------------------------------------------
    //		INSPECTORS
    //--------------------------------------------------------------------------

    //--------------------------------------------------------------------------
    //		MUTATORS
    //--------------------------------------------------------------------------

    public static void warning(String message) {
        JOptionPane.showMessageDialog(getInstance(), message);
    }


    public void startVideo() {
        getEnvironment().getAgentImplementations().startAllAgentImps();
        Setup.getInstance().setPaused();
    }


    public void playVideo() {
        Setup.getInstance().play();
    }

    public void stepVideo() {
        Setup.getInstance().step();

    }

    public void pauseVideo() {
        Setup.getInstance().setPaused();
    }

    public void restartVideo() {
        Setup.getInstance().reset();
        Setup.getInstance().startGui();
        UserPanel.getInstance().reset();

        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (started) {
            getDrawer().init(g, getWidth(), getHeight(),
                             getEnvironment().getWidth(),
                             getEnvironment().getHeight(), getBackground());

            getDrawer().drawGrid();
            getDrawer().clear();

            try {
                //Draw Pheromones (those which fill the whole area),
                Environment env = getEnvironment();

                drawItems(env.getWorld(PheromoneWorld.class).getItemsCopied());
                drawItems(env.getWorld(EnergyStationWorld.class).getItemsCopied());

                Collection<World<?>> worlds = env.getWorlds();
                for (World<?> world : worlds) {
                    if (!(world instanceof PheromoneWorld ||
                            world instanceof EnergyStationWorld)) {
                        drawItems(world.getItemsCopied());
                    }
                }
            } catch (NullPointerException exc) {
                Debug.alert(this, "Error while painting the components. " +
                            "Probably one of the needed worlds is not defined.\n");
            }
        }
    }

    public void initiate() {
        started = true;
        repaint();
    }

    private <T extends Item<?>> void drawItems(List<List<T>> items) {
        for (int i = 0; i < getEnvironment().getWidth(); i++) {
            for (int j = 0; j < getEnvironment().getHeight(); j++) {
                if (items.get(i).get(j) != null) {
                    items.get(i).get(j).draw(getDrawer());
                }
            }
        }
    }

      private void redrawAgent(Agent agent) {
          int ax = agent.getX();
          int ay = agent.getY();
          getDrawer().clearItem(ax, ay);
          agent.draw(getDrawer());
      }

    public void catchEvent(Event e) {
        if (e instanceof WorldProcessedEvent) {
            repaint();
        }
    }

    public void putPacket(int toX, int toY, Item<?> oldTo, Packet packet, Agent agent) {
        if (! (oldTo instanceof Destination)) {
            packet.draw(getDrawer());
        }
        redrawAgent(agent);
    }

    public void pickPacket(int fromX, int fromY, Packet packet, Agent agent) {
        Graphics g = getGraphics();
        redrawAgent(agent);
    }

    public void moveAgent(int fromX, int fromY, int toX, int toY, Agent agent) {
        getDrawer().clearItem(fromX, fromY);
        agent.draw(getDrawer());
    }

    public void refresh() {
        repaint();
    }

    public void step(int fx, int fy, int tx, int ty, Agent agent) {
        repaint();
    }

    public void put(int tx, int ty, Packet packet, Agent agent) {
        repaint();
    }

    public void pick(int px, int py, Packet packet, Agent agent) {
        repaint();
    }

    public void step() {
        repaint();
    }

    public void put() {
        repaint();
    }

    public void pick() {
        repaint();
    }


    //--------------------------------------------------------------------------
    //		GETTERS & SETTERS
    //--------------------------------------------------------------------------

    public void setFrame(JFrame frame) {
        getInstance().frame = frame;
    }

    public void setUserPanel(UserPanel userPanel) {
        getInstance().userPanel = userPanel;
        getInstance().setPlaySpeed(userPanel.speed);
    }

    public UserPanel getUserPanel() {
        return getInstance().userPanel;
    }

    public void setEnvironment(Environment environ) {
        this.env = environ;
    }

    public Environment getEnvironment() {
        return env;
    }

    public void setPlaySpeed(int speed) {
        playSpeed = speed;
        Setup.getInstance().setSpeed(speed);
    }

    public int getPlaySpeed() {
        return playSpeed;
    }

    public static boolean isRunning() {
        return running;
    }

    private ItemDrawer getDrawer() {
        return drawer;
    }

    //--------------------------------------------------------------------------
    //		ATTRIBUTES
    //--------------------------------------------------------------------------

    private JFrame frame;
    private UserPanel userPanel;
    private Environment env;
    boolean started;
    public static final boolean running = false;
    public static int playSpeed;

    /**
     * Visitor for the drawing of items.
     */
    private final ItemDrawer drawer;

    /**
     * @label singleton
     */
    public static VideoPanel instance = new VideoPanel();

}

