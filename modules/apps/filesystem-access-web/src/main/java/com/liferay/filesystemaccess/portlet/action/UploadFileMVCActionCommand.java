package com.liferay.filesystemaccess.portlet.action;

import com.liferay.filesystemaccess.api.FilesystemAccessService;
import com.liferay.filesystemaccess.portlet.Constants;
import com.liferay.filesystemaccess.portlet.FilesystemAccessPortletKeys;
import com.liferay.filesystemaccess.portlet.MissingConfigurationException;
import com.liferay.filesystemaccess.portlet.OperationNotAllowedException;
import com.liferay.filesystemaccess.portlet.config.FilesystemAccessPortletInstanceConfiguration;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import java.io.File;

import java.util.TimeZone;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * class UploadFileMVCActionCommand: This is the action command that handles file uploads.
 * @author dnebinger
 */
@Component(
	configurationPid = "com.liferay.filesystemaccess.portlet.config.FilesystemAccessPortletInstanceConfiguration",
	immediate = true,
	property = {
		"javax.portlet.name=" + FilesystemAccessPortletKeys.FILESYSTEM_ACCESS,
		"mvc.command.name=/upload_file"
	},
	service = MVCActionCommand.class
)
public class UploadFileMVCActionCommand extends BaseActionCommand {

	/**
	 * doProcessAction: This is called when the user uploads a file.
	 * @param actionRequest The action request.
	 * @param actionResponse The action response.
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

		if (! config.uploadsAllowed()) {
			logger.warn("Uploads are not authorized but user tried to upload.");

			SessionErrors.add(
				actionRequest, OperationNotAllowedException.class.getName());

			return;
		}

		String currentPath = ParamUtil.getString(
			actionRequest, Constants.PARM_CURRENT_PATH);
		String filename = ParamUtil.getString(
			actionRequest, Constants.PARM_UPLOAD_NAME);

		UploadPortletRequest uploadPortletRequest =
			PortalUtil.getUploadPortletRequest(actionRequest);

		File file = uploadPortletRequest.getFile(Constants.PARM_UPLOAD_FILE);

		if (SessionErrors.isEmpty(actionRequest)) {
			TimeZone tz;

			User user = PortalUtil.getUser(actionRequest);

			if (user != null) {
				tz = user.getTimeZone();
			}
			else {
				Company company = PortalUtil.getCompany(actionRequest);

				tz = company.getTimeZone();
			}

			_filesystemAccessService.uploadFile(
				config.rootPath(), currentPath, filename, file, tz);
		}
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