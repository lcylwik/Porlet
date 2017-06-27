
package porletNavegadorProsa.navegador;

import porletNavegadorProsa.ldap.ConfigurationException;
import porletNavegadorProsa.ldap.LDAPAttribute;
import porletNavegadorProsa.ldap.LDAPDAO;
import porletNavegadorProsa.ldap.LDAPEntry;
import porletNavegadorProsa.ldap.LDAPException;
import porletNavegadorProsa.logger.Log;
import porletNavegadorProsa.properties.PropertiesNav;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Lianet
 */
public class LdapNavegador {

    private List directorios;
    private String usuario;
    private PropertiesNav config;
    private LDAPDAO dao;

    private Navegador navegador;
    
    public LdapNavegador(String usuario)  {
        try {
            this.usuario = usuario;
            this.directorios = new ArrayList();
            this.config = new PropertiesNav();
            this.dao = new LDAPDAO();

            navegador = new Navegador(usuario);
            Log.logger.info("El usuario " + usuario + " acceso al navegador");
        } catch (ConfigurationException ex) {
            Log.logger.log(Level.SEVERE, null, ex);

            Log.tearDown();

        } catch (IOException ex) {

            Log.logger.log(Level.SEVERE, null, ex);
            Log.tearDown();
        }
    }
    public List getDirectorios() {
        return directorios;
    }

    public String getRootPath(){
        return navegador.getRootPath();
    }

    public void  setCurrentPath(String currentPath){
        try {
            navegador.setCurrentPath(currentPath);
            Log.logger.info("El usuario " + usuario + " entro a " + currentPath);
        } catch (IOException ex) {
            Log.logger.log(Level.SEVERE, null, ex);
            Log.tearDown();;

        }

    }

    public boolean cargaDirectorioRaiz(int [] x){
           return navegador.cargaDirectorioRaiz(x);
    }
    public List  listaDirectorio(){
        List ficherosReturns=new ArrayList();

        try {
            ficherosReturns = navegador.listaDirectorio();
        } catch (IOException ex) {
            Log.logger.log(Level.SEVERE, null, ex);
            Log.tearDown();

            return ficherosReturns;
        }
        return ficherosReturns;
    }

    public boolean cargaDirectorio(String padre){
            return navegador.cargaDirectorio(padre);
    }

    public String BuscaDirectorio(String dir){
        
        return navegador.BuscaDirectorio(dir);
        
    }

    public boolean isRoot(){

        return navegador.isRoot();

    }
    
    public boolean exists(String dir){
     
        return navegador.exists(dir);

    }

    public String getCurrentPath(){
        return navegador.getCurrentPath();
    }
    public String getUserCurrentPath(){

        return navegador.getUserCurrentPath();

    }

    public boolean accesoLDAPNavegador(){
        try {
            String distinguishedName = config.getProperty(PropertiesNav.NAVEGADOR);
            String[] returnAtrributes = {"uniqueMember"};
            Collection<LDAPAttribute> retrievedAttributes = dao.getSelectAttributes(distinguishedName, returnAtrributes);
            for (LDAPAttribute attribute : retrievedAttributes) {
                Collection values = attribute.getValues();
                for (Object value : values) {
                    String UIDuser = this.getUID(this.usuario);
                    String UIDuserLdap = this.getUID(value.toString());
                    if (UIDuser.equals(UIDuserLdap)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (LDAPException ex) {
            Log.logger.log(Level.SEVERE, null, ex);
            Log.tearDown();

            return false;
        }
    }
    public void setDirectorios(int [] x)  {
        try {
            String UID = this.getUID(this.usuario);
            String simpleFilter = "(&(" + UID + "))";
            String distinguishedNameClients = config.getProperty(PropertiesNav.CLIENTES);
            String varDir = config.getProperty(PropertiesNav.VARDIR);
            String simpleFilterClient = "(&(" + UID + ")(" + varDir + "=*))";
            String[] returningAttributes = new String[]{config.getProperty(PropertiesNav.VARDIR)};
            Collection<LDAPEntry> resultsClient = dao.search(distinguishedNameClients, simpleFilterClient, LDAPDAO.SearchScope.SUBTREE_SCOPE, returningAttributes);
            if (resultsClient.isEmpty()) {
                Collection<LDAPEntry> resultsClientSimple = dao.search(distinguishedNameClients, simpleFilter, LDAPDAO.SearchScope.SUBTREE_SCOPE);
                for (LDAPEntry result : resultsClientSimple) {
                    String DNcarpeta = result.getName();
                    StringTokenizer tokens = new StringTokenizer(DNcarpeta, ",");
                    int cont = 0;
                    String DNbanco = null;
                    while (tokens.hasMoreTokens()) {
                        cont++;
                        String aux = tokens.nextToken();
                        if (cont == 3) {
                            DNbanco = aux;
                        } else if (cont == 4) {
                            DNbanco += "," + aux + "," + config.getProperty(PropertiesNav.CLIENTES);
                        }
                    }
                    Collection<LDAPAttribute> retrievedAttributes = dao.getAttributes(DNbanco);
                    for (LDAPAttribute attribute : retrievedAttributes) {
                        Collection values = attribute.getValues();
                        if (attribute.getId().equals(config.getProperty(PropertiesNav.VARDIR))) {
                            for (Object value : values) {
                                this.directorios.add(value);
                            }
                        }
                    }
                }
            } else {
                for (LDAPEntry result : resultsClient) {
                    Collection<LDAPAttribute> foundAttributes = result.getAttributes();
                    for (LDAPAttribute attribute : foundAttributes) {
                        for (Object value : attribute.getValues()) {
                            StringTokenizer tokens = new StringTokenizer(value.toString(), ",");
                            while (tokens.hasMoreTokens()) {
                                this.directorios.add(tokens.nextToken().toString());
                            }
                        }
                    }
                }
            }
        } catch (LDAPException ex) {
            x[0]=1;

            Log.logger.log(Level.SEVERE, null, ex);
            Log.tearDown();
        }
    }

    public String getUsuario() {
        return usuario;
    }

    public String getUID(String DN){
        String UID = null;
        String[] splits=DN.split(",");
        for(String asset: splits){
            if(asset.matches("uid=.+"))
                UID=asset;
          break;
        }

        return UID;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
