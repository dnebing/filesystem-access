package com.liferay.filesystemaccess.portlet.action;

import com.liferay.filesystemaccess.api.FilesystemAccessService;
import com.liferay.filesystemaccess.portlet.FilesystemAccessPortletKeys;
import com.liferay.filesystemaccess.portlet.config.FilesystemAccessPortletInstanceConfiguration;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.PortletInstance;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.ActionRequest;

/**
 * class BaseActionCommand: This is a base class to provide access to the
 * configurtion object.
 * @author dnebinger
 */
public abstract class BaseActionCommand extends BaseMVCActionCommand {

	/**
	 * getConfiguration: Returns the configuration instance given the action request.
	 * @param request The request to get the config object from.
	 * @return FilesystemAccessPortletInstanceConfiguration The config instance.
	 */
	protected FilesystemAccessPortletInstanceConfiguration getConfiguration(
		ActionRequest request) {

		// Get the theme display object from the request attributes

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		// get the current portlet instance

		PortletInstance instance = PortletInstance.fromPortletInstanceKey(
			FilesystemAccessPortletKeys.FILESYSTEM_ACCESS);

		FilesystemAccessPortletInstanceConfiguration config = null;

		// use the configuration provider to get the configuration instance

		try {
			config = _configurationProvider.getPortletInstanceConfiguration(
				FilesystemAccessPortletInstanceConfiguration.class,
				themeDisplay.getLayout(), instance);
		}
		catch (ConfigurationException ce) {
			logger.error("Error getting instance config.", ce);
		}

		return config;
	}

	/**
	 * getLocalTargetPath: Returns the local target path.
	 * @param localPath The local path.
	 * @param target The target filename.
	 * @return String The local target path.
	 */
	protected String getLocalTargetPath(
		final String localPath, final String target) {

		if (Validator.isNull(target)) {
			return null;
		}

		if (Validator.isNull(localPath)) {
			return StringPool.SLASH + target;
		}

		if (localPath.trim().endsWith(StringPool.SLASH)) {
			return localPath.trim() + target.trim();
		}

		return localPath.trim() + StringPool.SLASH + target.trim();
	}

	protected ConfigurationProvider _configurationProvider;
	protected FilesystemAccessService _filesystemAccessService;

	private static final Log logger = LogFactoryUtil.getLog(
		BaseActionCommand.class);

}