package com.liferay.filesystemaccess.api;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * class DuplicateFileException: An exception thrown when attempting to create
 * a file that already exists.
 * @author dnebinger
 */
public class DuplicateFileException extends PortalException {

	/**
	 * DuplicateFileException: Constructor
	 * @param msg
	 */
	public DuplicateFileException(String msg) {
		super(msg);
	}

	/**
	 * DuplicateFileException: Constructor
	 * @param msg
	 * @param cause
	 */
	public DuplicateFileException(String msg, Throwable cause) {
		super(msg, cause);
	}

	private static final long serialVersionUID = -5766074504592093807L;

}