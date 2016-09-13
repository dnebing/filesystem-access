
<%@ include file="/init.jsp" %>

<%
String addsAllowed = String.valueOf(allowAddFileFolder);
String deletesAllowed = String.valueOf(allowDeleteFileFolder);
String editsAllowed = String.valueOf(allowEditFile);
String showPerms = String.valueOf(showPermissions);
String uploadsAllowed = String.valueOf(allowUploads);
String downloadsAllowed = String.valueOf(allowDownloads);
String viewSizeLimitStr = String.valueOf(viewSizeLimit);
String downloadableFolderSizeLimit = String.valueOf(maxDownloadFolderItems);
String downloadableFolderItemLimit = String.valueOf(maxDownloadFolderSize);
%>

<liferay-portlet:actionURL portletConfiguration="<%= true %>"
	var="configurationActionURL"
/>

<liferay-portlet:renderURL portletConfiguration="<%= true %>"
	var="configurationRenderURL"
/>

<aui:form action="<%= configurationActionURL %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden"
		value="<%= Constants.UPDATE %>"
	/>

	<aui:input name="redirect" type="hidden"
		value="<%= configurationRenderURL %>"
	/>

	<aui:fieldset>
		<aui:input autoFocus="true" helpMessage="root-path-help" label="root-path" name="rootPath" type="text" value="<%= String.valueOf(filesystemAccessPortletInstanceConfiguration.rootPath()) %>" />

		<aui:input helpMessage="adds-allowed-help" label="adds-allowed" name="addsAllowed" type="checkbox" value="<%= String.valueOf(filesystemAccessPortletInstanceConfiguration.addsAllowed()) %>" />

		<aui:input helpMessage="edits-allowed-help" label="edits-allowed" name="editsAllowed" type="checkbox" value="<%= String.valueOf(filesystemAccessPortletInstanceConfiguration.editsAllowed()) %>" />

		<aui:input helpMessage="deletes-allowed-help" label="deletes-allowed" name="deletesAllowed" type="checkbox" value="<%= String.valueOf(filesystemAccessPortletInstanceConfiguration.deletesAllowed()) %>" />

		<aui:input helpMessage="uploads-allowed-help" label="uploads-allowed" name="uploadsAllowed" type="checkbox" value="<%= String.valueOf(filesystemAccessPortletInstanceConfiguration.uploadsAllowed()) %>" />

		<aui:input helpMessage="downloads-allowed-help" label="downloads-allowed" name="downloadsAllowed" type="checkbox" value="<%= String.valueOf(filesystemAccessPortletInstanceConfiguration.downloadsAllowed()) %>" />

		<aui:input helpMessage="show-permissions-help" label="show-permissions" name="showPermissions" type="checkbox" value="<%= String.valueOf(filesystemAccessPortletInstanceConfiguration.showPermissions()) %>" />

		<aui:input helpMessage="view-size-limit-help" label="view-size-limit" name="viewSizeLimit" type="text" value="<%= String.valueOf(filesystemAccessPortletInstanceConfiguration.viewSizeLimit()) %>" />

		<aui:input helpMessage="downloadable-folder-size-limit-help" label="downloadable-folder-size-limit" name="downloadableFolderSizeLimit" type="text" value="<%= String.valueOf(filesystemAccessPortletInstanceConfiguration.downloadableFolderSizeLimit()) %>" />

		<aui:input helpMessage="downloadable-folder-item-limit-help" label="downloadable-folder-item-limit" name="downloadableFolderItemLimit" type="text" value="<%= String.valueOf(filesystemAccessPortletInstanceConfiguration.downloadableFolderItemLimit()) %>" />
	</aui:fieldset>

	<aui:button-row>
		<aui:button type="submit"></aui:button>
	</aui:button-row>
</aui:form>