package gui.setup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 *  Frame for selecting settings concerning the world.
 */
public class WorldConfigurationFrame extends JFrame {

    /**
     *  Initializes a new WorldConfigurationFrame object
     *
     * @param  setup        a Setup instance
     */
    public WorldConfigurationFrame(Setup setup) {
        setTitle("Configuration window");
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        int height = d.height;
        int width = d.width;
        setLocation(width / 8, height / 8);
//        setSize(12 * width / 18, 9 * height / 10);
        setSize(500, 300);
        addWindowListener(
            new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        WorldConfigurationPanel wcPanel = new WorldConfigurationPanel(this, setup);
        Container contentPane = getContentPane();
        contentPane.add(wcPanel, "Center");
    }
}
