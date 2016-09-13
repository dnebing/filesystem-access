package com.liferay.filesystemaccess.portlet;

import com.liferay.filesystemaccess.api.FilesystemConstants;
import com.liferay.portal.kernel.util.StringPool;

/**
 * class Constants: Container for constant values used in the application.
 * @author dnebinger
 */
public interface Constants extends com.liferay.portal.kernel.util.Constants {

	public static final String ATTRIB_EDIT_FILE = "edit_file.jsp-file";

	public static final String ATTRIB_FILESYSTEM_SERVICE = "fileSystemService";

	public static final String MSG_ADD_FILE = "add-file";

	public static final String MSG_ADD_FOLDER = "add-folder";

	public static final String PARM_ADD_MESSAGE = "addMessage";

	public static final String PARM_ADD_NAME = "addName";

	public static final String PARM_ADD_TYPE = "addType";

	public static final String PARM_CONTENT = "content";

	public static final String PARM_CURRENT_PATH = "currentPath";

	public static final String PARM_NEXT_PATH = "nextPath";

	public static final String PARM_TARGET = "target";

	public static final String PARM_UPLOAD_FILE = "uploadFileSelect";

	public static final String PARM_UPLOAD_NAME = "uploadFileName";

	public static final String TOUCH = "touch";

	public static final String TYPE_ADD_FILE = "addFile";

	public static final String TYPE_ADD_FOLDER = "addFolder";

	public static final String VARIABLE_LIFERAY_HOME =
		StringPool.DOLLAR_AND_OPEN_CURLY_BRACE +
			FilesystemConstants.LIFERAY_HOME +
			StringPool.CLOSE_CURLY_BRACE;

}