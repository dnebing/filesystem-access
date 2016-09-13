package com.liferay.filesystemaccess.portlet.config;

import com.liferay.filesystemaccess.portlet.FilesystemAccessPortletKeys;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PortletInstance;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.PortletRequest;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * class FilesystemAccessDisplayContext: Wrapper for accessing the portlet instance configuration,
 * can be used in JSP and action phases to gain access to the instance configuration object.
 * @author dnebinger
 */
@Component
public class FilesystemAccessDisplayContext {

	/**
	 * FilesystemAccessDisplayContext: Default constructor for our component.
	 */
	public FilesystemAccessDisplayContext() {
	}

	/**
	 * FilesystemAccessDisplayContext: Constructor
	 * @param req The http request object.
	 */
	public FilesystemAccessDisplayContext(final HttpServletRequest req) {

		// get the theme display from the request.

		ThemeDisplay themeDisplay;

		themeDisplay = (ThemeDisplay)req.getAttribute(WebKeys.THEME_DISPLAY);

		// extract the portlet display instance from the theme display.

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		// if the id is blank...

		if (Validator.isNull(portletDisplay.getId())) {

			// during action phase it is likely the id will not be set.  This is
			// by design, these kinds of things are not guaranteed to be
			// properly set up for the action phase.

			//

			// when this is the case, we have to fall back to the configuration
			// provider entry point.

			// find our current portlet instance

			PortletInstance instance = PortletInstance.fromPortletInstanceKey(
				FilesystemAccessPortletKeys.FILESYSTEM_ACCESS);
			Layout l = themeDisplay.getLayout();

			try {

				// get the config

				portletInstanceConfig =
					configurationProvider.getPortletInstanceConfiguration(
						FilesystemAccessPortletInstanceConfiguration.class, l,
						instance);
			}
			catch (ConfigurationException ce) {
				logger.error("Error getting instance config.", ce);
			}
		}
		else {

			// we are in a render or resource phase where portlet display is
			// good.

			try {
				portletInstanceConfig =
					portletDisplay.getPortletInstanceConfiguration(
						FilesystemAccessPortletInstanceConfiguration.class);
			}
			catch (ConfigurationException ce) {
				logger.error("Error getting portlet instance config.", ce);
			}
		}
	}

	/**
	 * FilesystemAccessDisplayContext: Constructor that takes a portlet request object.
	 * @param req The portlet request object.
	 */
	public FilesystemAccessDisplayContext(final PortletRequest req) {
		this(PortalUtil.getHttpServletRequest(req));
	}

	/**
	 * getFilesystemAccessPortletInstanceConfiguration: Returns the instance config obj.
	 * @return FilesystemAccessPortletInstanceConfiguration The instance config object to use.
	 */
	public FilesystemAccessPortletInstanceConfiguration
		getFilesystemAccessPortletInstanceConfiguration() {

		return portletInstanceConfig;
	}

	/**
	 * setConfigurationProvider: Sets the config provider to use.
	 * @param configurationProvider The config provider to use.
	 */
	@Reference
	protected void setConfigurationProvider(
		ConfigurationProvider configurationProvider) {

		this.configurationProvider = configurationProvider;
	}

	private static final Log logger = LogFactoryUtil.getLog(
		FilesystemAccessDisplayContext.class);

	private ConfigurationProvider configurationProvider;
	private FilesystemAccessPortletInstanceConfiguration portletInstanceConfig =
		null;

}