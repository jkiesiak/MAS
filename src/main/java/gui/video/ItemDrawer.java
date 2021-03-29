package gui.video;

import environment.CellPerception;
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
import util.Debug;

import java.awt.*;

/**
 * Visitor for the drawing of items in the video panel.
 */

@SuppressWarnings("FieldCanBeLocal")
public class ItemDrawer extends Drawer {

    public void init(Graphics g, int width, int height, int envWidth,
                     int envHeight, Color bgColor) {
        super.init(g, width, height, envWidth, envHeight, bgColor);
        int fw = Math.min(getWidth(), getHeight()); //fw = FrameWidth
        int s = Math.max(this.envWidth, this.envHeight);
        w = fw - 2 * horizontalOffset;
        cellWidth = w / s;
    }

    public void clear() {
        for (int i = 0; i < envWidth; i++) {
            for (int j = 0; j < envHeight; j++) {
                clearItem(i, j);
            }
        }
    }

    public void clearItem(int i, int j) {
        g.setColor(bgColor);
        g.fillRect(i * cellWidth + horizontalOffset + 1, j * cellWidth + verticalOffset + 1, cellWidth - 2, cellWidth - 2);
    }

    public void drawGrid() {
        g.setColor(Color.black);
        for (int i = 0; i <= envHeight; i++) {
            g.drawLine(horizontalOffset, verticalOffset + cellWidth * i, horizontalOffset + envWidth * cellWidth, verticalOffset + cellWidth * i);
        }
        for (int j = 0; j <= envWidth; j++) {
            g.drawLine(horizontalOffset + cellWidth * j, verticalOffset, horizontalOffset + cellWidth * j, verticalOffset + envHeight * cellWidth);
        }
    }

    public void drawPacket(Packet packet) {
        int i = packet.getX();
        int j = packet.getY();
        Color pc = packet.getColor();

        g.setColor(pc);
        g.fillRect(i * cellWidth + horizontalOffset + cellWidth / 4, j * cellWidth + verticalOffset + cellWidth / 4, cellWidth / 2,
                   cellWidth / 2);
        Debug.print(this, "packet drawn");
    }

    public void drawAgent(Agent agent) {

        int i = agent.getX();
        int j = agent.getY();
        int hr = horizontalOffset + i * cellWidth;
        int vr = verticalOffset + j * cellWidth;

        Packet packet = agent.carry();
        if (packet != null) {
            Color pc = packet.getColor();
            g.setColor(pc);
            g.fillRect(hr + 8 * cellWidth / 15 + 1, vr + 7 * cellWidth / 15 + 1, 4 * cellWidth / 15 - 1, 4 * cellWidth / 15 - 1);
            g.setColor(Color.black);
            g.drawRect(hr + 8 * cellWidth / 15, vr + 7 * cellWidth / 15, 4 * cellWidth / 15, 4 * cellWidth / 15);
        }

        String aName = agent.getName();

        g.setColor(Color.blue);

        int ax = agent.getX();
        int ay = agent.getY();
        int v = agent.getView();
        int minX = horizontalOffset + cellWidth * Math.max(0, ax - v);
        int maxX = cellWidth + horizontalOffset + cellWidth * Math.min(envWidth - 1, ax + v);
        int minY = verticalOffset + cellWidth * Math.max(0, ay - v);
        int maxY = cellWidth + verticalOffset + cellWidth * Math.min(envHeight - 1, ay + v);
        g.setColor(Color.blue);

        Graphics2D g2d = (Graphics2D) g;
        var oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(2));

        g2d.drawLine(minX, minY, minX, maxY);
        g2d.drawLine(minX, minY, maxX, minY);
        g2d.drawLine(minX, maxY, maxX, maxY);
        g2d.drawLine(maxX, maxY, maxX, minY);

        g2d.setStroke(oldStroke);

