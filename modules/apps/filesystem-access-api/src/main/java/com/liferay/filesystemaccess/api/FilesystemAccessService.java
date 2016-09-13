package com.liferay.filesystemaccess.api;

import com.liferay.portal.kernel.exception.PortalException;

import java.io.File;

import java.util.List;
import java.util.TimeZone;

/**
 * class FilesystemAccessService: The service interface for the filesystem
 * access.
 *
 * NOTE: The methods below refer to the *root path*.  The portlet will allow
 * an admin to constrain access to a given, fixed path.  This is the root path.
 * Whatever the user attempts, they will always be constrained within the root
 * path.
 *
 * @author dnebinger
 */
public interface FilesystemAccessService {

	/**
	 * countFilesystemItems: Counts the items at the given path.
	 * @param rootPath The root path.
	 * @param localPath The local path to the directory.
	 * @return int The count of items at the given path.
	 */
	public int countFilesystemItems(
		final String rootPath, final String localPath);

	/**
	 * createFilesystemItem: Creates a new filesystem item at the given path.
	 * @param rootPath The root path.
	 * @param localPath The local path for the new item.
	 * @param name The name for the new item.
	 * @param directory Flag indicating create a directory or a file.
	 * @return FilesystemItem The new item.
	 * @throws PortalException in case of error.
	 */
	public FilesystemItem createFilesystemItem(
			final String rootPath, final String localPath, final String name,
			final boolean directory)
		throws PortalException;

	/**
	 * deleteFilesystemItem: Deletes the target item.
	 * @param rootPath String The root path.
	 * @param localPath The local path to the item to delete.
	 * @throws PortalException In case of delete error.
	 */
	public void deleteFilesystemItem(
			final String rootPath, final String localPath)
		throws PortalException;

	/**
	 * getFilesystemItem: Returns the FilesystemItem at the given path.
	 * @param rootPath The root path.
	 * @param localPath The local path to the item.
	 * @param timeZone The time zone for the access.
	 * @return FilesystemItem The found item.
	 */
	public FilesystemItem getFilesystemItem(
		final String rootPath, final String localPath, final TimeZone timeZone);

	/**
	 * getFilesystemItems: Retrieves the list of items at the given path,
	 * includes support for scrolling through the items.
	 * @param rootPath The root path.
	 * @param localPath The local path to the directory.
	 * @param startIdx The starting index of items to return.
	 * @param endIdx The ending index of items to return.
	 * @param timeZone The time zone for the access.
	 * @return List The list of FilesystemItems.
	 */
	public List<FilesystemItem> getFilesystemItems(
		final String rootPath, final String localPath, final int startIdx,
		final int endIdx, final TimeZone timeZone);

	/**
	 * touchFilesystemItem: Touches the target item, updating the modified
	 * timestamp.
	 * @param rootPath The root path.
	 * @param localPath The local path to the item.
	 */
	public void touchFilesystemItem(
		final String rootPath, final String localPath);

	/**
	 * updateContent: Updates the file content.
	 * @param rootPath The root path.
	 * @param localPath The local path to the item to update.
	 * @param content String The new content to write.
	 * @throws PortalException In case of write error.
	 */
	public void updateContent(
			final String rootPath, final String localPath, final String content)
		throws PortalException;

	/**
	 * updateDownloadable: Updates the downloadable flag for the item.  For
	 * directories, they will be downloadable if the total bytes to zip is less
	 * than the given value and contains fiewer than the given number of files.
	 *
	 * These limits allow the administrator to prevent building zip files of
	 * downloads of a directory if building the zip file would overwhelm
	 * available resources.
	 * @param filesystemItem The item to update the downloadable flag on.
	 * @param maxUnzippedSize The max number of bytes to allow for download,
	 *                        for a directory it's the max total unzipped bytes.
	 * @param maxZipFiles The max number of files which can be zipped when
	 *                    checking download of a directory.
	 */
	public void updateDownloadable(
		final FilesystemItem filesystemItem, final long maxUnzippedSize,
		final int maxZipFiles);

	/**
	 * uploadFile: This method handles the upload and store of a file.
	 * @param rootPath The root path.
	 * @param localPath The local path where the file will go.
	 * @param filename The filename for the file.
	 * @param target The uploaded, temporary file.
	 * @param timeZone The user's time zone for modification date display.
	 * @return FilesystemItem The newly loaded filesystem item.
	 * @throws PortalException in case of error.
	 */
	public FilesystemItem uploadFile(
			final String rootPath, final String localPath,
			final String filename, final File target, final TimeZone timeZone)
		throws PortalException;

}