package com.liferay.filesystemaccess.portlet;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author dnebinger
 */
public class MissingConfigurationException extends PortalException {

	public MissingConfigurationException(String msg) {
		super(msg);
	}

	public MissingConfigurationException(String msg, Throwable cause) {
		super(msg, cause);
	}

	private static final long serialVersionUID = -9056357974897833962L;

}