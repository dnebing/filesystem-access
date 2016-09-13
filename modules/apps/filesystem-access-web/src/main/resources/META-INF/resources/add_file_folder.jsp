<%@ page import="com.liferay.filesystemaccess.portlet.Constants" %>
<%@ page import="com.liferay.portal.kernel.util.GetterUtil" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.kernel.util.StringPool" %>

<%@ page import="javax.portlet.ActionRequest" %>
<%@ page import="javax.portlet.PortletURL" %>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="aui" uri="http://liferay.com/tld/aui" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
String currentPath = GetterUtil.getString(renderRequest.getParameter(Constants.PARM_CURRENT_PATH), StringPool.SLASH);
String addType = GetterUtil.getString(ParamUtil.getString(request, Constants.PARM_ADD_TYPE), Constants.TYPE_ADD_FILE);
String addMessage = GetterUtil.getString(ParamUtil.getString(request, Constants.PARM_ADD_MESSAGE), Constants.MSG_ADD_FILE);
PortletURL portletURL = renderResponse.createRenderURL();
String currentURL = portletURL.toString();
%>

<portlet:actionURL var="addFileFolderActionURL">
	<portlet:param name="mvcPath" value="/add_file_folder" />
</portlet:actionURL>

<div id="fm">
	<form action="<%= addFileFolderActionURL %>" id="form" method="post" name="form">
		<aui:input name="<%= ActionRequest.ACTION_NAME %>" type="hidden" />
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
		<aui:input name="currentPath" type="hidden" value="<%= currentPath %>" />
		<aui:input name="addType" type="hidden" value="<%= addType %>" />

		<aui:fieldset>
			<aui:input autoFocus="true" helpMessage="add-name-help" label="add-name" name="addName" type="text" />
		</aui:fieldset>

		<aui:button-row>
			<aui:button type="submit" value="<%= addMessage %>" />
			<aui:button name="cancel" type="button" value="cancel" />
		</aui:button-row>
	</form>
</div>

<aui:script use="aui-base">
	A.one('#<portlet:namespace/>cancel').on('click', function(event) {
		var data = '';
		Liferay.Util.getOpener().<portlet:namespace/>closeYourPopUp(data, '<portlet:namespace/>dialog');
	});
</aui:script>