        if (Agent.ENERGY_ENABLED) {
            //Agent changes color according to the state of its battery
            //Only possible during runtime environment, not in edit mode -
            //therefore the try-catch
            try {
                float battState = agent.getBatteryState();
                if (battState <= Agent.BATTERY_MIN) {
                    g.setColor(Color.black);
                } else {
                    float battDelta = Agent.BATTERY_MAX - Agent.BATTERY_MIN;
                    float hue = (battState / battDelta) * 0.34f;
                    g.setColor(Color.getHSBColor(hue, 1.0f, 0.8f));
                }
            } catch (Exception exc) {
                g.setColor(Color.black);
            }
        } else {
            //Normal mode
            g.setColor(Color.black);
        }


        //draw head of agent
	    g.fillOval(hr + 3 * cellWidth / 15, vr + cellWidth / 15, 4 * cellWidth / 15, 4 * cellWidth / 15);

        //draw body of agent
        g.drawRect(hr + 3 * cellWidth / 15, vr + 5 * cellWidth / 15, 4 * cellWidth / 15,
                   5 * cellWidth / 15);
        //fill body of agent                   
        g.fillRect(hr + 3 * cellWidth / 15, vr + 5 * cellWidth / 15, 4 * cellWidth / 15,
                   5 * cellWidth / 15);
 
        //draw right arm of agent
        Polygon ra = new Polygon();
        ra.addPoint(hr + 7 * cellWidth / 15, vr + 5625 * cellWidth / 15000);
        ra.addPoint(hr + 9375 * cellWidth / 15000, vr + 7 * cellWidth / 15);
        ra.addPoint(hr + 8625 * cellWidth / 15000, vr + 7 * cellWidth / 15);
        ra.addPoint(hr + 7 * cellWidth / 15, vr + 6375 * cellWidth / 15000);
        g.fillPolygon (ra);          
                   
        //draw left arm of agent
        Polygon la = new Polygon();
        la.addPoint(hr + 3 * cellWidth / 15, vr + 5625 * cellWidth / 15000);
        la.addPoint(hr + 8 * cellWidth / 15, vr + 8625 * cellWidth / 15000);
        la.addPoint(hr + 8 * cellWidth / 15, vr + 9375 * cellWidth / 15000);
        la.addPoint(hr + 3 * cellWidth / 15, vr + 6375 * cellWidth / 15000);
        g.fillPolygon (la);
                         
        //draw right leg of agent
	    g.drawRect(hr + 55 * cellWidth / 150, vr + 10 * cellWidth / 15, cellWidth / 15, 375 * cellWidth / 1500);
	    g.fillRect(hr + 55 * cellWidth / 150, vr + 10 * cellWidth / 15, cellWidth / 15, 375 * cellWidth / 1500);
	    
	    //draw right foot of agent
        g.drawRect(hr + 55 * cellWidth / 150, vr + 135 * cellWidth / 150, 2 * cellWidth / 15, 5 * cellWidth / 150);
	    g.fillRect(hr + 55 * cellWidth / 150, vr + 135 * cellWidth / 150, 2 * cellWidth / 15, 5 * cellWidth / 150);
                   
        //draw left leg of agent
        g.fillRect(hr + 35 * cellWidth / 150, vr + 10 * cellWidth / 15, cellWidth / 15, 375 * cellWidth / 1500);
        g.drawRect(hr + 35 * cellWidth / 150, vr + 10 * cellWidth / 15, cellWidth / 15, 375 * cellWidth / 1500);
        
        //draw left foot of agent
        g.drawRect(hr + 25 * cellWidth / 150, vr + 135 * cellWidth / 150, 2 * cellWidth / 15, 5 * cellWidth / 150);
        g.fillRect(hr + 25 * cellWidth / 150, vr + 135 * cellWidth / 150, 2 * cellWidth / 15, 5 * cellWidth / 150);
             
