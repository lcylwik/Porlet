
package porletNavegadorProsa.ldap;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Lianet
 */
public class LDAPAttribute implements Cloneable {

    private String id;
    private Collection values;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Collection getValues() {
        return this.values;
    }

    public void setValues(Collection values) {
        this.values = values;
    }

    @Override
    public LDAPAttribute clone() {
        LDAPAttribute clonedAttribute = new LDAPAttribute();
        clonedAttribute.setId(this.id);
        Collection clonedValues = new ArrayList();
        for(Object value : this.values) {
            clonedValues.add(value);
        }
        clonedAttribute.setValues(clonedValues);
        return clonedAttribute;
    }

}
