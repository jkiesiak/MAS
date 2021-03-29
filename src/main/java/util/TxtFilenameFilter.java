package util;

import java.io.File;
import java.io.FilenameFilter;

/**
 *  A filename filter for java.io to return files with extension 'txt' only.
 */
public class TxtFilenameFilter implements FilenameFilter {
    protected TxtFilenameFilter() {
    }

    public static TxtFilenameFilter getInstance() {
        if (instance == null) {
            instance = new TxtFilenameFilter();
        }
        return instance;
    }

    public boolean accept(File dirName, String fileName) {
        return (fileName.endsWith(".txt"));
    }

    private static TxtFilenameFilter instance = null;
}