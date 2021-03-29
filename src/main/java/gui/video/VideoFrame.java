package gui.video;

import environment.Environment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class VideoFrame extends JFrame {

    final VideoPanel videoPanel;
    final UserPanel userPanel;

    /**
     *  Initializes a new VideoFrame object
     *
     * @param  env      The applications Environment instance
     * @param  speed    The speed at which we want the application to run
     */
    public VideoFrame(Environment env, int speed) {
        setTitle("Video for the agent application");
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        int height = d.height;
        int width = d.width;
        setLocation(550, 0);
        setSize(17 * width / 30, 18 * height / 20);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        videoPanel = VideoPanel.getInstance();
        videoPanel.setFrame(this);
        //videoPanel.setController(controller);
        videoPanel.setEnvironment(env);
        Container contentPane = getContentPane();
        contentPane.add(videoPanel, "Center");
        userPanel = UserPanel.getInstance();
        userPanel.setFrame(this);
        videoPanel.setUserPanel(userPanel);
        videoPanel.initiate();
        contentPane.add(userPanel, "South");
    }

    public VideoPanel getVideoPanel() {
        return videoPanel;
    }

}
