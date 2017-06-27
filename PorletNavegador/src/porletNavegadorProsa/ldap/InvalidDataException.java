
package porletNavegadorProsa.ldap;

/**
 *
 * @author Lianet
 */
public class InvalidDataException extends Exception {

    Object data;

    public InvalidDataException(Object data) {
        this.data = data;
    }

}
