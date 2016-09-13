package com.liferay.filesystemaccess.portlet.config;

import com.liferay.filesystemaccess.portlet.FilesystemAccessPortletKeys;
import com.liferay.portal.kernel.settings.definition.ConfigurationPidMapping;
import org.osgi.service.component.annotations.Component;

/**
 * class FilesystemAccessPortletInstanceConfigurationPidMapping: Handles the config pid mapping for
 * the filesystem access portlet.
 * @author dnebinger
 */
@Component
public class FilesystemAccessPortletInstanceConfigurationPidMapping
	implements ConfigurationPidMapping {

	/**
	 * getConfigurationBeanClass: Returns the configuration bean class.
	 * @return Class The configuration bean class.
	 */
	@Override
	public Class<?> getConfigurationBeanClass() {
		return FilesystemAccessPortletInstanceConfiguration.class;
	}

	/**
	 * getConfigurationPid: Returns the portlet id for the config.
	 * @return String The portlet id.
	 */
	@Override
	public String getConfigurationPid() {
		return FilesystemAccessPortletKeys.FILESYSTEM_ACCESS;
	}

}