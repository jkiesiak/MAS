package gui.editor;

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
import gui.setup.Setup;
import gui.video.VideoPanel;
import util.MyColor;
import util.AsciiReader;
import util.TxtFileFilter;
import util.Variables;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.Vector;

/**
 *  A graphical editor for editing environment files.
 */
public class EnvironmentBuilder extends JFrame {

    public static void main(String[] arg) {
        EnvironmentBuilder builder = new EnvironmentBuilder();
        builder.init();
        builder.setVisible(true);
    }

    /**
     * Initializes the application. Calls initialize() and does some more init
     */
    public void init() {
        VideoPanel.getInstance().setFrame(this);
        panel1 = videop;
        initialize();
        jComboBoxColor.addItem("blue");
        jComboBoxColor.addItem("red");
        jComboBoxColor.addItem("yellow");
        jComboBoxColor.addItem("green");
        jComboBoxColor.addItem("pink");
        for (int i = 4; i <= MAXSIZE; i++) {
            jComboBoxWidth.addItem("" + i);
            jComboBoxHeight.addItem("" + i);
        }
        for (int i = 1; i <= MAXVIEW; i++) {
            jComboBoxView.addItem("" + i);
        }
        jComboBoxWidth.addActionListener(this::sizeActionPerformed);
        jComboBoxHeight.addActionListener(this::sizeActionPerformed);
        jComboBoxView.addActionListener(this::sizeActionPerformed);
        jComboBoxColor.addActionListener(this::colorActionPerformed);
        newFile();
    }

