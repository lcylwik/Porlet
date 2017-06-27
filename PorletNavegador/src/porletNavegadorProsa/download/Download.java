package porletNavegadorProsa.download;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import porletNavegadorProsa.logger.Log;
import porletNavegadorProsa.properties.PropertiesNav;

public class Download extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        File archivo=null;


        try{
        PropertiesNav config = new PropertiesNav();
        //Capturamos los datos
        String currentPath=(String)request.getParameter("currentPath");
        String usuario=(String)request.getParameter("usuario");
        String downfile=(String)request.getParameter("downfile");
        String idSesion=(String)request.getParameter("idSesion");
        //Variables que se leen del archivo para poder descargar
        String currentPathFile = null;
        String usuarioFile = null;
        String downfileFile = null;
        String idSesionFile = null;

         archivo = new File (config.getProperty(PropertiesNav.TRASH),idSesion);
        if (archivo.exists()){
            FileReader fr = null;
            try {
                fr = new FileReader(archivo);
                BufferedReader br = new BufferedReader(fr);
                String linea;
                while ((linea = br.readLine()) != null) {
                    StringTokenizer tokens = new StringTokenizer(linea);
                    while (tokens.hasMoreTokens()) {
                        String temp = tokens.nextToken();
                        if (temp.equals("CurrentPath:")) {
                            currentPathFile = tokens.nextToken();
                        } else if (temp.equals("Archivo:")) {
                            downfileFile = tokens.nextToken();
                        } else if (temp.equals("Sesion:")) {
                            idSesionFile = tokens.nextToken();
                        } else if (temp.equals("Usuario:")) {
                            usuarioFile = tokens.nextToken();
                        }
                    }
                }
                if (idSesionFile.equals(idSesion) && currentPathFile.equals(currentPath) && usuarioFile.equals(usuario) && downfileFile.equals(downfile)) {
                    Log.setup();
                    Log.logger.info("Se descargara el archivo " + currentPath + "/" + downfileFile);
                    Log.tearDown();
                    PrintWriter out = response.getWriter();
                    File f = new File(currentPath, downfileFile);
                    if (f.exists() && f.canRead()) {
                        response.setContentType(new MimetypesFileTypeMap().getContentType(f));
                        response.setHeader("Content-Disposition", "attachment;filename=\"" + f.getName() + "\"");
                        response.setContentLength((int) f.length());
                        BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(f));
                        int i;
                        out.flush();
                        while ((i = fileInputStream.read()) != -1) {
                            out.write(i);
                        }
                        out.flush();
                    }
                }
            } catch (IOException ex) {
                System.out.println("akiiiiiiiiiiiiii"+ex);
                Log.logger.severe(ex.toString());
                Log.tearDown();
                //Logger.getLogger(Download.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    fr.close();
                } catch (IOException ex) {
                    Log.logger.severe(ex.toString());
                    //Logger.getLogger(Download.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        }catch(Exception ex){
         
                Log.logger.severe(ex.toString());
                    if (archivo != null)
                        archivo.delete();
        }
        archivo.delete();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
