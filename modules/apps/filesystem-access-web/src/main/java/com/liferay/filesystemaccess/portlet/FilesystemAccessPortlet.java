package com.liferay.filesystemaccess.portlet;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import javax.portlet.Portlet;

import org.osgi.service.component.annotations.Component;

/**
 * class FilesystemAccessPortlet: This portlet is used to provide filesystem
 * access.  Allows an administrator to grant access to users to access local
 * filesystem resources, useful in those cases where the user does not have
 * direct OS access.
 *
 * This portlet will provide access to download, upload, view, 'touch' and
 * edit files.
 * @author dnebinger
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.system.admin",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=false",
		"javax.portlet.display-name=Filesystem Access",
		"javax.portlet.init-param.config-template=/configuration.jsp",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class FilesystemAccessPortlet extends MVCPortlet {
}