    /**
     * Creates all components
     */
    protected void initialize() {

        //getContentPane().add(panel3, java.awt.BorderLayout.SOUTH);
        getContentPane().add(panel2, java.awt.BorderLayout.NORTH);
        getContentPane().add(panel1, java.awt.BorderLayout.CENTER);
        panel2.setLayout(new java.awt.GridBagLayout());
        panel2.setSize(new java.awt.Dimension(481, 40));
        panel2.add(lblWidth,
                   new java.awt.GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            java.awt.GridBagConstraints.CENTER,
            java.awt.GridBagConstraints.NONE,
            new java.awt.Insets(0, 0, 0, 0), 0, 0));
        panel2.add(jComboBoxWidth,
                   new java.awt.GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            java.awt.GridBagConstraints.CENTER,
            java.awt.GridBagConstraints.NONE,
            new java.awt.Insets(0, 0, 0, 0), 0, 0));
        panel2.add(lblHeight,
                   new java.awt.GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
            java.awt.GridBagConstraints.CENTER,
            java.awt.GridBagConstraints.NONE,
            new java.awt.Insets(0, 0, 0, 0), 0, 0));
        panel2.add(jComboBoxHeight,
                   new java.awt.GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
            java.awt.GridBagConstraints.CENTER,
            java.awt.GridBagConstraints.NONE,
            new java.awt.Insets(0, 0, 0, 0), 0, 0));
        panel2.add(lblView,
                   new java.awt.GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
            java.awt.GridBagConstraints.CENTER,
            java.awt.GridBagConstraints.NONE,
            new java.awt.Insets(0, 0, 0, 0), 0, 0));
        panel2.add(jComboBoxView,
                   new java.awt.GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
            java.awt.GridBagConstraints.CENTER,
            java.awt.GridBagConstraints.NONE,
            new java.awt.Insets(0, 0, 0, 0), 0, 0));
        panel2.add(lblColor,
                   new java.awt.GridBagConstraints(6, 0, 1, 1, 0.0, 0.0,
            java.awt.GridBagConstraints.CENTER,
            java.awt.GridBagConstraints.NONE,
            new java.awt.Insets(0, 0, 0, 0), 0, 0));
        panel2.add(jComboBoxColor,
                   new java.awt.GridBagConstraints(7, 0, 1, 1, 0.0, 0.0,
            java.awt.GridBagConstraints.CENTER,
            java.awt.GridBagConstraints.NONE,
            new java.awt.Insets(0, 0, 0, 0), 0, 0));
        panel2.add(btnPacket,
                   new java.awt.GridBagConstraints(8, 0, 1, 1, 0.0, 0.0,
            java.awt.GridBagConstraints.CENTER,
            java.awt.GridBagConstraints.NONE,
            new java.awt.Insets(0, 0, 0, 0), 0, 0));
        panel2.add(btnDest,
                   new java.awt.GridBagConstraints(9, 0, 1, 1, 0.0, 0.0,
            java.awt.GridBagConstraints.CENTER,
            java.awt.GridBagConstraints.NONE,
            new java.awt.Insets(0, 0, 0, 0), 0, 0));
        panel2.add(btnAgent,
                   new java.awt.GridBagConstraints(10, 0, 1, 1, 0.0, 0.0,
            java.awt.GridBagConstraints.CENTER,
            java.awt.GridBagConstraints.NONE,
            new java.awt.Insets(0, 0, 0, 0), 0, 0));
        panel2.add(btnBattery,
                   new java.awt.GridBagConstraints(11, 0, 1, 1, 0.0, 0.0,
            java.awt.GridBagConstraints.CENTER,
            java.awt.GridBagConstraints.NONE,
            new java.awt.Insets(0, 0, 0, 0), 0, 0));
        panel2.add(btnWall,
                   new java.awt.GridBagConstraints(12, 0, 1, 1, 0.0, 0.0,
            java.awt.GridBagConstraints.CENTER,
            java.awt.GridBagConstraints.NONE,
            new java.awt.Insets(0, 0, 0, 0), 0, 0));
        panel2.add(btnRemove,
                   new java.awt.GridBagConstraints(13, 0, 1, 1, 0.0, 0.0,
            java.awt.GridBagConstraints.CENTER,
            java.awt.GridBagConstraints.NONE,
            new java.awt.Insets(0, 0, 0, 0), 0, 0));
        lblColor.setText(" Color");
        jComboBoxColor.setSize(new java.awt.Dimension(60, 21));
        jComboBoxColor.setEnabled(false);
        // btnPacket.setLabel("Packet");
        btnPacket.setText("Packet");
        btnPacket.addActionListener(this::packetButtonActionPerformed);
        // btnDest.setLabel("Destination");
        btnDest.setText("Destination");
        btnDest.setActionCommand("Destination");
        btnDest.addActionListener(this::destButtonActionPerformed);
        // btnAgent.setLabel("Agent");
        btnAgent.setText("Agent");
        btnAgent.addActionListener(this::agentButtonActionPerformed);
        // btnBattery.setLabel("Battery");
        btnBattery.setText("Battery");
        btnBattery.addActionListener(this::batteryButtonActionPerformed);
        // btnWall.setLabel("Wall");
        btnWall.setText("Wall");
        btnWall.addActionListener(this::wallButtonActionPerformed);

        setResizable(true);
        setDefaultCloseOperation(javax.swing.JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exitActionPerformed(e);
            }
        });

        setTitle(WINDOW_TITLE);
        setSize(new java.awt.Dimension(600, 600));
        //setBounds(new java.awt.Rectangle(0, 0, 731, 487));
        setBounds(new java.awt.Rectangle(0, 0, 900, 700));
        lblWidth.setText("Width ");
        lblWidth.setToolTipText("");
        lblHeight.setText(" Height ");
        lblView.setText(" View ");
        btnRemove.setText("Remove");
        btnRemove.setToolTipText("");
        btnRemove.addActionListener(this::removeButtonActionPerformed);
        jComboBoxWidth.setPreferredSize(new java.awt.Dimension(50, 25));
        jComboBoxHeight.setPreferredSize(new java.awt.Dimension(50, 25));

        jComboBoxView.setMinimumSize(new java.awt.Dimension(50, 25));
        jComboBoxView.setSize(new java.awt.Dimension(30, 25));
        jComboBoxView.setPreferredSize(new java.awt.Dimension(50, 25));
        panel1.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                panelMouseClicked(e);
            }
        });
        /*
        button4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SaveActionPerformed(e);
            }
        });
        */

        //Menubar
        setJMenuBar(menuBar);

        //Menu File
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.getAccessibleContext().setAccessibleDescription(
                "Opens the file menu");
        menuBar.add(fileMenu);

        JMenuItem menuItem;
        //File > New
        menuItem = new JMenuItem("New");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Creates a new environment file");
        menuItem.addActionListener(this::newActionPerformed);
        fileMenu.add(menuItem);
        //File > Load
        menuItem = new JMenuItem("Open...");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Loads an environment file");
        menuItem.addActionListener(this::loadActionPerformed);
        fileMenu.add(menuItem);
        //File > Save
        menuItem = new JMenuItem("Save");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Saves the active environment file");
        menuItem.addActionListener(this::saveActionPerformed);
        fileMenu.add(menuItem);
        //File > Save as
        menuItem = new JMenuItem("Save as...");
        //menuItem.setAccelerator(KeyStroke.getKeyStroke(
        //        KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Saves the active environment file with a new name");
        menuItem.addActionListener(this::saveAsActionPerformed);
        fileMenu.add(menuItem);

        fileMenu.addSeparator();
        //File > Exit
        menuItem = new JMenuItem("Exit");
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Quits the editor");
        menuItem.addActionListener(this::exitActionPerformed);
        fileMenu.add(menuItem);


    }

    /**
     * Routine for pressing packet button
     */
    public void packetButtonActionPerformed(ActionEvent e) {
        buttonClear();
        btnPacket.setBackground(selectedColor);
        jComboBoxColor.setEnabled(true);
        action = 1;
    }

    /**
     * Routine for pressing destination button
     */
    public void destButtonActionPerformed(ActionEvent e) {
        buttonClear();
        btnDest.setBackground(selectedColor);
        jComboBoxColor.setEnabled(true);
        action = 2;
    }

    /**
     * Routine for pressing agent button
     */
    public void agentButtonActionPerformed(ActionEvent e) {
        buttonClear();
        btnAgent.setBackground(DEFAULT_COLOR);
        action = 3;
    }

    /**
     * Routine for pressing battery button
     */
    public void batteryButtonActionPerformed(ActionEvent e) {
        buttonClear();
        btnBattery.setBackground(DEFAULT_COLOR);
        action = 4;
    }

    public void wallButtonActionPerformed(ActionEvent e) {
        buttonClear();
        btnWall.setBackground(DEFAULT_COLOR);
        action = 5;
    }

    /**
     * Routine for pressing remove button
     */
    public void removeButtonActionPerformed(ActionEvent e) {
        buttonClear();
        btnRemove.setBackground(DEFAULT_COLOR);
        action = 6;
    }

    /**
     * resets a buttons
     */
    private void buttonClear() {
        jComboBoxColor.setEnabled(false);
        btnPacket.setBackground(null);
        btnDest.setBackground(null);
        btnAgent.setBackground(null);
        btnRemove.setBackground(null);
        btnBattery.setBackground(null);
        btnWall.setBackground(null);
    }

    /**
     * Routine for adjusting size combo
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void sizeActionPerformed(ActionEvent e) {
        int width = Integer.parseInt( (String) jComboBoxWidth.getSelectedItem());
        int height = Integer.parseInt( (String) jComboBoxHeight.getSelectedItem());
        int newView = Integer.parseInt( (String) jComboBoxView.getSelectedItem());
        setView(newView);
        Environment newenv = new Environment(width, height);

        for (Class<? extends World<?>> worldClass : WORLDS) {
            try {
                World<?> w = worldClass.getDeclaredConstructor().newInstance();
                w.initialize(width, height, newenv);

                newenv.addWorld(w);
            } catch (Exception exc) {
                System.err.println("Error setting worlds" + exc);
            }
        }
        newenv.createEnvironment();

        if (getEnvironment() != null) {
            for (Class<? extends World<?>> worldClass : WORLDS) {
                World oldWorld = env.getWorld(worldClass);
                World newWorld = newenv.getWorld(worldClass);

                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        if (i < getEnvironment().getWidth() && j < getEnvironment().getHeight()) {
                            if (oldWorld.getItem(i, j) != null) {
                                newWorld.placeItem(oldWorld.getItem(i, j));
                                if (oldWorld.getItem(i, j) instanceof Agent) {
                                    ((Agent) newWorld.getItem(i, j)).setEnvironment(newenv);
                                    ((Agent) newWorld.getItem(i, j)).setView(newView);
                                }
                            }
                        }
                    }
                }
            }
        }
        changed = true;
        setEnvironment(newenv);

        videop.setEnvironment(getEnvironment());
        videop.initiate();
    }

    /**
     * Routine for adjusting color combo
     */
    @SuppressWarnings("UnnecessaryReturnStatement")
    public void colorActionPerformed(ActionEvent e) {
        selectedColor = MyColor.getColor((String) jComboBoxColor.getSelectedItem());
        switch (action) {
            case 0:
                return;
            case 1:
                btnPacket.setBackground(selectedColor);
                break;
            case 2:
                btnDest.setBackground(selectedColor);
                break;
            //case 3:
            //    btnAgent.setBackground(selectedColor);
            //    break;
            //case 4:
            //    btnBattery.setBackground(selectedColor);
            //    break;
            //case 5:
            //    btnWall.setBackground(selectedColor);
            //    break;
            //case 6:
            //    btnRemove.setBackground(selectedColor);
            //    break;
            default:
                return;
        }
    }

    /**
     * routine for clicking in the environment area
     */
    @SuppressWarnings("DuplicateBranchesInSwitch")
    public void panelMouseClicked(MouseEvent e) {
        int currentAction = action;

        //If rightclick, action = remove
        //Only works with java version >= 1.4
        //if (e.getButton() == 3) {
        //    currentAction = 6;
        //}


        int x = e.getPoint().x;
        int y = e.getPoint().y;
        int fw = Math.min(panel1.getWidth(), panel1.getHeight());
        int s = Math.max(getEnvironment().getWidth(),
                         getEnvironment().getHeight());
        int o = 20;
        int w = fw - 2 * o;
        int cs = w / s;
        int i = (x - o) / cs;
        int j = (y - o) / cs;
        if (i >= getEnvironment().getWidth() || j >= getEnvironment().getHeight()) {
            //Clicked outside grid
            //Could cause NullPointerException
            //No-op
            return;
        }
        //System.out.println("cs = "+cs+" o = "+o+" w = "+w);
        //System.out.println("("+x+","+y+") x = "+i+" y= "+j);
        if (getEnvironment().getWorld(AgentWorld.class).getItem(i, j) != null) {
            removeAgent(i, j);
        }
        switch (currentAction) {
            case 0:
                return;
            case 1:
                Packet p = new Packet(i, j, (String) jComboBoxColor.getSelectedItem());
                getEnvironment().free(i, j);
                getEnvironment().getWorld(PacketWorld.class).placeItem(p);
                changed = true;
                break;
            case 2:
                Destination d = new Destination(i, j, (String) jComboBoxColor.getSelectedItem());
                getEnvironment().free(i, j);
                getEnvironment().getWorld(DestinationWorld.class).placeItem(d);
                changed = true;
                break;
            case 3:
                Agent a = new Agent(i, j, getEnvironment(), getView(), agents.size() + 1,
                                     "" + (agents.size() + 1));
                agents.addElement(a);
                getEnvironment().free(i, j);
                getEnvironment().getWorld(AgentWorld.class).placeItem(a);
                changed = true;
                break;
            case 4:
                EnergyStation es = new EnergyStation(i, j, 0);
                getEnvironment().free(i, j);
                getEnvironment().getWorld(EnergyStationWorld.class).placeItem(es);
                changed = true;
                break;
            case 5:
                Wall wa = new Wall(i, j);
                getEnvironment().free(i, j);
                getEnvironment().getWorld(WallWorld.class).placeItem(wa);
                changed = true;
                break;
            case 6:
                getEnvironment().free(i, j);
                changed = true;
                break;
            default:
                return;
        }
        videop.refresh();
    }

    /**
     * Removes an agent from location (i,j). All agents with ID greater than
     * the removed agent are replaced by an agent with an ID that is 1 lower.
     */
    private void removeAgent(int i, int j) {
        Vector<Agent> v = new Vector<>();
        for (int k = 0; k < agents.size(); k++) {
            if (! ( agents.elementAt(k).getX() == i &&
                    agents.elementAt(k).getY() == j)) {
                v.addElement(new Agent( agents.elementAt(k).getX(),
                                        agents.elementAt(k).getY(),
                                        getEnvironment(), getView(),
                                        v.size() + 1, "" + (v.size() + 1)));
            }
        }
        agents = v;
        for (int k = 0; k < agents.size(); k++) {
            getEnvironment().getWorld(AgentWorld.class).placeItem(
                    agents.elementAt(k));
        }
    }

    private Object[] searchAll(World<? extends Item<?>> world) {
        Vector<Item<?>> v = new Vector<>();
        List<? extends List<? extends Item<?>>> items = world.getItems();
        for (List<? extends Item<?>> item : items) {
            for (Item<?> value : item) {
                if (value != null) {
                    v.addElement(value);
                }
            }
        }
        return v.toArray();
    }

    public void newActionPerformed(ActionEvent e) {
        if (changed) {
            int sel = JOptionPane.showConfirmDialog(this,
                "Environment has been modified. Do you want to save changes?",
                "Save modified file?",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE);
            if (sel == JOptionPane.YES_OPTION) {
                saveDialog();
                newFile();
            } else if (sel == JOptionPane.NO_OPTION) {
                newFile();
            }
            //else: CANCEL_OPTION --> No-op
        }
        newFile();
    }

    public void loadActionPerformed(ActionEvent e) {
        if (changed) {
            int sel = JOptionPane.showConfirmDialog(this,
                "Environment has been modified. Do you want to save changes?",
                "Save modified file?",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE);
            if (sel == JOptionPane.YES_OPTION) {
                saveDialog();
                if (loadDialog()) changed = false;
            } else if (sel == JOptionPane.NO_OPTION) {
                if (loadDialog()) changed = false;
            }
            //else: CANCEL_OPTION --> No-op
        } else {
            if (loadDialog()) changed = false;
        }
    }

    /**
     * Routine for pressing save button
     */
    public void saveActionPerformed(ActionEvent e) {
        if (changed) {
            if (curFile != null) {
                if (writeFile(curFile)) {
                    changed = false;
                } else {
                    VideoPanel.warning("Could not write to output file.");
                }
            } else {
                if (saveDialog()) {
                    changed = false;
                }
            }
        }
    }

    public void saveAsActionPerformed(ActionEvent e) {
        if (saveDialog()) {
            changed = false;
        }
    }

    public void exitActionPerformed(java.awt.AWTEvent e) {
        if (changed) {
            int sel = JOptionPane.showConfirmDialog(this,
                "Environment has been modified. Do you want to save changes?",
                "Save modified file?",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE);
            if (sel == JOptionPane.YES_OPTION) {
                if (saveDialog()) {
                    System.exit(0);
                }
            } else if (sel == JOptionPane.NO_OPTION) {
                System.exit(0);
            }
            //Else: cancel --> No-op
        } else {
            System.exit(0);
        }
    }

    /**
     * Shows a save file dialog. Returns <code>true</code> if the loading has
     * succeeded, <code>false</code> otherwise.
     */
    public boolean loadDialog() {
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(TxtFileFilter.getInstance());
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setCurrentDirectory(new File(Variables.ENVIRONMENTS_PATH));
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (file != null) {
                readFile(file);
                setCurrentFile(file);
                return true;
            }
        }
        return false;
    }

    /**
     * Shows a save file dialog. Returns <code>true</code> if the saving has
     * succeeded, <code>false</code> otherwise.
     */
    public boolean saveDialog() {
        boolean valid = false;
        File file = new File("");
        while (!valid) {
            fileChooser = new JFileChooser();
            fileChooser.setFileFilter(TxtFileFilter.getInstance());
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setCurrentDirectory(new File(Variables.ENVIRONMENTS_PATH));
            int returnVal = fileChooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
                if (!file.getAbsolutePath().endsWith(".txt")) {
                    file = new File(file.getAbsolutePath() + ".txt");
                }
                if (file.isFile()) {
                    int sel = JOptionPane.showConfirmDialog(this,
                        "File already exists! Do you want to overwrite it?",
                        "Overwrite file?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                    if (sel == JOptionPane.YES_OPTION) {
                        valid = true;
                    }
                } else {
                    valid = true;
                }
            } else {
                return false;
            }
        }
        if (!writeFile(file)) {
            VideoPanel.warning("Could not write to output file.");
            return false;
        } else {
            setCurrentFile(file);
            return true;
        }
    }

    private void newFile() {
        setEnvironment(null);
        sizeActionPerformed(null);
        action = 0;
        agents = new Vector<>();
        changed = false;
    }

    /**
     * @see   @link{guisetup.Setup#createEnvFromFile(String)}
     * @param file
     */
    private void readFile(File file) {
        String configFile = file.getAbsolutePath();

        //Sets the values of the width and height combo boxes
        try {
            AsciiReader reader = new AsciiReader(configFile);
            reader.check("width");
            int worldWidth = reader.readInt();
            reader.check("height");
            int worldHeight = reader.readInt();
            //reader.check("agentview");

            jComboBoxWidth.setSelectedItem(String.valueOf(worldWidth));
            jComboBoxHeight.setSelectedItem(String.valueOf(worldHeight));

            setEnvironment(Setup.getInstance().createEnvFromFile(configFile));
            List<Agent> agents = getEnvironment().getAgentWorld().getAgents();
            this.agents = new Vector<>();
            for (Agent agent : agents) {
                this.agents.addElement(agent);
            }
            try {
                jComboBoxView.setSelectedItem(String.valueOf(getEnvironment().getAgentWorld().getAgent(1).getView()));
            } catch (NullPointerException exc) {
                jComboBoxView.setSelectedIndex(0);
            }
            videop.setEnvironment(getEnvironment());
            videop.initiate();
            changed = false;
        } catch (FileNotFoundException e) {
            System.err.println("Environment config file not found: " +
                    configFile + "\n" + e.getMessage());
        } catch (IOException e) {
            System.err.println("Something went wrong while reading: " +
                    configFile + "\n" + e.getMessage());
        }
    }

    /**
     * writes configuration file
     */
    private boolean writeFile(File file) {
        String configFile = file.getAbsolutePath();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
            writer.write("width " + getEnvironment().getWidth() + "\n");
            writer.write("height " + getEnvironment().getHeight() + "\n");

            writer.write("\n");
            Object[] items = searchAll(getEnvironment().getWorld(AgentWorld.class));
            writer.write("nbAgents " + items.length + "\n");
            for (Object o : items) {
                writer.write("environment.world.agent.Agent\nnbArgs 5\nInteger ");
                writer.write("" + ((Agent) o).getX());
                writer.write("\nInteger ");
                writer.write("" + ((Agent) o).getY());
                writer.write("\nInteger ");
                writer.write("" + getView());
                writer.write("\nInteger ");
                writer.write("" + ((Agent) o).getID());
                writer.write("\nString \"");
                writer.write("" + ((Agent) o).getID() + "\"\n");
            }
            items = searchAll(getEnvironment().getWorld(PacketWorld.class));
            writer.write("\nnbPackets " + items.length + "\n");
            for (Object item : items) {
                writer.write(
                        "environment.world.packet.Packet\nnbArgs 3\nInteger ");
                writer.write("" + ((Packet) item).getX());
                writer.write("\nInteger ");
                writer.write("" + ((Packet) item).getY());
                writer.write("\nString \"");
                writer.write("" +
                        MyColor.getName(((Packet) item).getColor()) +
                        "\"\n");
            }
            items = searchAll(getEnvironment().getWorld(DestinationWorld.class));
            writer.write("\nnbDestinations " + items.length + "\n");
            for (Object item : items) {
                writer.write(
                        "environment.world.destination.Destination\nnbArgs 3\nInteger ");
                writer.write("" + ((Destination) item).getX());
                writer.write("\nInteger ");
                writer.write("" + ((Destination) item).getY());
                writer.write("\nString \"");
                writer.write("" +
                        MyColor.getName(((Destination) item).
                                getColor()) +
                        "\"\n");
            }
            items = searchAll(getEnvironment().getWorld(WallWorld.class));
            writer.write("\nnbWalls " + items.length + "\n");
            for (Object item : items) {
                writer.write(
                        "environment.world.wall.Wall\nnbArgs 2\nInteger ");
                writer.write("" + ((Wall) item).getX());
                writer.write("\nInteger ");
                writer.write("" + ((Wall) item).getY());
                writer.write("\n");
            }
            items = searchAll(getEnvironment().getWorld(EnergyStationWorld.class));
            StringBuilder str_energystations = new StringBuilder();
            int nbBatteries = 0;
            for (Object item : items) {
                if (item instanceof EnergyStation) {
                    nbBatteries++;
                    str_energystations.append("environment.world.energystation.EnergyStation\n")
                            .append("nbArgs 3\n")
                            .append("Integer ").append(((EnergyStation) item).getX())
                            .append("\nInteger ").append(((EnergyStation) item).getY())
                            .append("\nInteger ").append(agents.size() + nbBatteries)
                            .append("\n");
                }
            }
            writer.write("\nnbEnergyStations " + nbBatteries + "\n");
            writer.write(str_energystations.toString());

            writer.close();
            return true;
        } catch (IOException ioe) {
            return false;
        }
    }

    //--------------------------------------------------------------------------
    //		GETTERS & SETTERS
    //--------------------------------------------------------------------------

    private Environment getEnvironment() {
        return env;
    }

    private void setEnvironment(Environment env) {
        this.env = env;
    }

    private int getView() {
        return view;
    }

    private void setView(int view) {
        this.view = view;
    }

    private File getCurrentFile() {
        return curFile;
    }

    private void setCurrentFile(File file) {
        curFile = file;
        setTitle(WINDOW_TITLE + " - " + file.getAbsolutePath());
    }

    //--------------------------------------------------------------------------
    //		ATTRIBUTES
    //--------------------------------------------------------------------------

    private JPanel panel1 = new JPanel();
    private final JPanel panel2 = new JPanel();
    private final JButton btnPacket = new JButton();
    private final JButton btnDest = new JButton();
    private final JButton btnAgent = new JButton();
    private final JButton btnBattery = new JButton();
    private final JButton btnWall = new JButton();
    private final JButton btnRemove = new JButton();

    private final JLabel lblWidth = new JLabel();
    private final JLabel lblHeight = new JLabel();
    private final JLabel lblView = new JLabel();
    private final JLabel lblColor = new JLabel();
    private final JComboBox<String> jComboBoxWidth = new JComboBox<>();
    private final JComboBox<String> jComboBoxHeight = new JComboBox<>();
    private final JComboBox<String> jComboBoxView = new JComboBox<>();
    private final JComboBox<String> jComboBoxColor = new JComboBox<>();
    private final JMenuBar menuBar = new JMenuBar();
    private JFileChooser fileChooser;
    private Color selectedColor = java.awt.Color.blue;
    private final Color DEFAULT_COLOR = java.awt.Color.gray;
    private final String WINDOW_TITLE = "Environment Creator";

    private File curFile = null;
    private boolean changed = false;

    private Environment env;
    private int view;
    private final List<Class<? extends World<?>>> WORLDS = Setup.getInstance().getDefaultWorlds();
    private final VideoPanel videop = VideoPanel.getInstance();
    private int action = 0;
    private Vector<Agent> agents = new Vector<>();

    private final static int MAXSIZE = 100;
    private final static int MAXVIEW = 100;
}
