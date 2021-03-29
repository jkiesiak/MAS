package synchronizer;

import util.Debug;
import util.AsciiReader;

import java.io.IOException;

/**
 *A class responsible for the creation of instances of all classes of this package needed at startup time when distributed
 * synchronization is used. The creation method used by this class requires a textfile from which the number of
 * PersonalSynchronizer instances are read.
 */
public class FileDistributedSynchronization extends DistributedSynchronization {

    /**
     *The file containing the number of PersonalSynchronizers needed.
     */
    private final String configFile;

    /**
     *Initialize a new File-DistributedSynchronization instance, relying on the file <file>.
     *@param file
         *       The configFile from which the number of needed synchronizers is read.
     *@pre   file <> null
     *@post  new.configFile==file.
     */
    public FileDistributedSynchronization(String file) {
        configFile = file;
    }

    /**
     *Create the number of PersonalSynchronizers mentioned in configFile.
     */
    public void createSynchroPackage() {
        int nbSynchros = 0;
        try {
            AsciiReader reader = new AsciiReader(configFile);
            reader.check("nbSynchros");
            nbSynchros = reader.readInt();
        } catch (IOException e) {
            System.err.println("Error when opening file: " + configFile);
        } catch (Exception e) {
            Debug.alert(this, e.getMessage());
        }
        for (int i = 0; i < nbSynchros; i++) {
            synchros[i] = new PersonalSynchronizer(i);
        }
    }
}
