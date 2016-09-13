package com.liferay.filesystemaccess.portlet.config;

import com.liferay.portal.kernel.settings.definition.ConfigurationBeanDeclaration;
import org.osgi.service.component.annotations.Component;

/**
 * class FilesystemAccessInstanceConfigurationBeanDeclaration: Declaration for the filesystem
 * access configuration bean.
 * @author dnebinger
 */
@Component
public class FilesystemAccessPortletInstanceConfigurationBeanDeclaration
	implements ConfigurationBeanDeclaration {

	@Override
	public Class<?> getConfigurationBeanClass() {
		return FilesystemAccessPortletInstanceConfiguration.class;
	}

}