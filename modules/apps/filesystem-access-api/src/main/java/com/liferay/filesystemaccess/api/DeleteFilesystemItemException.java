package com.liferay.filesystemaccess.api;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author dnebinger
 */
public class DeleteFilesystemItemException extends PortalException {

	public DeleteFilesystemItemException(String msg) {
		super(msg);
	}

	public DeleteFilesystemItemException(String msg, Throwable cause) {
		super(msg, cause);
	}

	private static final long serialVersionUID = -8711631351674584274L;

}