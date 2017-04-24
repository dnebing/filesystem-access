package com.liferay.filesystemaccess.portlet;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author dnebinger
 */
public class OperationNotAllowedException extends PortalException {

	public OperationNotAllowedException(String msg) {
		super(msg);
	}

	public OperationNotAllowedException(String msg, Throwable cause) {
		super(msg, cause);
	}

	private static final long serialVersionUID = 2498690609250463926L;

}