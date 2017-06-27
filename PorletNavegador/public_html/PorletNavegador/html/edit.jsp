<%@ page contentType = "text/html; charset=windows-1252"
         pageEncoding = "windows-1252"
         import = "javax.portlet.*, java.util.*, porletNavegadorProsa.PorletNavegador, porletNavegadorProsa.resource.PorletNavegadorBundle"%>
<%@ taglib uri = "http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects/>
<%
    PortletPreferences prefs = renderRequest.getPreferences();
    ResourceBundle res =
        portletConfig.getResourceBundle(renderRequest.getLocale());
%>

<form action="<portlet:actionURL/>" method="POST">
  <table border="0">
    <tr>
      <td width="20%">
        <p class="portlet-form-field" align="right">
          <%=  res.getString(PorletNavegadorBundle.PORTLETTITLE) %>
        </p>
      </td>
      <td width="80%">
        <input class="portlet-form-input-field"
               type="TEXT"
               name="<%= PorletNavegador.PORTLETTITLE_KEY %>"
               value="<%= prefs.getValue(PorletNavegador.PORTLETTITLE_KEY, res.getString("javax.portlet.title")) %>"
               size="20">
      </td>
    </tr>
    <tr>
      <td colspan="2" align="center">
        <input class="portlet-form-button" type="submit"
                                             name="<%=PorletNavegador.OK_ACTION%>"
                                             value="<%=res.getString(PorletNavegadorBundle.OK_LABEL)%>">
        <input class="portlet-form-button" type="submit"
                                             name="<%=PorletNavegador.APPLY_ACTION%>"
                                             value="<%=res.getString(PorletNavegadorBundle.APPLY_LABEL)%>">
      </td>
    </tr>
  </table>
</form>
