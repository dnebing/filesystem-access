
<%@ include file="/init.jsp" %>

<%

	// okay, from current path we need to build a list of urls...

	String currentBaseName = FilenameUtil.getBaseName(currentPath);
	String parentPath = FilenameUtil.getParentPath(currentPath);
	String tempName;

	boolean onRootPath = StringPool.SLASH.equals(currentPath);

// prep the portletURL for the list.

	PortletURL portletUrl = renderResponse.createRenderURL();

// these are the default values

	portletUrl.setParameter("mvcPath", "/view.jsp");
	portletUrl.setParameter("redirect", currentURL);
	portletUrl.setParameter("currentPath", currentPath);

	PortletURL iterUrl = renderResponse.createRenderURL();

// these are the default values

	iterUrl.setParameter("mvcPath", "/view.jsp");
	iterUrl.setParameter("redirect", currentURL);
	iterUrl.setParameter("currentPath", currentPath);

// we need to create a list of links

	List<String> linkUrls = new LinkedList<>();
	List<String> linkTitles = new LinkedList<>();

	if (! onRootPath) {

		// while the parent path has length

		while (parentPath.length() > 1) {

			// the base name of the parent is our next link name

			tempName = FilenameUtil.getBaseName(parentPath);

			// update the parameter values

			portletUrl.setParameter("nextPath", parentPath);
			portletUrl.setParameter("linkName", tempName);

			// insert the url at the front of the list

			linkUrls.add(0, portletUrl.toString());
			linkTitles.add(0, tempName);

			// compute the new parent path

			parentPath = FilenameUtil.getParentPath(parentPath);
		}
	}
%>

<b><liferay-ui:message key="welcome-msg"/></b>

<liferay-ui:error exception="<%= OperationNotAllowedException.class %>" message="error-adds-are-not-authorized" />
<liferay-ui:error exception="<%= DuplicateFileException.class %>" message="error-duplicate-name" />
<liferay-ui:error exception="<%= CreateFilesystemItemException.class %>" message="error-creating-file-folder" />

<portlet:renderURL var="addFileURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
	<portlet:param name="mvcPath" value="/add_file_folder.jsp" />
	<portlet:param name="redirect" value="<%= currentURL %>" />
	<portlet:param name="currentPath" value="<%= currentPath %>" />
	<portlet:param name="addType" value="addFile" />
	<portlet:param name="addMessage" value="add-file" />
</portlet:renderURL>

<portlet:renderURL var="addFolderURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
	<portlet:param name="mvcPath" value="/add_file_folder.jsp" />
	<portlet:param name="redirect" value="<%= currentURL %>" />
	<portlet:param name="currentPath" value="<%= currentPath %>" />
	<portlet:param name="addType" value="addFolder" />
	<portlet:param name="addMessage" value="add-folder" />
</portlet:renderURL>

