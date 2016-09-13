
<%@ include file="/init.jsp" %>

<%
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

FilesystemItem file = null;

if (row != null) {
	file = (FilesystemItem)row.getObject();
}
else {
	file = (FilesystemItem)request.getAttribute(Constants.ATTRIB_EDIT_FILE);
}

boolean showDownload = allowDownloads;
boolean showView = !file.isDirectory();

if (showView && Validator.isNull(file.getEditorMode())) {
	showView = false;
}

if (showDownload) {
	long fsize = maxDownloadFolderSize;
	int fitems = maxDownloadFolderItems;

	_filesystemAccessService.updateDownloadable(file, fsize, fitems);

	showDownload = file.isDownloadable();
}

boolean canEdit = ((! file.isDirectory()) && allowEditFile);
%>

<portlet:renderURL var="redirectURL">
	<portlet:param name="mvcPath" value="/view.jsp" />
</portlet:renderURL>

<liferay-ui:icon-menu icon="<%= StringPool.BLANK %>" message="<%= StringPool.BLANK %>" showExpanded="<%= row == null %>" showWhenSingleIcon="true">
	<c:if test="<%= showView %>">
		<portlet:renderURL var="viewFile">
			<portlet:param name="mvcPath" value="/view_file.jsp" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="currentPath" value="<%= currentPath %>" />
			<portlet:param name="target" value="<%= file.getName() %>" />
		</portlet:renderURL>

		<liferay-ui:icon
			iconCssClass="icon-eye-open"
			message="view"
			url="<%= viewFile %>"
		/>
	</c:if>

	<c:if test="<%= showDownload %>">
		<portlet:resourceURL id="/download-file" var="downloadURL">
			<portlet:param name="target" value="<%= file.getTargetLocalPath() %>" />
		</portlet:resourceURL>

		<liferay-ui:icon iconCssClass="icon-download" message="download" method="get" url="<%= downloadURL %>" />
	</c:if>

	<c:if test="<%= canEdit %>">
		<portlet:renderURL var="editURL">
			<portlet:param name="mvcPath" value="/edit_file.jsp" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="currentPath" value="<%= currentPath %>" />
			<portlet:param name="target" value="<%= file.getName() %>" />
		</portlet:renderURL>

		<liferay-ui:icon
			iconCssClass="icon-edit"
			message="edit"
			url="<%= editURL %>"
		/>
	</c:if>

	<portlet:actionURL name="/touch_file_folder" var="touchURL">
		<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.TOUCH %>" />
		<portlet:param name="redirect" value="<%= (row == null) ? redirectURL : currentURL %>" />
		<portlet:param name="currentPath" value="<%= currentPath %>" />
		<portlet:param name="target" value="<%= file.getName() %>" />
	</portlet:actionURL>

	<liferay-ui:icon iconCssClass="icon-hand-down" message="touch" url="<%= touchURL %>" />

	<c:if test="<%= allowDeleteFileFolder %>">
		<portlet:actionURL name="/delete_file_folder" var="deleteURL">
			<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.DELETE %>" />
			<portlet:param name="redirect" value="<%= (row == null) ? redirectURL : currentURL %>" />
			<portlet:param name="currentPath" value="<%= currentPath %>" />
			<portlet:param name="target" value="<%= file.getName() %>" />
		</portlet:actionURL>

		<liferay-ui:icon iconCssClass="icon-trash" message="delete" url="<%= deleteURL %>" />
	</c:if>
</liferay-ui:icon-menu>