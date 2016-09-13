package com.liferay.filesystemaccess.portlet.action;

import com.liferay.filesystemaccess.api.FilesystemAccessService;
import com.liferay.filesystemaccess.portlet.Constants;
import com.liferay.filesystemaccess.portlet.FilesystemAccessPortletKeys;
import com.liferay.filesystemaccess.portlet.MissingConfigurationException;
import com.liferay.filesystemaccess.portlet.config.FilesystemAccessPortletInstanceConfiguration;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * class TouchFileFolderMVCActionCommand: Action command that handles the 'touch' of the file/folder.
 * @author dnebinger
 */
@Component(
	configurationPid = "com.liferay.filesystemaccess.portlet.config.FilesystemAccessPortletInstanceConfiguration",
	immediate = true,
	property = {
		"javax.portlet.name=" + FilesystemAccessPortletKeys.FILESYSTEM_ACCESS,
		"mvc.command.name=/touch_file_folder"
	},
	service = MVCActionCommand.class
)
public class TouchFileFolderMVCActionCommand extends BaseActionCommand {

	/**
	 * doProcessAction: Called to handle the touch file/folder action.
	 * @param actionRequest Request instance.
	 * @param actionResponse Response instance.
	 * @throws Exception in case of error.
	 */
	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		// get the config instance

		FilesystemAccessPortletInstanceConfiguration config = getConfiguration(
			actionRequest);

		if (config == null) {
			logger.warn("No config found.");

			SessionErrors.add(
				actionRequest, MissingConfigurationException.class.getName());

			return;
		}

		// Extract the target and current path from the action request params.

		String touchName = ParamUtil.getString(
			actionRequest, Constants.PARM_TARGET);
		String currentPath = ParamUtil.getString(
			actionRequest, Constants.PARM_CURRENT_PATH);

		// get the real target path to use for the service call

		String target = getLocalTargetPath(currentPath, touchName);

		// use the service to touch the item.

		_filesystemAccessService.touchFilesystemItem(config.rootPath(), target);
	}

	/**
	 * setConfigurationProvider: Sets the configuration provider for config access.
	 * @param configurationProvider The config provider to use.
	 */
	@Reference
	protected void setConfigurationProvider(
		ConfigurationProvider configurationProvider) {

		_configurationProvider = configurationProvider;
	}

	/**
	 * setFilesystemAccessService: Sets the filesystem access service instance to use.
	 * @param filesystemAccessService The filesystem access service instance.
	 */
	@Reference(unbind = "-")
	protected void setFilesystemAccessService(
		final FilesystemAccessService filesystemAccessService) {

		_filesystemAccessService = filesystemAccessService;
	}

	private static final Log logger = LogFactoryUtil.getLog(
		TouchFileFolderMVCActionCommand.class);

}