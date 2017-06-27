package porletNavegadorProsa;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.io.PrintWriter;

import java.util.StringTokenizer;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import porletNavegadorProsa.logger.Log;

import porletNavegadorProsa.properties.PropertiesNav;

import porletNavegadorProsa.resource.PorletNavegadorBundle;

public class PorletNavegador extends GenericPortlet {
    protected String getTitle(RenderRequest request) {
        // Get the customized title. This defaults to the declared title.
        PortletPreferences prefs = request.getPreferences();
        return prefs.getValue(PORTLETTITLE_KEY, super.getTitle(request));
    }

    protected void doDispatch(RenderRequest request,
                              RenderResponse response) throws PortletException,
                                                              IOException {
        // Do nothing if window state is minimized.
        WindowState state = request.getWindowState();
        if (state.equals(WindowState.MINIMIZED)) {
            super.doDispatch(request, response);
            return;
        }

        // Get the content type for the response.
        String contentType = request.getResponseContentType();

        // Get the requested portlet mode.
        PortletMode mode = request.getPortletMode();

        // Reference a request dispatcher for dispatching to web resources
        PortletContext context = getPortletContext();
        PortletRequestDispatcher rd = null;

        // Dispatch based on content type and portlet mode.
        if (contentType.equals("text/html")) {
            if (mode.equals(PortletMode.VIEW)) {
                rd = context.getRequestDispatcher("/PorletNavegador/html/view.jsp");
            } else if (mode.equals(PortletMode.EDIT)) {
                rd = context.getRequestDispatcher("/PorletNavegador/html/edit.jsp");
            } else {
                super.doDispatch(request,response);
            }
        } else {
            super.doDispatch(request,response);
        }
        if (rd != null) {
            rd.include(request, response);
        } else {
            super.doDispatch(request, response);
        }
    }
    public static final String APPLY_ACTION = "apply_action";
    public static final String OK_ACTION = "ok_action";
    public static final String PORTLETTITLE_KEY = "portletTitle";

    private String[] buildValueArray(String values) {
        if (values.indexOf(',') < 0) {
            return new String[] {values};
        }
        StringTokenizer st = new StringTokenizer(values, ",");
        String[] valueArray = new String[st.countTokens()];
        int i = 0;
        while (st.hasMoreTokens()) {
            valueArray[i] = st.nextToken().trim();
            i++;
        }
        return valueArray;
    }

    public void processAction(ActionRequest request,
                              ActionResponse response) throws PortletException,
                                                              IOException {
        // Determine which action.
        String okAction = request.getParameter(OK_ACTION);
        String applyAction = request.getParameter(APPLY_ACTION);

        if (okAction != null || applyAction != null) {
            // Save the preferences.
            PortletPreferences prefs = request.getPreferences();
            String param = request.getParameter(PORTLETTITLE_KEY);
            prefs.setValues(PORTLETTITLE_KEY, buildValueArray(param));
            prefs.store();
            if (okAction != null) {
                response.setPortletMode(PortletMode.VIEW);
                response.setWindowState(WindowState.NORMAL);
            }
        }
        
              
        
        String currentPath=(String)request.getPortletSession().getAttribute("currentPath",PortletSession.APPLICATION_SCOPE);
        String usuario=(String)request.getPortletSession().getAttribute("usuario",PortletSession.APPLICATION_SCOPE);
        String downfile=request.getParameter("downfile");
        String idSesion=request.getPortletSession().getId();
        
        PropertiesNav config=new PropertiesNav();
        String dirTemp=config.getProperty(PropertiesNav.TRASH);
        File directorioBasura=new File(dirTemp);
        if(!directorioBasura.exists()){//NO exite lo creamos
            directorioBasura.mkdirs();
        }
        if(!(currentPath == null || usuario == null || downfile == null  || idSesion == null )){
            FileWriter fichero =new FileWriter(new File(dirTemp,idSesion));
            PrintWriter pw = new PrintWriter(fichero);
            pw.println("CurrentPath: " + currentPath);
            pw.println("Archivo: " + downfile);
            pw.println("Sesion: " + idSesion);
            pw.println("Usuario: " + usuario);
            fichero.close();
            Log.setup();
            Log.logger.info("El usuario " + usuario +" Descargara el archivo "+ currentPath+"/"+downfile);
            Log.tearDown();
            response.sendRedirect(request.getContextPath()+"/Download?currentPath="+currentPath+"&downfile="+downfile+"&usuario="+usuario+"&idSesion="+idSesion);
        }else {
            response.sendRedirect(request.getContextPath()+"/Download");
        }
    }
}
