import gui.editor.EnvironmentBuilder;
import gui.setup.MainMenu;
import gui.setup.Setup;
import gui.video.BatchMAS;

import javax.swing.*;

/**
 * Main class for the packet world application.
 */

public class Application {

    /**
     *  Initializes a new PacketWorld object
     */
    public Application() {}

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        if (args.length > 0) {
            if (args[0].equals("gui")) {
                Setup.getInstance().start();
            } else if (args[0].equals("quick")) {
                Setup.getInstance().start(args);
            } else if (args[0].equals("batchgui")) {
                BatchMAS prog = new BatchMAS();
                prog.init();
                prog.setVisible(true);
            } else if (args[0].equals("file") && args[1] != null) {
                Setup.getInstance().start(args);
            } else if (args[0].equals("editor")) {
                EnvironmentBuilder builder = new EnvironmentBuilder();
                builder.init();
                builder.setVisible(true);
            } else {
                System.out.println("Usage: java PacketWorld [options]");
                System.out.println("options:");
                System.out.println(
                    "    quick     Launch the application with default settings.");
                System.out.println(
                    "    gui       Start the gui application.");
                System.out.println(
                    "    editor    Make a world with the world editor.");
                System.out.println(
                    "    batchgui  Fast data run - GUI interface but no graphical output");
                System.out.println(
                    "    batch     Fast data run - Text interface and text output");
                System.out.println(
                    "    file [file] Start the application from a configuration file");
            }
        } else {
            MainMenu mm = new MainMenu();
            mm.setVisible(true);
        }
    }
}
