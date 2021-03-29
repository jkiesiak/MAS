package gui.video;

import environment.world.agent.Agent;
import environment.world.crumb.Crumb;
import environment.world.destination.Destination;
import environment.world.energystation.EnergyStation;
import environment.world.flag.Flag;
import environment.world.gradient.Gradient;
import environment.world.packet.Packet;
import environment.world.pheromone.DirPheromone;
import environment.world.pheromone.Pheromone;
import environment.world.wall.Wall;

import java.awt.*;

/**
 * Visitor for the drawing of items in the video panel.
 */

abstract public class Drawer {

    protected void init(Graphics g, int width, int height, int envWidth,
                        int envHeight, Color bgColor) {
        this.g = g;
        this.width = width;
        this.height = height;
        this.envWidth = envWidth;
        this.envHeight = envHeight;
        this.bgColor = bgColor;
    }

    /**
     * Clear the whole drawing area (on behalf of the grid).
     */
    abstract public void clear();

    /**
     * Clear the content of a cell on position (x, y).
     *
     * @param x  The row of the cell to clear
     * @param y  The position of the cell to clear
     */
    abstract public void clearItem(int x, int y);

    /**
     * Draw a grid.
     */
    abstract public void drawGrid();

    abstract public void drawAgent(Agent agent);

    abstract public void drawPacket(Packet packet);

    abstract public void drawDestination(Destination destination);

    abstract public void drawFlag(Flag flag);

    abstract public void drawPheromone(Pheromone pheromone);
    abstract public void drawDirPheromone(DirPheromone pheromone);

    abstract public void drawWall(Wall wall);

    abstract public void drawCrumb(Crumb crumb);

    abstract public void drawGradient(Gradient gradient);

    abstract public void drawEnergyStation(EnergyStation station);

    //--------------------------------------------------------------------------
    //		GETTERS & SETTERS
    //--------------------------------------------------------------------------

    public void setGraphics(Graphics g) {
        this.g = g;
    }

    public Graphics getGraphics() {
        return g;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    //--------------------------------------------------------------------------
    //		ATTRIBUTES
    //--------------------------------------------------------------------------

    protected Graphics g;

    /**
     * The width and height of the video panel to draw in.
     */
    protected int width, height;

    /**
     * The width and height of the environment.
     */
    protected int envWidth, envHeight;

    /**
     * The background color of the video panel.
     */
    protected Color bgColor;

}