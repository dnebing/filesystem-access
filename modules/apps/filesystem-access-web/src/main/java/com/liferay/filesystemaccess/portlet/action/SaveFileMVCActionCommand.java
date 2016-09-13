package com.liferay.filesystemaccess.portlet.action;

import com.liferay.filesystemaccess.api.FilesystemAccessService;
import com.liferay.filesystemaccess.portlet.Constants;
import com.liferay.filesystemaccess.portlet.FilesystemAccessPortletKeys;
import com.liferay.filesystemaccess.portlet.MissingConfigurationException;
import com.liferay.filesystemaccess.portlet.OperationNotAllowedException;
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
 * class AddFileFolderMVCActionCommand: Action command that handles the addition of the file/folder.
 * @author dnebinger
 */
@Component(
	configurationPid = "com.liferay.filesystemaccess.portlet.config.FilesystemAccessPortletInstanceConfiguration",
	immediate = true,
	property = {
		"javax.portlet.name=" + FilesystemAccessPortletKeys.FILESYSTEM_ACCESS,
		"mvc.command.name=/save_file"
	},
	service = MVCActionCommand.class
)
public class SaveFileMVCActionCommand extends BaseActionCommand {

	/**
	 * doProcessAction: Called to handle the save file action.
	 * @param actionRequest Request instance.
	 * @param actionResponse Response instance.
	 * @throws Exception in case of error.
	 */
	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		FilesystemAccessPortletInstanceConfiguration config = getConfiguration(
			actionRequest);

		if (config == null) {
			logger.warn("No config found.");

			SessionErrors.add(
				actionRequest, MissingConfigurationException.class.getName());

			return;
		}

		if (! config.editsAllowed()) {
			logger.warn("Edits are not authorized but user tried to save.");

			SessionErrors.add(
				actionRequest, OperationNotAllowedException.class.getName());

			return;
		}

		String targetName = ParamUtil.getString(
			actionRequest, Constants.PARM_TARGET);
		String currentPath = ParamUtil.getString(
			actionRequest, Constants.PARM_CURRENT_PATH);

		// we cannot use ParamUtil.getString() because it actually trims the
		// incoming value.  User likely wants all of their trailing and leading
		// whitespace.

		String content = actionRequest.getParameter(Constants.PARM_CONTENT);

		if (content == null) {
			content = "";
		}

		targetName = getLocalTargetPath(currentPath, targetName);

		_filesystemAccessService.updateContent(
			config.rootPath(), targetName, content);
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
		SaveFileMVCActionCommand.class);

}