        //draw neck of agent
        g.fillRect(hr + 425 * cellWidth / 1500, vr + 45 * cellWidth / 150, 15 * cellWidth / 150, 5 * cellWidth / 150);
        g.drawRect(hr + 425 * cellWidth / 1500, vr + 45 * cellWidth / 150, 15 * cellWidth / 150, 5 * cellWidth / 150);
                
        //draw hair of agent
        if (hairyMode) {
            g.drawLine(hr + 5 * cellWidth / 15, vr + 3 * cellWidth / 15, hr + 3 * cellWidth / 15, vr + cellWidth / 15);
            g.drawLine(hr + 5 * cellWidth / 15, vr + 3 * cellWidth / 15, hr + 4 * cellWidth / 15, vr + 5 * cellWidth / 150);
            g.drawLine(hr + 5 * cellWidth / 15, vr + 3 * cellWidth / 15, hr + 5 * cellWidth / 15, vr + 25 * cellWidth / 1500);
            g.drawLine(hr + 5 * cellWidth / 15, vr + 3 * cellWidth / 15, hr + 6 * cellWidth / 15, vr + 5 * cellWidth / 150);
            g.drawLine(hr + 5 * cellWidth / 15, vr + 3 * cellWidth / 15, hr + 7 * cellWidth / 15, vr + cellWidth / 15);
        }

        g.setColor(agent.getColor().orElse(Color.black));
        int fontSize = cellWidth / 3;
        if (fontSize > 30) {
            fontSize = 30;
        } else if (fontSize < 8) {
            fontSize = 8;
        }

        Font font = new Font("Courier", Font.BOLD, fontSize);
        g.setFont(font);
        g.drawString(aName, hr + 9 * cellWidth / 15, vr + 6 * cellWidth / 15);

