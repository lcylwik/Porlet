package porletNavegadorProsa.properties;


import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import porletNavegadorProsa.logger.Log;



/**
 *
 * @author lianet
 */
    public class PropertiesNav {
    private Properties properties;
    private String CONFIG_FILE_NAME = "configuration.properties";
    public final static String ROOT = "rootContext";
    public final static String TRASH = "tmp";
    public final static String NAVEGADOR = "dn.navegador";
    public final static String CLIENTES = "dn.clientes";
    public final static String VARDIR = "var.dir";
    public final static String LEVELLOG="var.level.log";
    public final static String DIRLOG="var.dir.log";

    public PropertiesNav() {
        this.properties = new Properties();
        try {
            properties.load(PropertiesNav.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME));
        } catch (IOException ex) {
            Log.logger.log(Level.SEVERE, null, ex);
            Log.tearDown();
        }
    }

    /**
     * Retorna la propiedad de configuracion seleccionada
     * @param key
     * @return
     */
    public String getProperty(String key) {
        return this.properties.getProperty(key);
    }//getProperty
}