<aui:button-row>
	<c:if test="<%= allowAddFileFolder %>">
		<button class="btn btn-default" data-target="#<portlet:namespace/>AddFileModal" data-toggle="modal" id="<portlet:namespace/>showAddFileBtn"><liferay-ui:message key="add-file" /></button>
		<div aria-labelledby="<portlet:namespace/>AddFileModalLabel" class="fade in lex-modal modal" id="<portlet:namespace/>AddFileModal" role="dialog" tabindex="-1">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<portlet:actionURL name="/add_file_folder" var="addFileActionURL" />

					<div id="<portlet:namespace/>fm3">
						<form action="<%= addFileActionURL %>" id="<portlet:namespace/>form3" method="post" name="<portlet:namespace/>form3">
							<aui:input name="<%= ActionRequest.ACTION_NAME %>" type="hidden" />
							<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
							<aui:input name="currentPath" type="hidden" value="<%= currentPath %>" />
							<aui:input name="addType" type="hidden" value="addFile" />

							<div class="modal-header">
								<button aria-labelledby="Close" class="btn btn-default close" data-dismiss="modal" role="button" type="button">
									<svg aria-hidden="true" class="lexicon-icon lexicon-icon-times">
										<use xlink:href="<%= themeDisplay.getPathThemeImages() + "/lexicon/icons.svg" %>#times" />
									</svg>
								</button>

								<button class="btn btn-default modal-primary-action-button visible-xs" type="button">
									<svg aria-hidden="true" class="lexicon-icon lexicon-icon-check">
										<use xlink:href="<%= themeDisplay.getPathThemeImages() + "/lexicon/icons.svg" %>#check" />
									</svg>
								</button>

								<h4 class="modal-title" id="<portlet:namespace/>AddFileModalLabel"><liferay-ui:message key="add-file" /></h4>
							</div>

							<div class="modal-body">
								<aui:fieldset>
									<aui:input autoFocus="true" helpMessage="add-file-help" id="addNameFile" label="add-file" name="addName" type="text" />
								</aui:fieldset>
							</div>

							<div class="modal-footer">
								<button class="btn btn-default close-modal" id="<portlet:namespace/>addFileBtn" name="<portlet:namespace/>addFileBtn" type="button"><liferay-ui:message key="add-file" /></button>
								<button class="btn btn-link close-modal" data-dismiss="modal" type="button"><liferay-ui:message key="cancel" /></button>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>

		<button class="btn btn-default" data-target="#<portlet:namespace/>AddFolderModal" data-toggle="modal" id="<portlet:namespace/>showAddFolderBtn"><liferay-ui:message key="add-folder" /></button>
		<div aria-labelledby="<portlet:namespace/>AddFolderModalLabel" class="fade in lex-modal modal" id="<portlet:namespace/>AddFolderModal" role="dialog" tabindex="-1">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<portlet:actionURL name="/add_file_folder" var="addFolderActionURL" />

					<div id="<portlet:namespace/>fm4">
						<form action="<%= addFolderActionURL %>" id="<portlet:namespace/>form4" method="post" name="<portlet:namespace/>form4">
							<aui:input name="<%= ActionRequest.ACTION_NAME %>" type="hidden" />
							<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
							<aui:input name="currentPath" type="hidden" value="<%= currentPath %>" />
							<aui:input name="addType" type="hidden" value="addFolder" />

							<div class="modal-header">
								<button aria-labelledby="Close" class="btn btn-default close" data-dismiss="modal" role="button" type="button">
									<svg aria-hidden="true" class="lexicon-icon lexicon-icon-times">
										<use xlink:href="<%= themeDisplay.getPathThemeImages() + "/lexicon/icons.svg" %>#times" />
									</svg>
								</button>

								<button class="btn btn-default modal-primary-action-button visible-xs" type="button">
									<svg aria-hidden="true" class="lexicon-icon lexicon-icon-check">
										<use xlink:href="<%= themeDisplay.getPathThemeImages() + "/lexicon/icons.svg" %>#check" />
									</svg>
								</button>

								<h4 class="modal-title" id="<portlet:namespace/>AddFolderModalLabel"><liferay-ui:message key="add-folder" /></h4>
							</div>

							<div class="modal-body">
								<aui:fieldset>
									<aui:input autoFocus="true" helpMessage="add-folder-help" id="addNameFolder" label="add-folder" name="addName" type="text" />
								</aui:fieldset>
							</div>

							<div class="modal-footer">
								<button class="btn btn-default close-modal" id="<portlet:namespace/>addFolderBtn" name="<portlet:namespace/>addFolderBtn" type="button"><liferay-ui:message key="add-folder" /></button>
								<button class="btn btn-link close-modal" data-dismiss="modal" type="button"><liferay-ui:message key="cancel" /></button>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</c:if>
	<c:if test="<%= allowUploads %>">
		<button class="btn btn-default" data-target="#<portlet:namespace/>uploadModal" data-toggle="modal" id="<portlet:namespace/>showUploadBtn"><liferay-ui:message key="upload-file" /></button>
		<div aria-labelledby="<portlet:namespace/>UploadModalLabel" class="fade in lex-modal modal" id="<portlet:namespace/>uploadModal" role="dialog" tabindex="-1">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<portlet:actionURL name="/upload_file" var="uploadActionURL" />

					<div id="<portlet:namespace/>fm4">
						<form action="<%= uploadActionURL %>" id="<portlet:namespace/>form5" method="post" name="<portlet:namespace/>form5" cssClass="container lfr-dynamic-form mtm" enctype="multipart/form-data" >
							<aui:input name="<%= ActionRequest.ACTION_NAME %>" type="hidden" />
							<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
							<aui:input name="currentPath" type="hidden" value="<%= currentPath %>" />
							<aui:input name="uploadFileName" type="hidden" />

							<div class="modal-header">
								<button aria-labelledby="Close" class="btn btn-default close" data-dismiss="modal" role="button" type="button">
									<svg aria-hidden="true" class="lexicon-icon lexicon-icon-times">
										<use xlink:href="<%= themeDisplay.getPathThemeImages() + "/lexicon/icons.svg" %>#times" />
									</svg>
								</button>

								<button class="btn btn-default modal-primary-action-button visible-xs" type="button">
									<svg aria-hidden="true" class="lexicon-icon lexicon-icon-check">
										<use xlink:href="<%= themeDisplay.getPathThemeImages() + "/lexicon/icons.svg" %>#check" />
									</svg>
								</button>

								<h4 class="modal-title" id="<portlet:namespace/>UploadModalLabel"><liferay-ui:message key="upload-file" /></h4>
							</div>

							<div class="modal-body">
								<aui:fieldset>
									<aui:input label="file" name="uploadFileSelect" type="file" />
								</aui:fieldset>
							</div>

							<div class="modal-footer">
								<button class="btn btn-default close-modal" id="<portlet:namespace/>uploadBtn" name="<portlet:namespace/>uploadBtn" type="button"><liferay-ui:message key="upload-file" /></button>
								<button class="btn btn-link close-modal" data-dismiss="modal" type="button"><liferay-ui:message key="cancel" /></button>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</c:if>

	<!-- Now we need a path of links for each parent directory -->
	<span class="display-current-path">
		<c:if test="<%= onRootPath %>">
			<liferay-ui:message key="slash" />
		</c:if>

		<c:if test="<%= ! onRootPath %>">
			<portlet:renderURL var="rootURL">
				<portlet:param name="mvcPath" value="/view.jsp" />
				<portlet:param name="redirect" value="<%= currentURL %>" />
				<portlet:param name="currentPath" value="<%= currentPath %>" />
				<portlet:param name="nextPath" value="/" />
				<portlet:param name="linkName" value="/" />
			</portlet:renderURL>

			<aui:a href="<%= rootURL %>">/</aui:a>

			<%
				String url, title;

				for (int idx = 0; idx < linkUrls.size(); idx++) {
					url = linkUrls.get(idx);
					title = linkTitles.get(idx);
			%>

			<aui:a href="<%= url %>"><%= title %></aui:a>
			<liferay-ui:message key="slash" />

			<%
				}
			%>

			<%= currentBaseName %>
		</c:if>
	</span>
