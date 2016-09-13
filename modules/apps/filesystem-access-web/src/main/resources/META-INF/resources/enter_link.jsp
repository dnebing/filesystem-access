
<%@ include file="/init.jsp" %>

<%
ResultRow row = (ResultRow) request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

FilesystemItem file = null;

if (row != null) {
	file = (FilesystemItem)row.getObject();
}
else {
	file = (FilesystemItem)request.getAttribute(Constants.ATTRIB_EDIT_FILE);
}

if (file.isDirectory()) {

	// build the icon image link

	StringBuffer sb = new StringBuffer(5);

	sb.append("<img src=\"");
	sb.append(themeDisplay.getPathThemeImages());
	sb.append("/common/folder.png\" style=\"border-width: 0; text-align: left;\">");

	String iconTag = sb.toString();

	String targetPath = file.getTargetLocalPath();
%>

	<portlet:renderURL var="enterDir">
		<portlet:param name="mvcPath" value="/view.jsp" />
		<portlet:param name="redirect" value="<%= currentURL %>" />
		<portlet:param name="currentPath" value="<%= targetPath %>" />
	</portlet:renderURL>

	<aui:a href="<%= enterDir %>"><span class="file-folder-name-display"><%= iconTag %><span class="fname-display"><%= file.getName() %></span></span></aui:a>

<%
}
else {

	// need the extension for the file

	String extension = FileUtil.getExtension(file.getName());

	// now we should be able to get the icon image

	String iconName = DLUtil.getFileIcon(extension);

	// build the icon image link

	StringBuffer sb = new StringBuffer(5);

	sb.append("<img src=\"");
	sb.append(themeDisplay.getPathThemeImages());
	sb.append("/file_system/small/");
	sb.append(iconName);
	sb.append(".png\" style=\"border-width: 0; text-align: left;\">");

	String iconTag = sb.toString();

	// if the file item has a mode, we can use a link to the redirect

	boolean visible = true;

	if ((viewSizeLimit >= 0) && (viewSizeLimit < file.getSize())) {
		visible = false;
	}

	if (! visible) {
		_log.warn("File [" + file.getName() + "] is not visible.");
	}

	if ((visible) && (Validator.isNotNull(file.getEditorMode()))) {

		// we have a mode, should display the view file link

%>

		<portlet:renderURL var="viewFile">
			<portlet:param name="mvcPath" value="/view_file.jsp" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="currentPath" value="<%= currentPath %>" />
			<portlet:param name="target" value="<%= file.getName() %>" />
		</portlet:renderURL>

		<aui:a href="<%= viewFile %>"><span class="file-folder-name-display"><%= iconTag %><span class="fname-display"><%= file.getName() %></span></span></aui:a>

	<%
	}
	else {
	%>

		<span class="file-folder-name-display"><%= iconTag %><span class="fname-display"><%= file.getName() %></span></span>

<%
	}
}
%>

<%!
private static Log _log = LogFactoryUtil.getLog("com.liferay.filesystemaccess.portlet.enter_link_jsp");
%>