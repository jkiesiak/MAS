package gui.setup;

import util.TxtFilenameFilter;
import util.Variables;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Arrays;

/**
 *  Panel for selecting settings concerning the world.
 */
@SuppressWarnings("FieldCanBeLocal")
public class WorldConfigurationPanel extends JPanel {

    /**
     *  Initializes a new WorldConfigurationPanel object
     *
     * @param  frame        parent frame
     * @param  setup        a Setup instance
     */
    public WorldConfigurationPanel(JFrame frame, Setup setup) {
        this.frame = frame;
        this.setup = setup;
        setLayout(new GridBagLayout());

        //title
        JPanel titlePanel = new JPanel();
        titlePanel.add(new JLabel("Define the world's characteristics"));
        placeComponent(titlePanel, 10);

        // strategy / agentImplementation
        JPanel iPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        iPanel.add(new JLabel("<html>Select the implementation<br>of the agents:", SwingConstants.CENTER));
        implementationList = new JComboBox<>(getDirList(Variables.IMPLEMENTATIONS_PATH));
        iPanel.add(implementationList);
        placeComponent(iPanel, 10);


        iPanel.add(new JLabel("Select the environment:"));
        environmentList = new JComboBox<>(getDirList(Variables.ENVIRONMENTS_PATH));
        iPanel.add(environmentList);
        placeComponent(iPanel, 10);


        JPanel cPanel = new JPanel(new GridLayout(1, 1, 10, 10));
        JButton continueButton = new JButton("Continue");
        continueButton.addActionListener(this::startController);
        continueButton.setEnabled(true);
        cPanel.add(continueButton);
        placeComponent(cPanel, 10);

    }

    /**
     *  Cfr. javax.swing.JComponent
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }


    private void warning(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
    }


    private void startController(ActionEvent evt) {
        if (implementationList.getSelectedItem() == null || environmentList.getSelectedItem() == null) {
            this.warning("Make sure you have selected an implementation as well as an environment before continuing.",
                    "Warning");
            return;
        }

        String implementation = implementationList.getItemAt(implementationList.getSelectedIndex());
        String environment = environmentList.getItemAt(environmentList.getSelectedIndex());

        this.startDefault(implementation, environment);
        frame.setVisible(false);
    }

    /**
     * Returns the filenames from all .txt-files in a specified directory.
     * @param dirName   the directory to investigate
     * @return          a Vector of the filenames (without extension).
     */
    private String[] getDirList(String dirName) {
        File dir = new File(dirName);
        if (!dir.isDirectory()) {
            System.err.println("Configpath " + dirName + " is not a directory.");
            return null;
        }
        String[] dirList = dir.list(TxtFilenameFilter.getInstance());
        for (int i = 0; i < dirList.length; i++) {
            int pointPos = dirList[i].lastIndexOf(".txt");
            dirList[i] = dirList[i].substring(0, pointPos);
        }
        Arrays.sort(dirList);
        return dirList;
    }



    public void startDefault(String impl, String worldFile) {
        setup.setImplementation(impl);
        setup.setEnvFile(worldFile);
        setup.make(false);
        setup.startGui();
    }

    protected void placeComponent(Component comp, int paddingY) {
        GridBagConstraints c = new GridBagConstraints();
        c.weighty = 0.5;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = componentCount;
        c.insets = new Insets(paddingY, 5, paddingY, 5);
        add(comp, c);
        componentCount++;
    }

    //--------------------------------------------------------------------------
    //		ATTRIBUTES
    //--------------------------------------------------------------------------

    private final JFrame frame;
    private final Setup setup;

    private final JComboBox<String> implementationList;
    private final JComboBox<String> environmentList;
    private static int componentCount = 0;
}
