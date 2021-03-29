package util;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 *  A filename filter for javax.swing to return files with extension 'txt' only.
 */
public class TxtFileFilter extends FileFilter {
    protected TxtFileFilter() {
    }

    public static TxtFileFilter getInstance() {
        if (instance == null) {
            instance = new TxtFileFilter();
        }
        return instance;
    }

    public boolean accept(File file) {
        return (file.getName().endsWith(".txt"));
    }


    public String getDescription() {
        return "*.txt";
    }

    private static TxtFileFilter instance = null;
}
