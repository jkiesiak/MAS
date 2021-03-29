package util;

import java.io.File;

public class Variables {

    public static String BASE_PATH = System.getProperty("user.dir");

    public static String CONFIG_PATH = BASE_PATH + File.separator + "configfiles" + File.separator;
    public static String OUTPUT_PATH = BASE_PATH + File.separator + "output" + File.separator;

    public static String IMPLEMENTATIONS_PATH = Variables.CONFIG_PATH + "implementations" + File.separator;
    public static String ENVIRONMENTS_PATH = Variables.CONFIG_PATH + "environments" + File.separator;

    public static String WORLD_PROPERTIES_FILE = Variables.CONFIG_PATH + "worlds.properties";
    public static String LAWS_PROPERTIES_FILE = Variables.CONFIG_PATH + "lawsoftheuniverse.properties";
    public static String PERCEPTION_LAWS_PROPERTIES_FILE = Variables.CONFIG_PATH + "perceptionlaws.properties";
}
