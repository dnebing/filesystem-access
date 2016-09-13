<%@ page import="com.liferay.filesystemaccess.portlet.Constants" %>
<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ page import="com.liferay.portal.kernel.util.GetterUtil" %>
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
PortletURL portletURL = renderResponse.createRenderURL();
String currentURL = portletURL.toString();
%>

<portlet:actionURL name="uploadFile" var="uploadFileActionURL">
	<portlet:param name="mvcPath" value="/upload_file" />
</portlet:actionURL>

<liferay-ui:error key="error.could-not-upload-file" message="error.could-not-upload-file" />

<liferay-ui:success key="success.file-x-uploaded-successfully" message='<%= LanguageUtil.format(request, "success.file-x-uploaded-successfully", request.getAttribute("title")) %>' />

<aui:form action="<%= uploadFileActionURL %>" cssClass="container lfr-dynamic-form mtm" enctype="multipart/form-data" id="uploadFileForm" method="post" name="uploadFileForm">
	<aui:input name="<%= ActionRequest.ACTION_NAME %>" type="hidden" />
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="currentPath" type="hidden" value="<%= currentPath %>" />
	<aui:input name="title" type="hidden" />

	<aui:fieldset-group markupView="lexicon">
		<aui:fieldset cssClass="pa-md">
			<aui:input label="file" name="file" type="file" />
		</aui:fieldset>
	</aui:fieldset-group>

	<aui:button-row>
		<aui:button type="submit" value="upload" />
		<aui:button name="cancelUpload" type="button" value="cancel" />
	</aui:button-row>
</aui:form>

<aui:script use="aui-base,aui-io-request">
	var fileSelect = A.one('#<portlet:namespace />file');
	var title = A.one('#<portlet:namespace />title');

	fileSelect.on(
		'change',
		function(event) {
			var ct = event.currentTarget;
			var val = ct.val();

			if (val.includes('C:\\fakepath\\')) {
				val = val.replace(/C:\\fakepath\\/, '');
			}

			title.attr('value', val);
		}
	);

	A.one('#<portlet:namespace/>cancelUpload').on('click', function(event) {
		var data = '';
		Liferay.Util.getOpener().<portlet:namespace/>closeYourPopUp(data, '<portlet:namespace/>dialog');
	});

</aui:script>