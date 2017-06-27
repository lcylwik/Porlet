package porletNavegadorProsa.ldap;

import java.util.ArrayList;
import java.util.Collection;

public class LDAPEntry implements Cloneable {

    private String name;
    private Collection<LDAPAttribute> attributes;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<LDAPAttribute> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Collection<LDAPAttribute> attributes) {
        this.attributes = attributes;
    }

    /**
     * @return
     */
    @Override
    public LDAPEntry clone() {
        LDAPEntry entry = new LDAPEntry();
        entry.setName(this.name);
        Collection<LDAPAttribute> entryAttributes = new ArrayList<LDAPAttribute>();
        for(LDAPAttribute attribute : this.attributes) {
            LDAPAttribute clonedAttribute = attribute.clone();
            entryAttributes.add(clonedAttribute);
        }
        return entry;
    }

}