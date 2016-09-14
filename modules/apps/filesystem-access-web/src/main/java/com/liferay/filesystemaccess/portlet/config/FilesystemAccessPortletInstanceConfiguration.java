package com.liferay.filesystemaccess.portlet.config;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * class FilesystemAccessPortletInstanceConfiguration: Instance configuration for
 * the portlet configuration.
 * @author dnebinger
 */
@ExtendedObjectClassDefinition(
	category = "Platform",
	scope = ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE
)
@Meta.OCD(
	localization = "content/Language",
	name = "FilesystemAccessPortlet.portlet.instance.configuration.name",
	id = "com.liferay.filesystemaccess.portlet.config.FilesystemAccessPortletInstanceConfiguration"
)
public interface FilesystemAccessPortletInstanceConfiguration {

	/**
	 * rootPath: This is the root path that constrains all filesystem access.
	 */
	@Meta.AD(deflt = "${LIFERAY_HOME}", required = false)
	public String rootPath();

	/**
	 * showPermissions: This is a flag whether permissions should be displayed or not.
	 */
	@Meta.AD(deflt = "true", required = false)
	public boolean showPermissions();

	/**
	 * deletesAllowed: This is a flag that determines whether files/folders can be deleted or not.
	 */
	@Meta.AD(deflt = "false", required = false)
	public boolean deletesAllowed();

	/**
	 * uploadsAllowed: This is a flag that determines whether file uploads are allowed.
	 */
	@Meta.AD(deflt = "false", required = false)
	public boolean uploadsAllowed();

	/**
	 * downloadsAllowed: This is a flag that determines whether file/folder downloads are allowed or not.
	 */
	@Meta.AD(deflt = "true", required = false)
	public boolean downloadsAllowed();

	/**
	 * editsAllowed: This is a flag that determines whether inline editing is allowed.
	 */
	@Meta.AD(deflt = "false", required = false)
	public boolean editsAllowed();

	/**
	 * addsAllowed: This is a flag that determines whether files/folders can be added or not.
	 */
	@Meta.AD(deflt = "false", required = false)
	public boolean addsAllowed();

	/**
	 * viewSizeLimit: This is a size limit that determines whether a file can be viewed in the browser.
	 */
	@Meta.AD(deflt = "-1", required = false)
	public long viewSizeLimit();

	/**
	 * downloadableFolderSizeLimit: This defines the size limit for downloading folders.  This is the max size (uncompressed)
	 * that a folder can be in order to be downloadable.
	 */
	@Meta.AD(deflt = "-1", required = false)
	public long downloadableFolderSizeLimit();

	/**
	 * downloadableFolderItemLimit: This defines the max number of files that can be in a folder for downloading.
	 */
	@Meta.AD(deflt = "-1", required = false)
	public int downloadableFolderItemLimit();

}