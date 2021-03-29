package gui.setup;

import gui.editor.EnvironmentBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 *  Starting frame to select the application mode to start.
 */
public class MainMenu extends JFrame {

    //--------------------------------------------------------------------------
    //		CONSTRUCTOR
    //--------------------------------------------------------------------------

    /**
     *  Initializes a new MainMenu instance
     */
    public MainMenu() {

        // Set the window title
        setTitle("PacketWorld - main menu");

        // Set the toolkit
        Toolkit tk = Toolkit.getDefaultToolkit();

        // Set size & location
        Dimension d = tk.getScreenSize();
        int height = d.height;
        int width = d.width;
        setLocation(width / 8, height / 8);
        setSize(width / 4, height / 4);

        // What has to happen when the user wants to close the window
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // Create the main panel
        JPanel menuPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        getContentPane().setLayout(new BorderLayout(25, 25));
        getContentPane().add(new JLabel(), BorderLayout.NORTH);
        getContentPane().add(new JLabel(), BorderLayout.SOUTH);
        getContentPane().add(new JLabel(), BorderLayout.EAST);
        getContentPane().add(new JLabel(), BorderLayout.WEST);

        getContentPane().add(menuPanel, BorderLayout.CENTER);

        // Make parts and add them to the menu panel.
        JLabel title = new JLabel("Select the program mode to launch :\n", SwingConstants.CENTER);
        menuPanel.add(title);
        JButton guiButton = new JButton("Normal mode");
        guiButton.addActionListener(evt -> {
            Setup.getInstance().start();
            setVisible(false);
        });
        menuPanel.add(guiButton);

        JButton editorButton = new JButton("World Editor");
        editorButton.addActionListener(evt -> {
            EnvironmentBuilder builder = new EnvironmentBuilder();
            builder.init();
            builder.setVisible(true);
            setVisible(false);
        });
        menuPanel.add(editorButton);
    }
}
