
package porletNavegadorProsa.navegador;


import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import porletNavegadorProsa.logger.Log;
import porletNavegadorProsa.properties.PropertiesNav;
/**
 *
 * @author Lianet
 */
public final class Navegador {
    
     private PropertiesNav config;
     private String usuario;
     public  Map mapaDirectorio = new TreeMap();
     public  String rootPath;
     public String currentPath;

     public Navegador(String usuario) throws IOException{
        this.usuario=usuario;
        mapaDirectorio.clear();
        config=new PropertiesNav();
        this.setRoot();
    }

     public boolean isRoot(){
         if(this.currentPath.equals(rootPath)){
             return true;
         }else{
             return false;
         }
     }

    /**
     * Función que convierte a MD5 una cadena
     * @param stringToHash
     * @return
     */
    public  String hash(String stringToHash)  {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(stringToHash.getBytes());
            StringBuilder sb = new StringBuilder(2 * bytes.length);
            for (int i = 0; i < bytes.length; i++) {
                int low = (int)(bytes[i] & 0x0f);
                int high = (int)((bytes[i] & 0xf0) >> 4);
                sb.append(Integer.toHexString(high));
                sb.append(Integer.toHexString(low));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {

            Log.logger.log(Level.SEVERE, null, ex);
            Log.tearDown();
            return null;
        }
    }

    public void InsertaMapa(String cveMD5, String valor){
         mapaDirectorio.put(cveMD5,valor);
    }

    public String BuscaDirectorio(String cveMD5){
        String path=(String)mapaDirectorio.get(cveMD5);
        if (path==null) {
            path=this.currentPath;
        }
        return path;
    }
    /**
     * @param args the command line arguments
     */
    public boolean cargaDirectorio(String path) {
        try{
            mapaDirectorio.clear();
            File navegador= new File(path);
            File[] Archivos=navegador.listFiles();
        if (Archivos.length==0) {

            } else {
                for (File Archivo : Archivos){
                        if(Archivo.exists()){
                            try {
                                this.InsertaMapa(this.hash(Archivo.getCanonicalPath()), Archivo.getCanonicalPath());
                            } catch (IOException ex) {
                                Log.logger.log(Level.SEVERE, null, ex);
                                Log.tearDown();

                                return false;
                            }
                        }else{

                        }
                }

            }
            if(!this.isRoot()) {
                    this.InsertaMapa(this.hash(navegador.getParent()),navegador.getParent());
            }
        }catch(Exception e){

            Log.logger.severe(e.toString());
            Log.tearDown();
            return false;
        }
        return true;
    }

    public boolean cargaDirectorioRaiz(int [] x) {//SOlo con permisos

            //SOlo con permisos
            mapaDirectorio.clear();
            LdapNavegador dirLDAP = new LdapNavegador(this.usuario);
            dirLDAP.setDirectorios(x);
            List dirAutorizados = dirLDAP.getDirectorios();
            try{
                File navegador = new File(this.rootPath);
                File[] Archivos = navegador.listFiles();
                if (Archivos.length == 0) {

                } else {
                    for (File Archivo : Archivos) {
                        if (Archivo.exists() && dirAutorizados.contains(Archivo.getName())) {
                            try {
                                //Y es parte de los que tenemos permiso
                                this.InsertaMapa(this.hash(Archivo.getCanonicalPath()), Archivo.getCanonicalPath());
                            } catch (IOException ex) {
                                Log.logger.log(Level.SEVERE, null, ex);
                                Log.tearDown();

                                return false;
                            }
                        } else {

                        }

                    }
                 }
              }catch(Exception e){

                    Log.logger.log(Level.SEVERE, null, e);
                    Log.tearDown();
                    return false;
                  }

        return true;
    }

    public boolean exists(String clave){
        String BuscaDirectorio = this.BuscaDirectorio(clave);
        File apoyo=new File(BuscaDirectorio);
        if(apoyo.exists()){
            return true;
        }else {
            return false;
        }
    }
    public boolean isFile(String path, String name){
        String clave=this.hash(path+name);
        String BuscaDirectorio = this.BuscaDirectorio(clave);
        File apoyo=new File(BuscaDirectorio);
        if(apoyo.isFile()) {
            return true;
        }else {
            return false;
        }
    }

    public List listaDirectorio() throws IOException{//Solo listar lo que esta cargado
        List ficherosReturns=new ArrayList();

        File f=new File(this.currentPath);
        File[] entry;
        entry=f.listFiles();
        Arrays.sort(entry, new FileComp(1));
        if(!this.isRoot()) {
            Archivos file= new Archivos();
            file.setNombre("..");
            file.setCveMD5(this.hash(f.getParent()));
            file.setTamano(f.length());
            file.setFecha(f.lastModified());
            file.setIsDirectory(true);
            ficherosReturns.add(file);
        }
        for(File archivo : entry) {
            if(this.mapaDirectorio.containsValue(archivo.getCanonicalPath())){
                Archivos file= new Archivos();
                file.setNombre(archivo.getName());
                file.setCveMD5(this.hash(archivo.getCanonicalPath()));
                file.setTamano(archivo.length());
                file.setFecha(archivo.lastModified());
                file.setIsDirectory(archivo.isDirectory());
                ficherosReturns.add(file);
            }
        }
        return ficherosReturns;
    }
    public void setRoot() throws IOException{
        rootPath=config.getProperty(PropertiesNav.ROOT);
    }

    public void setCurrentPath(String path) throws IOException{
        currentPath=path;
        if(currentPath.indexOf(rootPath)==-1) {
            currentPath=rootPath;
        }else if(currentPath.indexOf(rootPath)!=0) {
            currentPath=rootPath;
        }
    }
    public String getCurrentPath(){
        return currentPath;
    }
    public String getRootPath(){
        return rootPath;
    }

    public String getUserCurrentPath(){
        String userCurrentPath;
        if(this.isRoot()){
            userCurrentPath="/";
        }else{
            userCurrentPath=this.currentPath.substring(this.rootPath.length());
        }
        return userCurrentPath;
    }
}
