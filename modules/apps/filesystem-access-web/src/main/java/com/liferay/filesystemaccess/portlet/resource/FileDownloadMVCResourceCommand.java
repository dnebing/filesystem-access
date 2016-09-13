package com.liferay.filesystemaccess.portlet.resource;

import com.liferay.filesystemaccess.api.FilesystemAccessService;
import com.liferay.filesystemaccess.api.FilesystemItem;
import com.liferay.filesystemaccess.api.HiddenFilenameFilter;
import com.liferay.filesystemaccess.portlet.FilesystemAccessPortletKeys;
import com.liferay.filesystemaccess.portlet.config.FilesystemAccessDisplayContext;
import com.liferay.filesystemaccess.portlet.config.FilesystemAccessPortletInstanceConfiguration;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactoryUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * class FileDownloadMVCResourceCommand: A resource command class for returning files.
 * @author dnebinger
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + FilesystemAccessPortletKeys.FILESYSTEM_ACCESS,
		"mvc.command.name=/download-file"
	},
	service = MVCResourceCommand.class
)
public class FileDownloadMVCResourceCommand extends BaseMVCResourceCommand {

	/**
	 * doServeDirectory: Zips up the directory contents and returns it.
	 * @param item The file item to download.
	 * @param resourceRequest The resource request.
	 * @param resourceResponse The resource response.
	 * @throws Exception In case of error.
	 */
	protected void doServeDirectory(
			FilesystemItem item, ResourceRequest resourceRequest,
			ResourceResponse resourceResponse)
		throws Exception {

		// create the zip filename

		String zipFileName = getBaseName(item) + ".zip";

		// create a zip writer

		ZipWriter zipWriter = ZipWriterFactoryUtil.getZipWriter();

		// our item is our guy...

		File target = item.getFile();

		// zip up the file or the folder

		if (target.isDirectory()) {
			zipFolder(target, "/", zipWriter);
		}
		else {
			zipFile(target, "/", zipWriter);
		}

		// get the zip file

		File file = zipWriter.getFile();

		// get an input stream to the zip file

		InputStream inputStream = new FileInputStream(file);

		// send the file back to the browser.

		PortletResponseUtil.sendFile(
			resourceRequest, resourceResponse, zipFileName, inputStream,
			ContentTypes.APPLICATION_ZIP);

		// should probably delete the file

	}

	/**
	 * doServeFile: Serves up a single file.
	 * @param item The file item to download.
	 * @param resourceRequest The resource request.
	 * @param resourceResponse The resource response.
	 */
	protected void doServeFile(
		FilesystemItem item, ResourceRequest resourceRequest,
		ResourceResponse resourceResponse) {

		// send the file as-is

		try {
			PortletResponseUtil.sendFile(
				resourceRequest, resourceResponse, item.getName(),
				item.getDataStream(), 0, item.getMimeType(),
				HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT);
		}
		catch (IOException ioe) {
			logger.error("Error sending file: " + ioe.getMessage(), ioe);

			SessionErrors.add(resourceRequest, "downloadError");
		}
	}

	/**
	 * doServeResource: Handles the file download via resource request.
	 * @param resourceRequest The resource request.
	 * @param resourceResponse The resource response.
	 * @throws Exception in case of error.
	 */
	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		FilesystemAccessDisplayContext displayContext =
			new FilesystemAccessDisplayContext(resourceRequest);

		FilesystemAccessPortletInstanceConfiguration config =
			displayContext.getFilesystemAccessPortletInstanceConfiguration();

		// make sure downloads are allowed

		if ((config == null) || (! config.downloadsAllowed())) {
			logger.warn("User tried a download but downloads are disabled.");
			SessionErrors.add(resourceRequest, "downloadsDisabled");
			return;
		}

		// okay, get the target

		String target = GetterUtil.getString(
			ParamUtil.getString(resourceRequest, "target"));

		// verify that we have the target

		if (Validator.isNull(target)) {
			logger.warn("No target provided.");

			SessionErrors.add(resourceRequest, "noDownloadTargetProvided");

			return;
		}

		// okay, we're here, let's get the file item

		User user = PortalUtil.getUser(resourceRequest);

		FilesystemItem item = _filesystemAccessService.getFilesystemItem(
			config.rootPath(), target, user.getTimeZone());

		if (item.isDirectory()) {
			doServeDirectory(item, resourceRequest, resourceResponse);
		}
		else {
			doServeFile(item, resourceRequest, resourceResponse);
		}
	}

	/**
	 * getBasename: Utility method to get the base name from an item.
	 * @param item The item to get the base name for.
	 * @return String The base name.
	 */
	protected String getBaseName(final FilesystemItem item) {
		String name = item.getName();

		int pos = name.lastIndexOf('.');

		if (pos < 0) {
			return name;
		}

		return name.substring(0, pos);
	}

	@Reference
	protected void setFilesystemAccessService(
		final FilesystemAccessService filesystemAccessService) {

		_filesystemAccessService = filesystemAccessService;
	}

	/**
	 * zipFile: Adds the given file into the zip file.
	 * @param target The target file to add.
	 * @param path The path for the file in the zip.
	 * @param zipWriter The zip writer used to create the zip.
	 * @throws IOException in case of error.
	 */
	protected void zipFile(File target, String path, ZipWriter zipWriter)
		throws IOException {

		try {
			zipWriter.addEntry(
				path + StringPool.SLASH + target.getName(),
				new FileInputStream(target));
		}
		catch (IOException ioe) {
			logger.error("Error adding [" + target.getAbsolutePath() +
				"] to zip: " + ioe.getMessage(), ioe);

			throw ioe;
		}
	}

	/**
	 * zipFolder: Includes the folder contents in the zip file.
	 * @param target The target folder to add.
	 * @param path The path for the folder in the zip.
	 * @param zipWriter The zip writer used to create the zip.
	 * @throws IOException in case of error.
	 */
	protected void zipFolder(File target, String path, ZipWriter zipWriter)
		throws IOException {

		File[] children = target.listFiles(
			HiddenFilenameFilter.HIDDEN_FILENAME_FILTER);

		if ((children == null) || (children.length < 1)) {
			return;
		}

		String newPath = path + '/' + target.getName();

		for (File child : children) {
			if (child.isDirectory()) {
				zipFolder(child, newPath, zipWriter);
			}
			else {
				zipFile(child, newPath, zipWriter);
			}
		}
	}

	private static final Log logger = LogFactoryUtil.getLog(
		FileDownloadMVCResourceCommand.class);

	private FilesystemAccessService _filesystemAccessService;

}