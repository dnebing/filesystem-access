package com.liferay.filesystemaccess.svc.internal;

import com.liferay.filesystemaccess.api.CreateFilesystemItemException;
import com.liferay.filesystemaccess.api.DuplicateFileException;
import com.liferay.filesystemaccess.api.FilesystemAccessService;
import com.liferay.filesystemaccess.api.FilesystemConstants;
import com.liferay.filesystemaccess.api.FilesystemItem;
import com.liferay.filesystemaccess.api.HiddenFilenameFilter;
import com.liferay.filesystemaccess.audit.Attribute;
import com.liferay.filesystemaccess.audit.AuditAttributes;
import com.liferay.filesystemaccess.audit.AuditEventTypes;
import com.liferay.filesystemaccess.audit.AuditMessageBuilder;
import com.liferay.filesystemaccess.util.FilenameUtil;
import com.liferay.portal.kernel.audit.AuditException;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * class FilesystemAccessServiceImpl: Implementation class of the FilesystemAccessService.
 *
 * @author dnebinger
 */
@Component(immediate=true, service = FilesystemAccessService.class)
public class FilesystemAccessServiceImpl implements FilesystemAccessService {

	/**
	 * countFilesystemItems: Counts the items at the given path.
	 *
	 * @param rootPath  The root path.
	 * @param localPath The local path to the directory.
	 * @return int The count of items at the given path.
	 */
	@Override
	public int countFilesystemItems(String rootPath, String localPath) {
		File root = new File(getRealRootPath(rootPath));
		File dir = new File(root, localPath);

		// we only count directories that exist.

		if (! dir.exists()) {
			return 0;
		}

		// we only count what we can read

		if (! dir.canRead()) {
			return 0;
		}

		// we only count in directories.

		if (! dir.isDirectory()) {
			return 1;
		}

		// initialize the count

		int count = 0;

		// list the names of items in the directory

		String[] names = dir.list(HiddenFilenameFilter.HIDDEN_FILENAME_FILTER);

		// if names is not null, grab the count

		if (names != null) {
			count = names.length;
		}

		return count;
	}

