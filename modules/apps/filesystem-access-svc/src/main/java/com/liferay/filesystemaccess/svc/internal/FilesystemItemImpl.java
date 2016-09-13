package com.liferay.filesystemaccess.svc.internal;

import com.liferay.filesystemaccess.api.FilesystemItem;
import com.liferay.filesystemaccess.api.HiddenFilenameFilter;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

// import org.apache.commons.io.FilenameUtils;

/**
 * class FilesystemItemImpl: Internal implementation of the FilesystemItem
 * container interface.
 * @author dnebinger
 */
public class FilesystemItemImpl implements FilesystemItem {

	/**
	 * FilesystemItemImpl: Constructor for the container.
	 * @param target The target file.
	 * @param localPath The local path for the file.
	 */
	public FilesystemItemImpl(final File target, final String localPath) {
		file = target;
		this.localPath = localPath;

		if (file.isDirectory()) {
			mimeType = MimeTypesUtil.getContentType(file.getName());
		}
	}

	/**
	 * getAbsolutePath: Returns the absolute path for the file.
	 * @return String The absolute path to the file.
	 */
	@Override
	public String getAbsolutePath() {
		return file.getAbsolutePath();
	}

	/**
	 * getData: Returns the data of the file as a byte array.
	 * @return Byte array of data.
	 * @throws IOException in case of error.
	 */
	@Override
	public byte[] getData() throws IOException {
		return FileUtil.getBytes(file);
	}

	/**
	 * getDataStream: Returns the input stream to the file.
	 * @return InputStream The input stream.
	 * @throws FileNotFoundException in case the file is not found.
	 */
	@Override
	public InputStream getDataStream() throws FileNotFoundException {
		return new FileInputStream(file);
	}

	/**
	 * getEditorMode: Returns the editor mode.
	 * @return String The editor mode.
	 */
	@Override
	public String getEditorMode() {
		return editorMode;
	}

	/**
	 * getExtension: Returns the filename extension for the file.
	 * @return String The filename extension.
	 */
	@Override
	public String getExtension() {
		return FileUtil.getExtension(file.getName());
	}

	/**
	 * getFile: Returns the file object.
	 * @return File The file object.
	 */
	@Override
	public File getFile() {
		return file;
	}

	/**
	 * getLocalPath: Returns the local path where the file is.
	 * @return String The local path.
	 */
	@Override
	public String getLocalPath() {
		return localPath;
	}

	/**
	 * getMimeType: Returns the mime type string for the file.
	 * @return String The mime type.
	 */
	@Override
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * getModified: Returns the modified time for the file.
	 * @return Date The modified date.
	 */
	@Override
	public Date getModified() {
		return new Date(file.lastModified());
	}

	/**
	 * getModifiedDisplay: Returns the modified date string.
	 * @param timeZone The time zone to use when displaying the date.
	 * @return String The modified display date.
	 */
	@Override
	public String getModifiedDisplay(final TimeZone timeZone) {
		if (Validator.isNull(timeZone)) {
			return modifiedDisplay;
		}

		DateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

		dateFormatter.setTimeZone(timeZone);

		modifiedDisplay = dateFormatter.format(getModified());

		return modifiedDisplay;
	}

	/**
	 * getModifiedDisplay: Returns the modified date string.
	 * @return String The modified display date.
	 */
	@Override
	public String getModifiedDisplay() {
		return modifiedDisplay;
	}

	/**
	 * getName: Returns the name for the file.
	 * @return String The file name.
	 */
	@Override
	public String getName() {
		return file.getName();
	}

	/**
	 * getPermissionsDisplay: Returns the permission string for the file.
	 * @return String The permissions display string.
	 */
	@Override
	public String getPermissionsDisplay() {

		// if we have already retrieved the perms, then use that value.

		if (Validator.isNotNull(permissions)) {
			return permissions;
		}

		Set<PosixFilePermission> filePerm = null;

		// use the jdk 1.7 support for getting file perms.

		try {
			filePerm = Files.getPosixFilePermissions(
				Paths.get(getAbsolutePath()));
		}
		catch (IOException ioe) {

			// ignored

		}

		// use the jdk 1.7 support to convert the perm map to a display string

		permissions = PosixFilePermissions.toString(filePerm);

		// return the value

		return permissions;
	}