        Debug.print(this, "agent drawn");
    }

    public void drawDestination(Destination dest) {
        int i = dest.getX();
        int j = dest.getY();
        Color dc = dest.getColor();

        g.setColor(dc);
        g.fillOval(i * cellWidth + horizontalOffset + 1 + cellWidth / 8, j * cellWidth + verticalOffset + 1 + cellWidth / 8, 1 + 3 * cellWidth / 4, 1 + 3 * cellWidth / 4);
        Debug.print(this, "destination drawn");
    }

    public void drawFlag(Flag flag) {
        int i = flag.getX();
        int j = flag.getY();
        int hr = horizontalOffset + i * cellWidth;
        int vr = verticalOffset + j * cellWidth;
        Color dc = flag.getColor();
        g.setColor(dc);
        g.drawLine(hr + 6 * cellWidth / 15, vr + 2 * cellWidth / 15, hr + 6 * cellWidth / 15, vr + 14 * cellWidth / 15);
        g.fillRect(hr + 6 * cellWidth / 15, vr + 2 * cellWidth / 15, 4 * cellWidth / 10, 4 * cellWidth / 10);
    }

    public void drawPheromone(Pheromone pher) {
        int i = pher.getX();
        int j = pher.getY();
        int range = bgColor.getGreen();
        int plus = range * pher.getLifetime() / 2000;
        if (plus > 255) plus = 255;

        int hr = horizontalOffset + i * cellWidth;
        int vr = verticalOffset + j * cellWidth;
	    g.setColor(new Color(bgColor.getRed(), range - plus, bgColor.getBlue()));
 
        g.fillRect(hr + 1, vr + 1, cellWidth - 2, cellWidth - 2);
    }

    public void drawDirPheromone(DirPheromone pher) {
        int i = pher.getX();
        int j = pher.getY();
        int plus = 5 + pher.getLifetime() / 500;
        int newRed =   bgColor.getRed()   + plus / 2;
        if (newRed > 255) newRed = 255;
        int newGreen = bgColor.getGreen() - plus * 2;
        if (newGreen < 0) newGreen = 0;
        int newBlue =  bgColor.getBlue()  - plus * 2;
        if (newBlue < 0) newBlue = 0;

        int hr = horizontalOffset + i * cellWidth;
        int vr = verticalOffset + j * cellWidth;
        g.setColor(new Color(newRed, newGreen, newBlue));
        g.fillRect(hr + 1, vr + 1, cellWidth - 2, cellWidth - 2);

        CellPerception target = pher.getTarget();
        if (target != null) {
            int toI = target.getX();
            int toJ = target.getY();
            g.setColor(Color.black);
            g.drawLine(hr + cellWidth / 2, vr + cellWidth / 2, hr + cellWidth * (toI - i + 1) / 2,
                       vr + cellWidth * (toJ - j + 1) / 2);
        }
    }


    public void drawWall(Wall wall) {
        int i = wall.getX();
        int j = wall.getY();
        int hr = horizontalOffset + i * cellWidth;
        int vr = verticalOffset + j * cellWidth;
        g.setColor(new Color(200, 20, 0));
        g.fillRect(hr + 1, vr + 1, cellWidth - 1, cellWidth - 1);
        g.setColor(Color.white);
        g.drawLine(hr + 1, vr + cellWidth / 2, hr + cellWidth - 1, vr + cellWidth / 2);
        g.drawLine(hr + cellWidth / 3, vr + cellWidth / 2, hr + cellWidth / 3, vr + cellWidth - 1);
        g.drawLine(hr + cellWidth * 2 / 3, vr + 1, hr + cellWidth * 2 / 3, vr + cellWidth / 2);
    }

    @Override
    public void drawCrumb(Crumb crumb) {}

    @Override
    public void drawGradient(Gradient gradient) {
        int i = gradient.getX();
        int j = gradient.getY();
        int hr = horizontalOffset + i * cellWidth;
        int vr = verticalOffset + j * cellWidth;

        //int fontSize = 8;
        int fontSize = cellWidth / 3;
        if (fontSize > 24) {
            fontSize = 24;
        } else if (fontSize < 8) {
            fontSize = 8;
        }
        Font font = new Font("Courier", Font.PLAIN, fontSize);
        g.setFont(font);
        g.setColor(Color.black);
        g.drawString(String.valueOf(gradient.getValue()), hr + cellWidth / 15, vr + 13 * cellWidth / 15);
    }

    public void drawEnergyStation(EnergyStation station) {
        int i = station.getX();
        int j = station.getY();
        g.setColor(Color.yellow);
        g.fillRect(i * cellWidth + horizontalOffset + cellWidth / 4, j * cellWidth + verticalOffset + cellWidth / 6, cellWidth / 2 - 1, cellWidth / 6);
        g.setColor(Color.black);
        g.drawRect(i * cellWidth + horizontalOffset + cellWidth / 4, j * cellWidth + verticalOffset + cellWidth / 6, cellWidth / 2 - 1, cellWidth / 6);
        g.fillRect(i * cellWidth + horizontalOffset + cellWidth / 4, j * cellWidth + verticalOffset + cellWidth / 3, cellWidth / 2, cellWidth / 2);
        g.fillRect(i * cellWidth + horizontalOffset + cellWidth * 2 / 5, j * cellWidth + verticalOffset + cellWidth / 8, cellWidth / 5, cellWidth / 24 + 1);
        Debug.print(this, "energy station drawn");
    }

    //--------------------------------------------------------------------------
    //		ATTRIBUTES
    //--------------------------------------------------------------------------

    /**
     * The horizontal offset for the drawing area.
     */
    private final int horizontalOffset = 20;

    /**
     * The vertical offset for the drawing area.
     */
    private final int verticalOffset = 20;

    /**
     * The width & height for the drawing area.
     */
    private int w;

    /**
     * The width & height of a cell in the drawing area.
     */
    private int cellWidth;

    /**
     * Some color definitions
     */
    private final Color DARKGREEN = new Color(96, 160, 96);
    
    /**
     *draw hair on the agent's head
     */
    private final boolean hairyMode = true;

}
