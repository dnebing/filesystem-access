package com.liferay.filesystemaccess.audit;

/**
 * interface AuditEventTypes: This interface defines the event types that we are using for
 * auditing purposes at the service level.
 * @author dnebinger
 */
public interface AuditEventTypes {

	public static final String ADD_DIRECTORY = "ADD_DIRECTORY";

	public static final String ADD_FILE = "ADD_FILE";

	public static final String DELETE_DIRECTORY = "DELETE_DIRECTORY";

	public static final String DELETE_FILE = "DELETE_FILE";

	public static final String DOWNLOAD_DIRECTORY = "DOWNLOAD_DIRECTORY";

	public static final String DOWNLOAD_FILE = "DOWNLOAD_FILE";

	public static final String TOUCH = "TOUCH";

	public static final String UPDATE_CONTENT = "UPDATE_CONTENT";

	public static final String UPLOAD_FILE = "UPLOAD_FILE";

}