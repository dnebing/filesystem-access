package com.liferay.filesystemaccess.portlet;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.portal.kernel.model.Portlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * class FilesystemAccessPanelApp: Component which exposes our portlet as a control panel.
 * @author dnebinger
 */
@Component(
	immediate = true,
	property = {
		"panel.app.order:Integer=750",
		"panel.category.key=" + PanelCategoryKeys.CONTROL_PANEL_CONFIGURATION
	},
	service = PanelApp.class
)
public class FilesystemAccessPanelApp extends BasePanelApp {

	@Override
	public String getPortletId() {
		return FilesystemAccessPortletKeys.FILESYSTEM_ACCESS;
	}

	@Override
	@Reference(
		target = "(javax.portlet.name=" + FilesystemAccessPortletKeys.FILESYSTEM_ACCESS + ")",
		unbind = "-"
	)
	public void setPortlet(Portlet portlet) {
		super.setPortlet(portlet);
	}

}