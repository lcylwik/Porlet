


package porletNavegadorProsa.ldap;

/**
 *
 * @author Lianet
 */
public class ConfigurationException extends Exception {

    private static final String ERR_MSG = "Following property is not defined in LDAP configuration file: ";

    public ConfigurationException(String name) {
        super(ERR_MSG + name);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }

}
