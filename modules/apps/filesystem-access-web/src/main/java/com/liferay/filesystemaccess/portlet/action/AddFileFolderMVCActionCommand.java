package com.liferay.filesystemaccess.portlet.action;

import com.liferay.filesystemaccess.api.CreateFilesystemItemException;
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
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;

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
		"mvc.command.name=/add_file_folder"
	},
	service = MVCActionCommand.class
)
public class AddFileFolderMVCActionCommand extends BaseActionCommand {

	/**
	 * doProcessAction: Called to handle the add file/folder action.
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

		if (! config.addsAllowed()) {
			logger.warn("Adds are not authorized but user tried to add.");

			SessionErrors.add(
				actionRequest, OperationNotAllowedException.class.getName());

			return;
		}

		String addType = ParamUtil.getString(
			actionRequest, Constants.PARM_ADD_TYPE);
		String addName = ParamUtil.getString(
			actionRequest, Constants.PARM_ADD_NAME);
		String currentPath = ParamUtil.getString(
			actionRequest, Constants.PARM_CURRENT_PATH);
		boolean dir = false;

		if (Validator.isNotNull(addType) &&
			Constants.TYPE_ADD_FOLDER.equals(addType)) {

			dir = true;
		}

		// verify that we have the name

		if (Validator.isNull(addName) ||
			addName.trim().startsWith(StringPool.PERIOD)) {

			logger.warn("No name provided.");

			SessionErrors.add(
				actionRequest, CreateFilesystemItemException.class.getName());

			return;
		}

		addName = addName.trim();

		if (logger.isDebugEnabled()) {
			logger.debug(
				"User wants to add " + addType + " named " + addName + " to " +
					currentPath);
		}

		_filesystemAccessService.createFilesystemItem(
			config.rootPath(), currentPath, addName, dir);
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
		AddFileFolderMVCActionCommand.class);

}