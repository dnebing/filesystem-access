<%@ page import="java.io.IOException" %>
<%@ page import="java.util.LinkedList" %>
<%@ page import="java.util.List" %>

<%@ page import="javax.portlet.ActionRequest" %>
<%@ page import="javax.portlet.PortletURL" %>

<%@ page import="com.liferay.expando.kernel.exception.DuplicateColumnNameException" %>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>
<%@ page import="com.liferay.portal.kernel.log.LogFactoryUtil" %>
<%@ page import="com.liferay.portal.kernel.log.Log" %>

<%@ page import="com.liferay.portal.kernel.util.GetterUtil" %>
<%@ page import="com.liferay.portal.kernel.util.StringPool" %>
<%@ page import="com.liferay.portal.kernel.util.Validator" %>
<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>

<%@ page import="com.liferay.document.library.kernel.util.DLUtil" %>
<%@ page import="com.liferay.portal.kernel.dao.search.ResultRow" %>
<%@ page import="com.liferay.portal.kernel.util.WebKeys" %>
<%@ page import="com.liferay.portal.kernel.util.FileUtil" %>


<%@ page import="com.liferay.filesystemaccess.portlet.config.FilesystemAccessPortletInstanceConfiguration" %>
<%@ page import="com.liferay.filesystemaccess.portlet.config.FilesystemAccessDisplayContext" %>
<%@ page import="com.liferay.filesystemaccess.api.FilesystemAccessService" %>
<%@ page import="com.liferay.filesystemaccess.api.FilesystemItem" %>
<%@ page import="com.liferay.filesystemaccess.api.CreateFilesystemItemException" %>
<%@ page import="com.liferay.filesystemaccess.api.DeleteFilesystemItemException" %>
<%@ page import="com.liferay.filesystemaccess.portlet.Constants" %>
<%@ page import="com.liferay.filesystemaccess.util.FilenameUtil" %>
<%@ page import="com.liferay.filesystemaccess.portlet.OperationNotAllowedException" %>
<%@ page import="com.liferay.filesystemaccess.api.DuplicateFileException" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
	FilesystemAccessDisplayContext filesystemAccessDisplayContext = new FilesystemAccessDisplayContext(renderRequest);

	FilesystemAccessPortletInstanceConfiguration filesystemAccessPortletInstanceConfiguration = filesystemAccessDisplayContext.getFilesystemAccessPortletInstanceConfiguration();

	String rootPath = StringPool.BLANK;

	String currentPath = GetterUtil.getString(renderRequest.getParameter(Constants.PARM_CURRENT_PATH), StringPool.SLASH);
	String nextPath = renderRequest.getParameter(Constants.PARM_NEXT_PATH);
	boolean showPermissions = false;
	boolean allowDownloads = true;

	long viewSizeLimit = 0;
	long maxDownloadFolderSize = 0;
	int maxDownloadFolderItems = 0;
	boolean allowAddFileFolder = false;
	boolean allowEditFile = false;
	boolean allowDeleteFileFolder = false;
	boolean allowUploads = false;

	FilesystemAccessService _filesystemAccessService = (FilesystemAccessService) renderRequest.getAttribute(Constants.ATTRIB_FILESYSTEM_SERVICE);

	if (Validator.isNotNull(nextPath)) {

		// we are trying to navigate to a different path.

		currentPath = nextPath;
	}

	// use the new configuration api.

	if (Validator.isNotNull(filesystemAccessPortletInstanceConfiguration)) {
		allowAddFileFolder = filesystemAccessPortletInstanceConfiguration.addsAllowed();
		allowDeleteFileFolder = filesystemAccessPortletInstanceConfiguration.deletesAllowed();
		allowEditFile = filesystemAccessPortletInstanceConfiguration.editsAllowed();
		showPermissions = filesystemAccessPortletInstanceConfiguration.showPermissions();
		allowUploads = filesystemAccessPortletInstanceConfiguration.uploadsAllowed();
		allowDownloads = filesystemAccessPortletInstanceConfiguration.downloadsAllowed();
		viewSizeLimit = filesystemAccessPortletInstanceConfiguration.viewSizeLimit();
		maxDownloadFolderItems = filesystemAccessPortletInstanceConfiguration.downloadableFolderItemLimit();
		maxDownloadFolderSize = filesystemAccessPortletInstanceConfiguration.downloadableFolderSizeLimit();

		rootPath = filesystemAccessPortletInstanceConfiguration.rootPath();
	}

	if (Validator.isNull(rootPath)) {
		rootPath = "${LIFERAY_HOME}";
	}

	PortletURL portletURL = renderResponse.createRenderURL();
	String currentURL = portletURL.toString();
%>
