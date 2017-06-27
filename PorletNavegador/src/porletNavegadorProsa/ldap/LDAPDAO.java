

package porletNavegadorProsa.ldap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.event.EventDirContext;
import javax.naming.event.NamingListener;

/**
 *
 * @author Lianet
 */
public class LDAPDAO {

    private static final Logger logger = Logger.getLogger(LDAPDAO.class.getSimpleName());
    private static final String INITIAL_CONTEXT_FACTORY = "java.naming.factory.initial";
    private static final String PROVIDER_URL = "java.naming.provider.url";
    private static final String SECURITY_PRINCIPAL = "java.naming.security.principal";
    private static final String SECURITY_CREDENTIALS = "java.naming.security.credentials";
    private static final String RESOURCE_FILE = "/configuration.properties";
    private static final String ENV_FILE_PATH = "ldap.config.file";
    private static final String JNDI_FILE_PATH = "java:comp/env/ldap/ConfigFilePath";
    private static String initialContextFactory;
    private static String providerUrl;
    private static String securityPrincipal;
    private static String securityCredentials;
    private static Properties properties;
    private static EventDirContext eventContext;
    private long searchResultLimit;
    private int searchTimeLimit;

    public LDAPDAO() throws ConfigurationException {
        if(properties == null) {
            LDAPDAO.loadProperties();
            initialContextFactory = LDAPDAO.getProperty(INITIAL_CONTEXT_FACTORY);
            providerUrl = LDAPDAO.getProperty(PROVIDER_URL);
            securityPrincipal = LDAPDAO.getProperty(SECURITY_PRINCIPAL);
            securityCredentials = LDAPDAO.getProperty(SECURITY_CREDENTIALS);
        }
    }

    public enum Operation {
        ADD_ATTRIBUTE(1),
        REPLACE_ATTRIBUTE(2),
        REMOVE_ATTRIBUTE(3);
        private int number;
        Operation(int number) {
            this.number = number;
        }
        public int toInt() {
            return this.number;
        }
    }

    public enum SearchScope {
        OBJECT_SCOPE(0),
        ONELEVEL_SCOPE(1),
        SUBTREE_SCOPE(2);
        private int number;
        SearchScope(int number) {
            this.number = number;
        }
        public int toInt() {
            return this.number;
        }
    }

    private static String getConfigFilePath() throws ConfigurationException {
        Context jndiContext = null;
        String path = null;
        path = System.getProperty(ENV_FILE_PATH);
        if (path == null) {
            try {
                jndiContext = new InitialContext();
                path = (String) jndiContext.lookup(JNDI_FILE_PATH);
            } catch (NoInitialContextException exception) {
                throw new ConfigurationException(exception);
            } catch (NamingException exception) {
                throw new ConfigurationException(exception);
            } finally {
                try {
                    if (jndiContext != null) {
                        jndiContext.close();
                    }
                } catch (NamingException exception) {
                     LDAPException innerException =
                             new LDAPException(exception);
                     logger.log(Level.WARNING, innerException.getMessage(), innerException);
                }
            }
        }
        return path;
    }

