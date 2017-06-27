package porletNavegadorProsa.logger;


import java.io.IOException;

import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import porletNavegadorProsa.properties.PropertiesNav;

/**
 *
 * @author Lianet
 */
public class Log {
    public static FileHandler fileHandler;
    public static SimpleFormatter formatter;
    public static final Logger logger = Logger.getLogger("NDA");


    static public final void setup() {
        PropertiesNav properties = new PropertiesNav();
        String level = properties.getProperty(PropertiesNav.LEVELLOG);
        try {
            Log.fileHandler =
                    new FileHandler(properties.getProperty(PropertiesNav.DIRLOG),
                                    true);
            if (level.equals("ALL")) {
                Log.fileHandler.setLevel(Level.ALL);
                logger.setLevel(Level.ALL);
            } else if (level.equals("SEVERE")) {
                Log.fileHandler.setLevel(Level.SEVERE);
                logger.setLevel(Level.SEVERE);
            } else if (level.equals("WARNING")) {
                Log.fileHandler.setLevel(Level.WARNING);
                logger.setLevel(Level.WARNING);
            } else if (level.equals("INFO")) {
                Log.fileHandler.setLevel(Level.INFO);
                logger.setLevel(Level.INFO);
            } else if (level.equals("CONFIG")) {
                Log.fileHandler.setLevel(Level.CONFIG);
            } else if (level.equals("FINE")) {
                Log.fileHandler.setLevel(Level.FINE);
            } else if (level.equals("FINER")) {
                Log.fileHandler.setLevel(Level.FINER);
            } else if (level.equals("FINEST")) {
                Log.fileHandler.setLevel(Level.FINEST);
            } else if (level.equals("OFF")) {
                Log.fileHandler.setLevel(Level.OFF);
            } else {
                Log.fileHandler.setLevel(Level.ALL);
            }
            formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);
            Handler[] handlers = logger.getHandlers();
            logger.removeHandler(handlers[0]);
        } catch (IOException ex) {
            Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    static public void tearDown() {
        Log.fileHandler.close();
    }
}
