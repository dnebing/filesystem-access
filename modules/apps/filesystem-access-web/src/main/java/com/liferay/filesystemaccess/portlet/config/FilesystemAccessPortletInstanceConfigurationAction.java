package com.liferay.filesystemaccess.portlet.config;

import com.liferay.filesystemaccess.portlet.FilesystemAccessPortletKeys;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.util.ParamUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * class FilesystemAccessConfigurationAction: Configuration action for the filesystem access portlet.
 * @author dnebinger
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + FilesystemAccessPortletKeys.FILESYSTEM_ACCESS
	},
	service = ConfigurationAction.class
)
public class FilesystemAccessPortletInstanceConfigurationAction
	extends DefaultConfigurationAction {

	/**
	 * getJspPath: Return the path to our configuration jsp file.
	 * @param request The servlet request.
	 * @return String The path
	 */
	@Override
	public String getJspPath(HttpServletRequest request) {
		return "/configuration.jsp";
	}

	/**
	 * processAction: This is used to process the configuration form submission.
	 * @param portletConfig The portlet configuration.
	 * @param actionRequest The action request.
	 * @param actionResponse The action response.
	 * @throws Exception in case of error.
	 */
	@Override
	public void processAction(
		PortletConfig portletConfig, ActionRequest actionRequest,
		ActionResponse actionResponse)
		throws Exception {

		// extract all of the parameters from the request.
		String rootPath = ParamUtil.getString(actionRequest, "rootPath");
		String showPerms = ParamUtil.getString(actionRequest, "showPermissions");
		String deletesAllowed = ParamUtil.getString(
			actionRequest, "deletesAllowed");
		String uploadsAllowed = ParamUtil.getString(
			actionRequest, "uploadsAllowed");
		String downloadsAllowed = ParamUtil.getString(
			actionRequest, "downloadsAllowed");
		String editsAllowed = ParamUtil.getString(
			actionRequest, "editsAllowed");
		String addsAllowed = ParamUtil.getString(actionRequest, "addsAllowed");
		String viewSizeLimit = ParamUtil.getString(
			actionRequest, "viewSizeLimit");
		String downloadableFolderSizeLimit = ParamUtil.getString(
			actionRequest, "downloadableFolderSizeLimit");
		String downloadableFolderItemLimit = ParamUtil.getString(
			actionRequest, "downloadableFolderItemLimit");

		// lets log them for fun and giggles
		if (_log.isDebugEnabled()) {
			_log.debug("Configuration setting:");
			_log.debug("  rootPath = [" + rootPath + "]");
			_log.debug("  showPerms = " + showPerms);
			_log.debug("  deletesAllowed = " + deletesAllowed);
			_log.debug("  uploadsAllowed = " + uploadsAllowed);
			_log.debug("  editsAllowed = " + editsAllowed);
			_log.debug("  addsAllowed = " + addsAllowed);
			_log.debug("  viewSizeLimit = " + viewSizeLimit);
		}

		// Set the preference values
		setPreference(actionRequest, "rootPath", rootPath);
		setPreference(actionRequest, "showPermissions", showPerms);
		setPreference(actionRequest, "deletesAllowed", deletesAllowed);
		setPreference(actionRequest, "uploadsAllowed", uploadsAllowed);
		setPreference(actionRequest, "editsAllowed", editsAllowed);
		setPreference(actionRequest, "addsAllowed", addsAllowed);
		setPreference(actionRequest, "viewSizeLimit", viewSizeLimit);
		setPreference(
			actionRequest, "downloadableFolderSizeLimit",
			downloadableFolderSizeLimit);
		setPreference(
			actionRequest, "downloadableFolderItemLimit",
			downloadableFolderItemLimit);
		setPreference(actionRequest, "downloadsAllowed", downloadsAllowed);

		// fall through to the super action for the rest of the handling
		super.processAction(portletConfig, actionRequest, actionResponse);
	}

	/**
	 * setServletContext: Sets the servlet context, use your portlet's bnd.bnd Bundle-SymbolicName value.
	 * @param servletContext The servlet context to use.
	 */
	@Override
	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.filesystemaccess.web)", unbind = "-"
	)
	public void setServletContext(ServletContext servletContext) {
		super.setServletContext(servletContext);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FilesystemAccessPortletInstanceConfigurationAction.class);

}