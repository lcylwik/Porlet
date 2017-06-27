

package porletNavegadorProsa.ldap;

import java.util.ArrayList;
import java.util.Collection;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchResult;

/**
 *
 * @author Lianet
 */
public class LDAPUtil {

    static Collection<LDAPAttribute> convertToAttributeCollection(NamingEnumeration attributes)
            throws NamingException {
        if(attributes == null) {
            return null;
        }
        Collection<LDAPAttribute> attributeCollection = new ArrayList<LDAPAttribute>();
        while(attributes.hasMore()) {
            Attribute attribute = (Attribute)attributes.next();
            String id = attribute.getID();
            Collection valueList = LDAPUtil.convertToAttributeValueCollection(attribute.getAll());
            LDAPAttribute ldapAttribute = new LDAPAttribute();
            ldapAttribute.setId(id);
            ldapAttribute.setValues(valueList);
            attributeCollection.add(ldapAttribute);
        }
        attributes.close();
        return attributeCollection;
    }

    static Collection<String> convertToAttributeValueCollection(NamingEnumeration values)
            throws NamingException {
        if(values == null) {
            return null;
        }
        Collection valueList = new ArrayList<String>();
        while(values.hasMore()) {
            Object value = values.next();
            valueList.add(value);
        }
        values.close();
        return valueList;
    }

    static Attributes convertToAttributes(Collection<LDAPAttribute> attributeList) {
        if(attributeList == null) {
            return null;
        }
        Attributes attributes = new BasicAttributes();
        for(LDAPAttribute ldapAttribute : attributeList) {
            Collection<String> values = ldapAttribute.getValues();
            Attribute attribute = new BasicAttribute(ldapAttribute.getId());
            for(String value : values) {
                attribute.add(value);
            }
            attributes.put(attribute);
        }
        return attributes;
    }

    static Attribute convertToAttribute(LDAPAttribute ldapAttribute) {
        Attribute attribute = new BasicAttribute(ldapAttribute.getId());
        Collection values = ldapAttribute.getValues();
        if(values != null) {
            for(Object value : values) {
                attribute.add(value);
            }
        }
        return attribute;

    }

    static Collection<LDAPEntry> convertoToLDAPEntryResults(NamingEnumeration<SearchResult> results) throws NamingException {
        if(results != null) {
            Collection<LDAPEntry> entryList = new ArrayList<LDAPEntry>();
            while(results.hasMore()) {
                SearchResult result = results.next();
                Collection<LDAPAttribute> attributeCollection = LDAPUtil.convertToAttributeCollection(result.getAttributes().getAll());
                LDAPEntry entry = new LDAPEntry();
                entry.setName(result.getName());
                entry.setAttributes(attributeCollection);
                entryList.add(entry);
            }
            return entryList;
        } else {
            return null;
        }
    }

}