	/**
	 * createFilesystemItem: Creates a new filesystem item at the given path.
	 *
	 * @param rootPath  The root path.
	 * @param localPath The local path for the new item.
	 * @param name      The name for the new item.
	 * @param directory Flag indicating create a directory or a file.
	 * @return FilesystemItem The new item.
	 */
	@Override
	public FilesystemItem createFilesystemItem(
			String rootPath, String localPath, String name, boolean directory)
		throws PortalException {

		// get the dir where the item will be created

		File root = new File(getRealRootPath(rootPath));
		File dir = new File(root, localPath);

		// identify the target file

		File target = new File(dir, name);

		// fail if this guy already exists

		if (target.exists()) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"User tried to create [" + name +
						"] but it already exists.");
			}

			throw new DuplicateFileException(
				"Target [" + name + "] already exists.");
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Creating " + target.getAbsolutePath());
		}

		// if we are creating a directory

		AuditMessage am;
		List<Attribute> attribs;

		attribs = new ArrayList<>();
		attribs.add(
			new Attribute(
				AuditAttributes.ATTRIBUTE_ABSOLUTE_PATH,
				target.getAbsolutePath()));
		attribs.add(
			new Attribute(
				AuditAttributes.ATTRIBUTE_LOCAL_PATH, localPath + '/' + name));

		if (directory) {

			// create the directory

			if (!target.mkdir()) {
				_log.error("Failed creating " + target.getAbsolutePath());

				throw new CreateFilesystemItemException(
					"Error creating directory [" + name + "]");
			}

			am = AuditMessageBuilder.buildAuditMessage(
				AuditEventTypes.ADD_DIRECTORY,
				FilesystemAccessService.class.getName(), 0, attribs);
		}
		else {

			// create the file

			try {
				FileUtil.touch(target);

				am = AuditMessageBuilder.buildAuditMessage(
					AuditEventTypes.ADD_FILE,
					FilesystemAccessService.class.getName(), 0, attribs);
			}
			catch (IOException ioe) {
				_log.error("Error creating file [" + name + "]", ioe);

				throw new CreateFilesystemItemException(
					"Error creating [" + name + "]", ioe);
			}
		}

		if ((_auditRouter != null) && (am != null)) {
			_auditRouter.route(am);
		}

		// use the utility method to create the container

		return createItem(target, localPath, null);
	}

	/**
	 * deleteFilesystemItem: Deletes the target item.
	 *
	 * @param rootPath  String The root path.
	 * @param localPath The local path to the item to delete.
	 * @throws PortalException In case of delete error.
	 */
	@Override
	public void deleteFilesystemItem(String rootPath, String localPath)
		throws PortalException {

		// get the target file item

		File root = new File(getRealRootPath(rootPath));
		File target = new File(root, localPath);

		// if it doesn't exist, nothing to delete.

		if (! target.exists()) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"User wants to delete [" + localPath +
						"] but it does not exist.");
			}

			return;
		}

		// delete the file.

		boolean wasDir = target.isDirectory();
		AuditMessage am;
		List<Attribute> attribs;

		attribs = new ArrayList<>();
		attribs.add(
			new Attribute(
				AuditAttributes.ATTRIBUTE_ABSOLUTE_PATH,
				target.getAbsolutePath()));
		attribs.add(
			new Attribute(AuditAttributes.ATTRIBUTE_LOCAL_PATH, localPath));

		if (wasDir) {
			FileUtil.deltree(target);

			am = AuditMessageBuilder.buildAuditMessage(
				AuditEventTypes.DELETE_DIRECTORY,
				FilesystemAccessService.class.getName(), 0, attribs);
		}
		else {
			FileUtil.delete(target);

			am = AuditMessageBuilder.buildAuditMessage(
				AuditEventTypes.DELETE_FILE,
				FilesystemAccessService.class.getName(), 0, attribs);
		}

		if (_auditRouter != null) {
			try {
				_auditRouter.route(am);
			} catch (AuditException ae) {
				if (_log.isInfoEnabled()) {
					_log.info("Error sending audit message.", ae);
				}
			}
		}
	}

	/**
	 * getFilesystemItem: Returns the FilesystemItem at the given path.
	 *
	 * @param rootPath    The root path.
	 * @param localPath   The local path to the item.
	 * @return FilesystemItem The found item.
	 */
	@Override
	public FilesystemItem getFilesystemItem(
		String rootPath, String localPath, final TimeZone timeZone) {

		// find the target item

		File root = new File(getRealRootPath(rootPath));
		File target = new File(root, localPath);

		// if it doesn't exist return null

		if (! target.exists()) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"User wants to load [" + localPath +
						"] but it does not exist.");
			}

			return null;
		}

		// use the utility method to create the container

		return createItem(target, localPath, timeZone);
	}

	/**
	 * getFilesystemItems: Retrieves the list of items at the given path,
	 * includes support for scrolling through the items.
	 *
	 * @param rootPath    The root path.
	 * @param localPath   The local path to the directory.
	 * @param startIdx    The starting index of items to return.
	 * @param endIdx      The ending index of items to return.
	 * @return List The list of FilesystemItems.
	 */
	@Override
	public List<FilesystemItem> getFilesystemItems(
		String rootPath, String localPath, int startIdx, int endIdx,
		final TimeZone timeZone) {

		// get the target that we will be listing from

		File root = new File(getRealRootPath(rootPath));
		File dir = new File(root, localPath);

		// if it doesn't exist or can't be read, return an empty array.

		if ((! dir.exists()) || (! dir.canRead())) {
			return Collections.emptyList();
		}

		List<FilesystemItem> files = null;
		FilesystemItemImpl target;

		// if this is not a directory

		if (! dir.isDirectory()) {

			// get the parent path to the file

			String parentPath = FilenameUtil.getParentPath(localPath);

			// use the utility method to create the container

			target = createItem(dir, parentPath, timeZone);

			// create a list

			files = new ArrayList<>(1);

			// add the item

			if (target != null) {
				files.add(target);
			}

			// return the list
			if (_log.isDebugEnabled()) {
				_log.debug("Returning " + 1 + " item(s).");
			}
			return files;
		}

		// okay, we have a directory.

		int skip, count;

		count = endIdx - startIdx;
		skip = startIdx;

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Retrieving " + count + " filesystem items from " +
					dir.getAbsolutePath());
		}

		// get the list of files in the directory

		File[] children = dir.listFiles(
			HiddenFilenameFilter.HIDDEN_FILENAME_FILTER);

		if (_log.isDebugEnabled()) {
			_log.debug("Have " + children.length + " items.");
		}

		// if there are no files, return an empty array.

		if (children.length == 0) {
			if (_log.isDebugEnabled()) {
				_log.debug("Returning " + 0 + " item(s).");
			}
			return Collections.emptyList();
		}

		// if the length of children is less than the max count to return

		if (children.length <= count) {

			// build a list from the files to return

			files = new ArrayList<>(children.length);

			// for each file in the dir

			for (File f : children) {

				// use the utility method to create the container

				target = createItem(f, localPath, timeZone);

				// add the target to the list

				if (target != null) {
					files.add(target);
				}
			}

			// return the list
			if (_log.isDebugEnabled()) {
				_log.debug("Returning " + files.size() + " item(s).");
			}

			return files;
		}

		// if we get here we have a paging situation, need to return a sublist
		// from the array

		// create the list to hold the results

		files = new ArrayList<>(count);

		// for each file

		for (File f : children) {

			// if we have to skip yet, then continue to next item

			if (skip > 0) {
				skip --;
				continue;
			}

			// if we have added enough items, exit the loop early

			if (count <= 0) {
				break;
			}

			// use the utility method to create a container

			target = createItem(f, localPath, timeZone);

			// add the item to the list

			if (target != null) {

				// decrement the count since we're adding a file

				count--;

				files.add(target);
			}
		}

		// return the list
		if (_log.isDebugEnabled()) {
			_log.debug("Returning " + files.size() + " item(s).");
		}

		return files;
	}

	/**
	 * touchFilesystemItem: Touches the target item, updating the modified
	 * timestamp.
	 *
	 * @param rootPath  The root path.
	 * @param localPath The local path to the item.
	 */
	@Override
	public void touchFilesystemItem(String rootPath, String localPath) {

		// get the target file

		File root = new File(getRealRootPath(rootPath));
		File target = new File(root, localPath);

		// if the target does not exist, nothing to touch.

		if (! target.exists()) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"User tried to touch [" + localPath +
						"] but it does not exist.");
			}

			return;
		}

		// touch the item.

		try {
			FileUtil.touch(target);
		}
		catch (IOException ioe) {
			_log.error("Error touching [" + localPath + "]", ioe);
		}

		if (_auditRouter != null) {
			AuditMessage am;
			List<Attribute> attribs;

			attribs = new ArrayList<>();
			attribs.add(
				new Attribute(
					AuditAttributes.ATTRIBUTE_ABSOLUTE_PATH,
					target.getAbsolutePath()));
			attribs.add(
				new Attribute(AuditAttributes.ATTRIBUTE_LOCAL_PATH, localPath));

			am = AuditMessageBuilder.buildAuditMessage(
				AuditEventTypes.TOUCH, FilesystemAccessService.class.getName(),
				0, attribs);

			try {
				_auditRouter.route(am);
			} catch (AuditException ae) {
				if (_log.isInfoEnabled()) {
					_log.info("Error sending audit message.", ae);
				}
			}
		}
	}

	/**
	 * updateContent: Updates the file content.
	 *
	 * @param rootPath  The root path.
	 * @param localPath The local path to the item to update.
	 * @param content   String The new content to write.
	 * @throws PortalException In case of write error.
	 */
	@Override
	public void updateContent(String rootPath, String localPath, String content)
		throws PortalException {

		// get the target file to update

		File root = new File(getRealRootPath(rootPath));
		File target = new File(root, localPath);

		// if the target does not exist, need to bail

		if ((! target.exists()) || target.isDirectory() ||
			!target.canWrite() || target.isHidden()) {

			if (_log.isWarnEnabled()) {
				_log.warn(
					"User tried to write [" + localPath +
						"] but it does not exist.");
			}

			throw new PortalException(
				new FileNotFoundException(localPath + " does not exist"));
		}

		// write the content to the target file

		try {
			FileUtil.write(target, content);
		} catch (IOException e) {
			throw new PortalException(e);
		}

		if (_auditRouter != null) {

			AuditMessage am;
			List<Attribute> attribs;
			FilesystemItem item = getFilesystemItem(rootPath, localPath, null);
			int length = 0;

			if (content != null) {
				length = content.length();
			}

			attribs = new ArrayList<>();
			attribs.add(
				new Attribute(
					AuditAttributes.ATTRIBUTE_ABSOLUTE_PATH,
					target.getAbsolutePath()));
			attribs.add(
				new Attribute(AuditAttributes.ATTRIBUTE_LOCAL_PATH, localPath));
			attribs.add(
				new Attribute(
					AuditAttributes.ATTRIBUTE_EDITOR_MODE,
					item.getEditorMode()));
			attribs.add(
				new Attribute(
					AuditAttributes.ATTRIBUTE_SIZE, String.valueOf(length)));

			am = AuditMessageBuilder.buildAuditMessage(
				AuditEventTypes.UPDATE_CONTENT,
				FilesystemAccessService.class.getName(), 0, attribs);

			try {
				_auditRouter.route(am);
			} catch (AuditException ae) {
				if (_log.isInfoEnabled()) {
					_log.info("Error sending audit message.", ae);
				}
			}
		}
	}

	/**
	 * updateDownloadable: Updates the downloadable flag for the item.  For
	 * directories, they will be downloadable if the total bytes to zip is less
	 * than the given value and contains fiewer than the given number of files.
	 * <p>
	 * These limits allow the administrator to prevent building zip files of
	 * downloads of a directory if building the zip file would overwhelm
	 * available resources.
	 *
	 * @param filesystemItem  The item to update the downloadable flag on.
	 * @param maxUnzippedSize The max number of bytes to allow for download,
	 *                        for a directory it's the max total unzipped bytes.
	 * @param maxZipFiles     The max number of files which can be zipped when
	 */
	@Override
	public void updateDownloadable(
		FilesystemItem filesystemItem, long maxUnzippedSize, int maxZipFiles) {

		// if not a directory, the update flag is okay where it is.

		if (! filesystemItem.isDirectory()) {
			return;
		}

		// if the sizes are both less than or equal to zero, all downloads
		// allowed

		if ((maxUnzippedSize <= 0) && (maxZipFiles <= 0)) {
			return;
		}

		// if already not downloadable, dont check again.

		if (! filesystemItem.isDownloadable()) {
			return;
		}

		// create a container class to hold all of the info

		DirSizeInfo info = new DirSizeInfo(maxUnzippedSize, maxZipFiles);

		// use the recursive routine to determine if the folder can be
		// downloaded.

		checkDownloadable(filesystemItem.getFile(), info);

		// update the flag

		filesystemItem.setDownloadable(info.downloadable);
	}

	/**
	 * uploadFile: This method handles the upload and store of a file.
	 *
	 * @param rootPath  The root path.
	 * @param localPath The local path where the file will go.
	 * @param filename  The filename for the file.
	 * @param source    The uploaded, temporary file.
	 * @param timeZone  The user's time zone for modification date display.
	 * @return FilesystemItem The newly loaded filesystem item.
	 * @throws PortalException in case of error.
	 */
	@Override
	public FilesystemItem uploadFile(
		String rootPath, String localPath, String filename, File source,
		TimeZone timeZone)
		throws PortalException {
		// if the target does not exist, need to bail
		if ((! source.exists()) || source.isDirectory() ||
			!source.canRead() || source.isHidden()) {

			if (_log.isWarnEnabled()) {
				_log.warn(
					"User tried to upload [" + source.getAbsolutePath() +
						"] but it does not exist.");
			}

			throw new PortalException(
				new FileNotFoundException(
					source.getName() + " does not exist"));
		}

		File root = new File(getRealRootPath(rootPath));
		File targetDir = new File(root, localPath);

		if ((! targetDir.exists()) || !targetDir.isDirectory() ||
			!targetDir.canWrite() || targetDir.isHidden()) {

			if (_log.isWarnEnabled()) {
				_log.warn(
					"User tried to upload [" + source.getAbsolutePath() +
						"] but target dir does not exist.");
			}

			throw new PortalException(
				new FileNotFoundException(localPath + " does not exist"));
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Target filename: " + source.getName());
			_log.debug("Given filename:  " + filename);
		}

		File target = new File(targetDir, filename);
		if (target.exists()) {
			_log.error("User tried to upload [" + source.getAbsolutePath() +
				"] but target file already exists.");

			throw new PortalException(
				"File [" + filename + "] already exists.");
		}

		try {
			FileUtil.copyFile(source, target);
		} catch (IOException e) {
			_log.error(
				"Error copying " + source.getName() + " to " + target.getName(),
				e);

			throw new PortalException(
				"Error copying " + source.getName() + " to " + target.getName(),
				e);
		}

		// file is copied, delete the temp file
		source.delete();

		// don't forget to create the audit message
		if (_auditRouter != null) {
			AuditMessage am;
			List<Attribute> attribs;

			attribs = new ArrayList<>();
			attribs.add(
				new Attribute(
					AuditAttributes.ATTRIBUTE_ABSOLUTE_PATH,
					target.getAbsolutePath()));
			attribs.add(
				new Attribute(
					AuditAttributes.ATTRIBUTE_LOCAL_PATH,
					localPath + '/' + filename));

			am = AuditMessageBuilder.buildAuditMessage(
				AuditEventTypes.UPLOAD_FILE,
				FilesystemAccessService.class.getName(), 0, attribs);

			_auditRouter.route(am);
		}

		return createItem(target, localPath, timeZone);
	}

	/**
	 * updateMode: Utility method to update the code mirror mode for the file.
	 * @param target
	 */
	protected static void updateMode(FilesystemItemImpl target) {

		// get mime type and extension

		String mode = target.getMimeType();
		String ext = target.getExtension();

		// if anything is an octet stream, it is not viewable

		if (StringUtil.contains(mode, "octet") &&
			StringUtil.equalsIgnoreCase(ext, "out")) {

			// binary file, cannot edit/view.

			target.setEditorMode(null);

			return;
		}

		mode = null;

		if (Validator.isNotNull(ext)) {

			// use the extension to determine the editor mode

			mode = MODE_MAP.get(ext);
		}

		// save the editor mode

		target.setEditorMode(mode);
	}

	/**
	 * checkDownloadable: Checks the downloadable flag for the given target.
	 * @param target
	 * @param info
	 * @return boolean
	 */
	protected boolean checkDownloadable(File target, DirSizeInfo info) {

		// if the target is a directory

		if (target.isDirectory()) {

			// list the children

			File[] children = target.listFiles(
				HiddenFilenameFilter.HIDDEN_FILENAME_FILTER);

			// for each child

			for (File child : children) {

				// update the downloadable item for the child

				checkDownloadable(child, info);

				// if we are no longer downloadable, exit early

				if (! info.downloadable) {
					return false;
				}
			}
		}
		else {

			// increment the file count

			info.incItems();

			// if not downloadable, return false.

			if (! info.downloadable) {
				return false;
			}

			// add the file size

			info.incSize(target.length());

			// if not downloadable, return false.

			if (! info.downloadable) {
				return false;
			}
		}

		// return the current flag value.

		return info.downloadable;
	}

	/**
	 * createFilesystemItem: Creates a filesystem item for the given source and path.
	 * @param source
	 * @param path
	 * @return FilesystemItem The new filesystem item container.
	 */
	protected FilesystemItemImpl createItem(
		File source, String path, final TimeZone timeZone) {

		// if source is null return null

		if (source == null) {
			return null;
		}

		// if it does not exist, return null

		if (! source.exists()) {
			return null;
		}

		// create a container instance

		FilesystemItemImpl target = new FilesystemItemImpl(source, path);

		// prefetch the modified display string using the given timezone.
		target.getModifiedDisplay(timeZone);

		// init the download flag

		target.setDownloadable(true);

		// if not a directory

		if (! source.isDirectory()) {

			// update mime type

			target.setMimeType(MimeTypesUtil.getContentType(source.getName()));

			// update editor mode

			updateMode(target);
		}

		// return the container

		return target;
	}

	/**
	 * getRealRootPath: Processes any wildcards stored as part of the root path.
	 * @param rootPath The given path variable
	 * @return String The real root path after processing substitutions.
	 */
	protected String getRealRootPath(String rootPath) {

		// if path is null, default to root

		if (Validator.isNull(rootPath)) {
			return "/";
		}

		// if the path does not contain a variable marker, just return it

		if (! rootPath.contains("${")) {

			// no replacement

			return rootPath.trim();
		}

		// if we get here we have some possible replacements

		// create a map to hold replacement values

		Map<String, String> vals = new HashMap<>();

		// add environment vars as values

		vals.putAll(System.getenv());

		// add the liferay home replacement

		vals.put(
			FilesystemConstants.LIFERAY_HOME,
			PropsUtil.get(PropsKeys.LIFERAY_HOME));

		// add any other replacements here

		// perform the replacements and return the result.

		return StringUtil.replace(rootPath.trim(), "${", "}", vals);
	}

	/**
	 * class DirSizeInfo: Storage class for determining download capability.
	 */
	protected class DirSizeInfo {

		public DirSizeInfo(long ms, int mi) {
			maxSize = ms;
			maxItems = mi;
		}

		public void incItems() {
			if (maxItems <= 0) {
				return;
			}

			curItems ++;

			if (curItems >= maxItems) {
				downloadable = false;
			}
		}

		public void incSize(final long sz) {
			if (maxSize <= 0) {
				return;
			}

			curSize += sz;

			if (curSize >= maxSize) {
				downloadable = false;
			}
		}

		public int curItems;
		public long curSize;
		public boolean downloadable = true;
		public int maxItems;
		public long maxSize;

	}

	/**
	 * MODE_MAP: This is a map of filename extensions and the corresponding
	 * ACE editor mode.
	 */
	private static Map<String, String> MODE_MAP;

	private static final Log _log = LogFactoryUtil.getLog(
		FilesystemAccessServiceImpl.class);

	static {
		MODE_MAP = new HashMap<>();

		MODE_MAP.put("bat", "batchfile");
		MODE_MAP.put("htm", "html");
		MODE_MAP.put("html", "html");
		MODE_MAP.put("js", "javascript");
		MODE_MAP.put("json", "json");
		MODE_MAP.put("log", "plain_text");
		MODE_MAP.put("out", "plain_text");
		MODE_MAP.put("dtd", "plain_text");
		MODE_MAP.put("txt", "text");
		MODE_MAP.put("text", "text");
		MODE_MAP.put("ftl", "ftl");
		MODE_MAP.put("css", "css");
		MODE_MAP.put("sass", "sass");
		MODE_MAP.put("scss", "scss");
		MODE_MAP.put("xml", "xml");
		MODE_MAP.put("sh", "sh");
		MODE_MAP.put("vm", "velocity");
		MODE_MAP.put("properties", "properties");
		MODE_MAP.put("java", "java");
		MODE_MAP.put("jsp", "jsp");
		MODE_MAP.put("less", "less");
	}

	/**
	 * _auditRouter: We need a reference to the audit router, but we mark our
	 * cardinality as optional because LR7CE does not normally have a router
	 * service available.
	 */
	@Reference(
		cardinality = ReferenceCardinality.OPTIONAL,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile AuditRouter _auditRouter;

}