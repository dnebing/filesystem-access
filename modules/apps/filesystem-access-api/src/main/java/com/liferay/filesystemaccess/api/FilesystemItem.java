package com.liferay.filesystemaccess.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.util.Date;
import java.util.TimeZone;

/**
 * class FilesystemItem: This is a container for an individual filesystem item.
 *
 * @author dnebinger
 */
public interface FilesystemItem extends Serializable {

	/**
	 * getAbsolutePath: Returns the filesystem absolute path to the item.
	 * @return String The absolute path.
	 */
	public String getAbsolutePath();

	/**
	 * getData: Returns the byte data from the file.
	 * @return byte array of file data.
	 * @throws IOException in case of read failure.
	 */
	public byte[] getData() throws IOException;

	/**
	 * getDataStream: Returns an InputStream for the file data.
	 * @return InputStream The data input stream.
	 * @throws FileNotFoundException in case of read error.
	 */
	public InputStream getDataStream() throws FileNotFoundException;

	/**
	 * getEditorMode: Returns the editor mode, basically used to determine
	 * the language for the syntax aware editor.
	 * @return String The editor mode.
	 */
	public String getEditorMode();

	/**
	 * getExtension: Returns the filename extension.
	 * @return String The extension.
	 */
	public String getExtension();

	/**
	 * getFile: Returns the File this item refers to.
	 * @return File The item file.
	 */
	public File getFile();

	/**
	 * getLocalPath: Returns the shortened local path for the item.
	 * @return String The short local path.
	 */
	public String getLocalPath();

	/**
	 * getMimeType: Returns the mime type for the file.
	 * @return String
	 */
	public String getMimeType();

	/**
	 * getModified: Returns the modified date for the item.
	 * @return Date The modified date.
	 */
	public Date getModified();

	/**
	 * getModifiedDisplay: Returns the modified date string.
	 * @return String The modified date string.
	 */
	public String getModifiedDisplay();

	/**
	 * getModifiedDisplay: Returns the modified date string.
	 * @param timeZone The time zone to use when displaying the date.
	 * @return String The modified date string.
	 */
	public String getModifiedDisplay(final TimeZone timeZone);

	/**
	 * getName: Returns the name for the file.
	 * @return String The name.
	 */
	public String getName();

	/**
	 * getPermissionsDisplay: On systems that support file permissions, returns
	 * a posix-like permission string for the file.
	 * @return String The permissions display string.
	 */
	public String getPermissionsDisplay();

	/**
	 * getSize: For files, returns the size of the file.  For directories,
	 * returns the count of child items in the directory.
	 * @return long The size of the file or count of items in the directory.
	 */
	public long getSize();

	/**
	 * getSizeDisplay: Returns a human-readable form of the size.
	 * @return String The display version of the size.
	 */
	public String getSizeDisplay();

	/**
	 * getStringData: Returns the data from the file as a string.
	 * @return String The file string data.
	 * @throws IOException in case of read error.
	 */
	public String getStringData() throws IOException;

	/**
	 * getTargetLocalPath: Returns the target local path for the file item.
	 * @return String The target local path.
	 */
	public String getTargetLocalPath();

	/**
	 * isDirectory: Returns the flag indicating the file is a directory.
	 * @return boolean <code>true</code> if the item is a directory, otherwise
	 * it is a file.
	 */
	public boolean isDirectory();

	/**
	 * isDownloadable: Returns the flag indicating the file is downloadable or
	 * not.
	 * @return boolean <code>true</code> if it is downloadable, otherwise it is
	 * not.
	 */
	public boolean isDownloadable();

	/**
	 * setDownloadable: Sets the downloadable flag.
	 * @param downloadable The flag value.
	 */
	public void setDownloadable(final boolean downloadable);

}