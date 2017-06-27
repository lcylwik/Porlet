<%@ page contentType="text/html; charset=windows-1252"
         pageEncoding="windows-1252"
         import="javax.portlet.*, java.util.*, porletNavegadorProsa.PorletNavegador, porletNavegadorProsa.logger.*,
         porletNavegadorProsa.navegador.*,porletNavegadorProsa.ldap.*,porletNavegadorProsa.resource.PorletNavegadorBundle"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects/>
<p class="portlet-font">
  Welcome, this is the 
  ${renderRequest.portletMode}
   mode.
</p>

<%
int nameDirectory = 0; //No hay error en nameDirectory
int [] aux = new int[1]; //aux: array auxiliar para el paso de parámetros
aux[0]=nameDirectory;
boolean flag=true;
//Log.setup();
String usuario= renderRequest.getRemoteUser();
session.setAttribute("usuario", usuario);//Guardamos el usuario en la sesión
LdapNavegador ldapNavegador=new LdapNavegador(usuario);
if(usuario!=null){
if (ldapNavegador.accesoLDAPNavegador()){
    String currentPath;//Variable donde indica en que directorio estamos
    List directorio=null;//Se guardan los directorios a mostrar
    if (request.getParameter("dir")== null || session.getAttribute("currentPath")==null){//Cuando aún no se ha navegado
        currentPath=ldapNavegador.getRootPath();
        session.setAttribute("currentPath",currentPath);
        session.setAttribute("parent",ldapNavegador.getRootPath());
        ldapNavegador.setCurrentPath(currentPath);
        flag=ldapNavegador.cargaDirectorioRaiz(aux);
        if(flag){
            directorio=ldapNavegador.listaDirectorio();
         }
    }else{
        session.setAttribute("parent",session.getAttribute("currentPath"));
        String padre=session.getAttribute("parent").toString();
        String dir=request.getParameter("dir");
        ldapNavegador.setCurrentPath(padre);
        if(ldapNavegador.isRoot()){
            flag=ldapNavegador.cargaDirectorioRaiz(aux);
        }else{
            flag=ldapNavegador.cargaDirectorio(padre);
        }
        if(flag){
            if(ldapNavegador.exists(dir)==true){
                currentPath=ldapNavegador.BuscaDirectorio(dir);
                ldapNavegador.setCurrentPath(currentPath);
                session.setAttribute("currentPath",ldapNavegador.getCurrentPath());
            }
            currentPath=ldapNavegador.getCurrentPath();
            if(ldapNavegador.isRoot()){
                flag=ldapNavegador.cargaDirectorioRaiz(aux);
            }else{
                flag=ldapNavegador.cargaDirectorio(currentPath);
            }
            if(flag){
                directorio=ldapNavegador.listaDirectorio();
            }
        }
    }
       nameDirectory = aux[0];
    //if unike memeber trono{
    if(nameDirectory != 1){
    if(flag){
        if (!directorio.isEmpty()){//Acceso LDAP
            //si los directorios estan vación que diga No cuenta con carpetas asignadas
                    out.println("<table width=\"100%\"><tr><th align=\"left\" colspan=\"3\">Directorio Actual: "+ ldapNavegador.getUserCurrentPath() +"</th></tr>"
                                +"<tr><th align=\"left\" >Nombre</th><th align=\"center\" >Tama&ntilde;o</th><th align=\"center\" >Fecha de Modificaci&oacute;n</th></tr>");
                    for (Object archivo : directorio){
                        Archivos a=(Archivos)archivo;
                        if(a.getIsDirectory()){ %>
                             <tr onMouseOut="this.style.backgroundColor=''" onmouseover="this.style.backgroundColor='#FF6666'"><td><img src="<%=request.getContextPath()%>/Images/carpeta.jpg" width="30" height="30" alt="Carpeta"/><a href="<portlet:renderURL/>&dir=<%=a.getCveMD5()%>">[<%=a.getNombre()%>]</a></td><td>&nbsp;</td><td>&nbsp;</td></tr>
                         <%}else{ %>
                            <tr onMouseOut="this.style.backgroundColor=''" onmouseover="this.style.backgroundColor='#FF6666'"><td><img src="<%=request.getContextPath()%>/Images/file.jpg" width="30" height="30" alt="Archivo"/><a href="<portlet:actionURL/>&downfile=<%=a.getNombre()%>"><%=a.getNombre()%></a></td><td align="center"><%=a.getTamano()%>&nbsp;kB</td><td align="center"><%=a.getFecha()%></td></tr>
        <%            }
                    }
                    out.println("</table>");
        }else{
            out.println("No cuenta con directorios asignados");
        }
    }else{
        out.println("El directorio al que se intenta accesar no se encuentra disponible");
    }
   }else{
           out.println("No cuenta con directorios definidos");
   }
}else{
    out.println("No cuenta con acceso a la aplicación");
}
}
Log.tearDown();
%>
<p class="portlet-font">Last Modification</p>