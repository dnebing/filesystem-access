package com.liferay.filesystemaccess.api;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * class CreateFilesystemItemException: Thrown when a failure occurs creating a filesystem item.
 * @author dnebinger
 */
public class CreateFilesystemItemException extends PortalException {

	/**
	 * CreateFilesystemItemException: Constructor
	 * @param msg
	 */
	public CreateFilesystemItemException(String msg) {
		super(msg);
	}

	/**
	 * CreateFilesystemItemException: Constructor
	 * @param msg
	 * @param cause
	 */
	public CreateFilesystemItemException(String msg, Throwable cause) {
		super(msg, cause);
	}

	private static final long serialVersionUID = -1940772602210564256L;

}