</aui:button-row>

<liferay-ui:search-container
		iteratorURL="<%= iterUrl %>" total="<%= _filesystemAccessService.countFilesystemItems(rootPath, currentPath) %>"
>
	<liferay-ui:search-container-results
			results="<%= _filesystemAccessService.getFilesystemItems(rootPath, currentPath, searchContainer.getStart(), searchContainer.getEnd(), themeDisplay.getTimeZone()) %>"
	/>

	<liferay-ui:search-container-row
			className="com.liferay.filesystemaccess.api.FilesystemItem"
			escapedModel="true"
			modelVar="f"
	>
		<!-- want some sort of icon -->

		<liferay-ui:search-container-column-jsp name="name" path="/enter_link.jsp" valign="top" />

		<liferay-ui:search-container-column-text
				name="size"
				property="sizeDisplay"
				valign="top"
		/>

		<liferay-ui:search-container-column-text
				name="modified"
				property="modifiedDisplay"
				valign="top"
		/>

		<c:if test="<%= showPermissions %>">
			<liferay-ui:search-container-column-text
					name="permissions"
					property="permissionsDisplay"
					valign="top"
			/>
		</c:if>

		<liferay-ui:search-container-column-jsp
				cssClass="entry-action"
				name="actions"
				path="/file_actions.jsp"
				valign="top"
		/>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator />
</liferay-ui:search-container>

<aui:script use="aui-base">
	var showAddFile = A.one('#<portlet:namespace/>showAddFileBtn');

	if (showAddFile) {
		showAddFile.after('click', function() {
			var addNameText = A.one('#<portlet:namespace/>addNameFile');

			if (addNameText) {
				addNameText.val('');
				addNameText.focus();
			}
		});
	}

	var addFileBtnVar = A.one('#<portlet:namespace/>addFileBtn');

	if (addFileBtnVar) {
		addFileBtnVar.after('click',function() {
			var fm = A.one('#<portlet:namespace/>form3');

			if (fm) {
				fm.submit();
			}
		});
	}

	var showAddFolder = A.one('#<portlet:namespace/>showAddFolderBtn');

	if (showAddFolder) {
		showAddFolder.after('click', function() {
			var addNameText = A.one('#<portlet:namespace/>addNameFolder');

			if (addNameText) {
				addNameText.val('');
				addNameText.focus();
			}
		});
	}

	var addFolderBtnVar = A.one('#<portlet:namespace/>addFolderBtn');

	if (addFolderBtnVar) {
		addFolderBtnVar.after('click',function() {
			var fm = A.one('#<portlet:namespace/>form4');

			if (fm) {
				fm.submit();
			}
		});
	}

	var uploadFile = A.one('#<portlet:namespace/>showUploadBtn');

	if (uploadFile) {
		uploadFile.after('click', function() {
			var uploadText = A.one('#<portlet:namespace/>uploadFile');

			if (uploadText) {
				uploadText.val('');
				uploadText.focus();
			}
		});
	}

	var uploadBtnVar = A.one('#<portlet:namespace/>uploadBtn');

	if (uploadBtnVar) {
		uploadBtnVar.after('click', function() {
			var fm = A.one('#<portlet:namespace/>form5');

			if (fm) {
				fm.submit();
			}
		});
	}

	var fileSelect = A.one('#<portlet:namespace />uploadFileSelect');
	var title = A.one('#<portlet:namespace />uploadFileName');

	fileSelect.on('change',function(event) {
		var ct = event.currentTarget;
		var val = ct.val();

		if (val.includes('C:\\fakepath\\')) {
			val = val.replace(/C:\\fakepath\\/, '');
		}

		title.attr('value', val);
	});
</aui:script>

<%!
	private static Log _vlog = LogFactoryUtil.getLog("com.liferay.filesystemaccess.portlet.view_jsp");
%>