	/**
	 * getSize: Returns the size of the file or count of items in the directory.
	 * @return long The size or file count.
	 */
	@Override
	public long getSize() {

		// if a directory

		if (isDirectory()) {

			// the size is the count of visible files

			String[] files = file.list(
				HiddenFilenameFilter.HIDDEN_FILENAME_FILTER);

			if (Validator.isNull(files)) {
				return 0;
			}

			return files.length;
		}

		// otherwise return the size of the actual file.

		return file.length();
	}

	/**
	 * getSizeDisplay: Returns the size in a human-readable string.
	 * @return String The human readable size string.
	 */
	@Override
	public String getSizeDisplay() {

		// if we have determined this before, use that value.

		if (Validator.isNotNull(sizeDisplay)) {
			return sizeDisplay;
		}

		long sz = getSize();

		// if a directory

		if (isDirectory()) {

			// size is basically a count of items in the directory

			if (sz == 1) {
				sizeDisplay = "1 Item";
			}
			else {
				sizeDisplay = sz + " Items";
			}

			return sizeDisplay;
		}

		// use the utility method to make human readable format.

		sizeDisplay = byteCountToDisplaySize(sz);

		return sizeDisplay;
	}

	/**
	 * getStringData: Returns the string content data from the file.
	 * @return String The content data.
	 * @throws IOException In case of read data.
	 */
	@Override
	public String getStringData() throws IOException {
		return FileUtil.read(file);
	}

	/**
	 * getTargetLocalPath: Returns the local path to the target.
	 * @return String The target local path.
	 */
	@Override
	public String getTargetLocalPath() {
		if (localPath.endsWith("/")) {
			return localPath + getName();
		}

		return localPath + '/' + getName();
	}

	/**
	 * isDirectory: Determines if the item is a directory or not.
	 * @return boolean <code>true</code> if a directory, otherwise not.
	 */
	@Override
	public boolean isDirectory() {
		return file.isDirectory();
	}

	/**
	 * isDownloadable: Determines if the item can be downloaded.
	 * @return boolean <code>true</code> if downloadable, otherwise not.
	 */
	@Override
	public boolean isDownloadable() {
		return downloadable;
	}

	/**
	 * setDownloadable: Sets the downloadable flag.
	 * @param flag The new flag value.
	 */
	public void setDownloadable(final boolean flag) {
		downloadable = flag;
	}

	/**
	 * setEditorMode: Sets the editor mode for the file.
	 * @param mode String The editor mode.
	 */
	public void setEditorMode(final String mode) {
		editorMode = mode;
	}

	/**
	 * setMimeType: Sets the mime type for the file item.
	 * @param mimeType String The mime type.
	 */
	public void setMimeType(final String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * Returns a human-readable version of the file size, where the input
	 * represents a specific number of bytes.
	 *
	 * @param size  the number of bytes
	 * @return a human-readable display value (includes units)
	 */
	protected String byteCountToDisplaySize(long size) {
		String displaySize;

		if ((size / C_ONE_GB) > 0) {
			displaySize = String.valueOf(size / C_ONE_GB) + " GB";
		}
		else if ((size / B_ONE_MB) > 0) {
			displaySize = String.valueOf(size / B_ONE_MB) + " MB";
		}
		else if ((size / A_ONE_KB) > 0) {
			displaySize = String.valueOf(size / A_ONE_KB) + " KB";
		}
		else {
			displaySize = String.valueOf(size) + " bytes";
		}

		return displaySize;
	}

	/**
	 * The number of bytes in a kilobyte.
	 */
	protected static final long A_ONE_KB = 1024;

	/**
	 * The number of bytes in a megabyte.
	 */
	protected static final long B_ONE_MB = A_ONE_KB * A_ONE_KB;

	/**
	 * The number of bytes in a gigabyte.
	 */
	protected static final long C_ONE_GB = A_ONE_KB * B_ONE_MB;

	private static final long serialVersionUID = -5202393002417879981L;

	private boolean downloadable = true;
	private String editorMode;
	private File file;
	private String localPath;
	private String mimeType;
	private String permissions = null;
	private String sizeDisplay = null;
	private String modifiedDisplay = null;
}