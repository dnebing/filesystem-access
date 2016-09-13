package com.liferay.filesystemaccess.portlet;

import com.liferay.filesystemaccess.api.FilesystemAccessService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.IOException;

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
		"javax.portlet.name=" + FilesystemAccessPortletKeys.FILESYSTEM_ACCESS,
		"javax.portlet.display-name=Filesystem Access",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class FilesystemAccessPortlet extends MVCPortlet {

	/**
	 * render: Overrides the parent method to handle the injection of our
	 * service as a render request attribute so it is available to all of the
	 * jsp files.
	 *
	 * @param renderRequest The render request.
	 * @param renderResponse The render response.
	 * @throws IOException In case of error.
	 * @throws PortletException In case of error.
	 */
	@Override
	public void render(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {

		// set the service as a render request attribute
		renderRequest.setAttribute(
			Constants.ATTRIB_FILESYSTEM_SERVICE, _filesystemAccessService);

		// invoke super class method to let the normal render operation run.
		super.render(renderRequest, renderResponse);
	}

	/**
	 * setFilesystemAccessService: Sets the filesystem access service instance to use.
	 * @param filesystemAccessService The filesystem access service instance.
	 */
	@Reference(unbind = "-")
	protected void setFilesystemAccessService(
		final FilesystemAccessService filesystemAccessService) {

		_filesystemAccessService = filesystemAccessService;
	}

	private FilesystemAccessService _filesystemAccessService;

	private static final Log logger = LogFactoryUtil.getLog(
		FilesystemAccessPortlet.class);
}