    private static void loadProperties() throws ConfigurationException {
        File file = null;
        InputStream stream = null;
        LDAPDAO.properties = new Properties();
        try {
            stream = LDAPDAO.class.getResourceAsStream(RESOURCE_FILE);
            if(stream == null) {
                file = new File(LDAPDAO.getConfigFilePath());
                stream = new FileInputStream(file);
            }
            LDAPDAO.properties.load(stream);
        } catch(FileNotFoundException exception) {
            throw new ConfigurationException(exception);
        } catch(IOException exception) {
            throw new ConfigurationException(exception);
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException exception) {
                LDAPException innerException = new LDAPException(exception);
                logger.log(Level.WARNING, innerException.getMessage(), innerException);
            }
        }
    }

    private static String getProperty(String name) throws ConfigurationException {
        String value = LDAPDAO.properties.getProperty(name);
        if(value == null || value.equals("")) {
            throw new ConfigurationException(name);
        } else {
            return value;
        }
    }

    private DirContext open() throws LDAPException {
        Properties contextProperties = new Properties();
        contextProperties.setProperty(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
        contextProperties.setProperty(Context.PROVIDER_URL, providerUrl);
        contextProperties.setProperty(Context.SECURITY_PRINCIPAL, securityPrincipal);
        contextProperties.setProperty(Context.SECURITY_CREDENTIALS, securityCredentials);
        try {
            return new InitialDirContext(contextProperties);
        } catch(NamingException exception) {
            throw new LDAPException(exception);
        }
    }

    private void close(Context context) {
        if(context != null) {
            try {
                context.close();
            } catch(NamingException exception) {
                System.err.println(exception.getMessage());
            }
        }
    }
    
    public long getSearchResultLimit() {
        return this.searchResultLimit;
    }
    
    public int getSearchTimeLimit() {
        return this.searchTimeLimit;
    }

    public void setSearchResultLimit(long countLimit) {
        this.searchResultLimit = countLimit;
    }

    public void setSearchTimeLimit(int timeLimit) {
        this.searchTimeLimit = timeLimit;
    }

    public Collection<LDAPAttribute> getAttributes(String name)
            throws LDAPException {
        DirContext dirContext = this.open();
        Collection<LDAPAttribute> attributeList = new ArrayList<LDAPAttribute>();
        Attributes attributes = null;
        try {
            attributes = dirContext.getAttributes(name);
            attributeList = LDAPUtil.convertToAttributeCollection(attributes.getAll());
        } catch(NamingException exception) {
            throw new LDAPException(exception);
        }
        this.close(dirContext);
        return attributeList;
    }

    public Collection<LDAPAttribute> getSelectAttributes(String name, String[] returnsAttributes)throws LDAPException {
        DirContext dirContext = this.open();
        Collection<LDAPAttribute> attributeList = new ArrayList<LDAPAttribute>();
        Attributes attributes = null;
        try {
            attributes = dirContext.getAttributes(name,returnsAttributes);
            attributeList = LDAPUtil.convertToAttributeCollection(attributes.getAll());
        } catch(NamingException exception) {
            throw new LDAPException(exception);
        }
        this.close(dirContext);
        return attributeList;
    }
    public Collection<LDAPEntry> search(String context, Collection<LDAPAttribute> matchingAttributes) throws LDAPException {
        DirContext dirContext = this.open();
        NamingEnumeration<SearchResult> results = null;
        Collection<LDAPEntry> entryList = new ArrayList<LDAPEntry>();
        Attributes searchMatchingAttributes = LDAPUtil.convertToAttributes(matchingAttributes);
        try {
            results = dirContext.search(context, searchMatchingAttributes);
            entryList = LDAPUtil.convertoToLDAPEntryResults(results);
        } catch(NamingException exception) {
            throw new LDAPException(exception);
        }
        this.close(dirContext);
        return entryList;
    }

    public Collection<LDAPEntry> search(String context, Collection<LDAPAttribute> matchingAttributes, String[] attributesToReturn) throws LDAPException {
        DirContext dirContext = this.open();
        NamingEnumeration<SearchResult> results = null;
        Collection<LDAPEntry> entryList = new ArrayList<LDAPEntry>();
        Attributes searchMatchingAttributes = LDAPUtil.convertToAttributes(matchingAttributes);
        try {
            results = dirContext.search(context, searchMatchingAttributes, attributesToReturn);
            entryList = LDAPUtil.convertoToLDAPEntryResults(results);
        } catch(NamingException exception) {
            throw new LDAPException(exception);
        }
        this.close(dirContext);
        return entryList;
    }

    public Collection<LDAPEntry> search(String context, String filter, SearchScope scope) throws LDAPException {
        SearchControls controls = this.getSearchControls(scope.toInt());
        return this.search(context, filter, controls);
    }

    public Collection<LDAPEntry> search(String context, String filter, Object[] filterArguments, SearchScope scope) throws LDAPException {
        SearchControls controls = this.getSearchControls(scope.toInt());
        return this.search(context, filter, filterArguments, controls);
    }

    public Collection<LDAPEntry> search(String context, String filter, SearchScope scope, String[] attributes) throws LDAPException {
        SearchControls controls = this.getSearchControls(scope.toInt(), attributes);
        return this.search(context, filter, controls);
    }

    public Collection<LDAPEntry> search(String context, String filter, Object[] filterArguments, SearchScope scope, String[] attributes) throws LDAPException {
        SearchControls controls = this.getSearchControls(scope.toInt(), attributes);
        return this.search(context, filter, filterArguments, controls);
    }

    private Collection<LDAPEntry> search(String context, String filter, SearchControls controls) throws LDAPException {
        DirContext dirContext = this.open();
        NamingEnumeration<SearchResult> results = null;
        Collection<LDAPEntry> entryList = new ArrayList<LDAPEntry>();
        try {
            results = dirContext.search(context, filter, controls);
            entryList = LDAPUtil.convertoToLDAPEntryResults(results);
        } catch(NamingException exception) {
            throw new LDAPException(exception);
        }
        this.close(dirContext);
        return entryList;
    }

    private Collection<LDAPEntry> search(String context, String filter, Object[] filterArguments, SearchControls controls) throws LDAPException {
        DirContext dirContext = this.open();
        NamingEnumeration<SearchResult> results = null;
        Collection<LDAPEntry> entryList = new ArrayList<LDAPEntry>();
        try {
            results = dirContext.search(context, filter, filterArguments, controls);
            entryList = LDAPUtil.convertoToLDAPEntryResults(results);
        } catch(NamingException exception) {
            throw new LDAPException(exception);
        }
        this.close(dirContext);
        return entryList;
    }

    public void create(LDAPEntry entry) throws LDAPException, InvalidDataException {
        if(entry == null) {
            throw new InvalidDataException(entry);
        }
        DirContext dirContext = this.open();
        Attributes attributes = LDAPUtil.convertToAttributes(entry.getAttributes());
        try {
            if(attributes == null) {
                dirContext.createSubcontext(entry.getName());
            } else {
                dirContext.createSubcontext(entry.getName(), attributes);
            }
        } catch(NamingException exception) {
            throw new LDAPException(exception);
        }
        this.close(dirContext);
    }

    public void rename(String oldName, String newName) throws LDAPException {
        DirContext dirContext = this.open();
        try {
            dirContext.rename(oldName, newName);
        } catch(NamingException exception) {
            throw new LDAPException(exception);
        }
        this.close(dirContext);
    }

    public void modify(String name, Map<LDAPAttribute, Operation> modifications) throws LDAPException {
        DirContext dirContext = this.open();
        ModificationItem[] modificationItems = new ModificationItem[modifications.size()];
        int modificationNumber = 0;
        for(LDAPAttribute ldapAttribute : modifications.keySet()) {
            Attribute attribute = LDAPUtil.convertToAttribute(ldapAttribute);
            Operation operation = modifications.get(ldapAttribute);
            int operationId = operation.toInt();
            ModificationItem modificationItem = new ModificationItem(operationId, attribute);
            modificationItems[modificationNumber++] = modificationItem;
        }
        try {
            dirContext.modifyAttributes(name, modificationItems);
        } catch(NamingException exception) {
            throw new LDAPException(exception);
        }
        this.close(dirContext);
    }

    public void remove(String name) throws LDAPException {
        DirContext dirContext = this.open();
        try {
            dirContext.destroySubcontext(name);
        } catch(NamingException exception) {
            throw new LDAPException(exception);
        }
        this.close(dirContext);
    }

    public void bind(LDAPEntry entry, Object object) throws InvalidDataException, LDAPException {
        if(entry == null) {
            throw new InvalidDataException(entry);
        }
        DirContext dirContext = this.open();
        try {
            if(entry.getAttributes() == null) {
                dirContext.bind(entry.getName(), object);
            } else {
                Attributes attributes = LDAPUtil.convertToAttributes(entry.getAttributes());
                dirContext.bind(entry.getName(), object, attributes);
            }
        } catch(NamingException exception) {
            throw new LDAPException(exception);
        }
    }

    public Object lookup(String name) throws LDAPException {
        DirContext dirContext = this.open();
        Object object = null;
        try {
            object = dirContext.lookup(name);
        } catch(NamingException exception) {
            throw new LDAPException(exception);
        }
        this.close(dirContext);
        return object;
    }

    public void rebind(LDAPEntry entry, Object object) throws LDAPException, InvalidDataException {
        if(entry == null) {
            throw new InvalidDataException(entry);
        }
        DirContext dirContext = this.open();
        try {
            if(entry.getAttributes() == null) {
                dirContext.rebind(entry.getName(), object);
            } else {
                Attributes attributes = LDAPUtil.convertToAttributes(entry.getAttributes());
                dirContext.rebind(entry.getName(), object, attributes);
            }
        } catch(NamingException exception) {
            throw new LDAPException(exception);
        }
        this.close(dirContext);
    }

    public void unbind(String name) throws LDAPException {
        DirContext dirContext = this.open();
        try {
            dirContext.unbind(name);
        } catch(NamingException exception) {
            throw new LDAPException(exception);
        }
        this.close(dirContext);
    }

    public void addNamingListener(String target, SearchScope scope, NamingListener listener) throws LDAPException, EventNotSupportedException {
        if(eventContext == null) {
            eventContext = this.getEventContext();
        }
        try {
            eventContext.addNamingListener(target, scope.toInt(), listener);
        } catch(NamingException exception) {
            throw new LDAPException(exception);
        }
    }

    public void addNamingListener(String target, String filter, SearchScope scope, NamingListener listener) throws LDAPException, EventNotSupportedException {
        SearchControls controls = this.getSearchControls(scope.toInt());
        if(eventContext == null) {
            eventContext = this.getEventContext();
        }
        try {
            eventContext.addNamingListener(target, filter, controls, listener);
        } catch(NamingException exception) {
            throw new LDAPException(exception);
        }
    }

    public void addNamingListener(String target, String filter, Object[] filterArguments, SearchScope scope, NamingListener listener) throws LDAPException, EventNotSupportedException {
        SearchControls controls = this.getSearchControls(scope.toInt());
        if(eventContext == null) {
            eventContext = this.getEventContext();
        }
        try {
            eventContext.addNamingListener(target, filter, filterArguments, controls, listener);
        } catch(NamingException exception) {
            throw new LDAPException(exception);
        }
    }

    public void removeNamingListener(NamingListener listener) throws LDAPException, EventNotSupportedException {
        if(eventContext == null) {
            eventContext = this.getEventContext();
        }
        try {
            eventContext.removeNamingListener(listener);
        } catch(NamingException exception) {
            throw new LDAPException(exception);
        }
    }

    private EventDirContext getEventContext() throws LDAPException, EventNotSupportedException {
        DirContext dirContext = this.open();
        try {
            Object context = dirContext.lookup("");
            if(context instanceof EventDirContext) {
                eventContext = (EventDirContext)context;
            } else {
                throw new EventNotSupportedException();
            }
        } catch(NamingException exception) {
            throw new LDAPException(exception);
        }
        this.close(dirContext);
        return eventContext;
    }

    private SearchControls getSearchControls(int scope) {
        SearchControls controls = new SearchControls();
        controls.setCountLimit(this.searchResultLimit);
        controls.setTimeLimit(this.searchTimeLimit);
        controls.setSearchScope(scope);
        return controls;
    }

    private SearchControls getSearchControls(int scope, String[] attributes) {
        SearchControls controls = new SearchControls();
        controls.setCountLimit(this.searchResultLimit);
        controls.setTimeLimit(this.searchTimeLimit);
        controls.setSearchScope(scope);
        controls.setReturningAttributes(attributes);
        return controls;
    }

}
