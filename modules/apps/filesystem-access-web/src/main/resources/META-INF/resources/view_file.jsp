
<%@ include file="/init.jsp" %>

<portlet:renderURL var="doneViewingURL">
	<portlet:param name="mvcPath" value="/view.jsp" />
	<portlet:param name="redirect" value="<%= currentURL %>" />
	<portlet:param name="currentPath" value="<%= currentPath %>" />
</portlet:renderURL>

<%
String target = renderRequest.getParameter("target");

String targetFile = currentPath;

if (! targetFile.endsWith("/")) {
	targetFile = targetFile + '/';
}

targetFile = targetFile + target;

FilesystemItem item = _filesystemAccessService.getFilesystemItem(rootPath, targetFile, themeDisplay.getTimeZone());

String aceMode = "";
String content = "";
String displayName = "";
boolean showEditor = false;

if (item != null) {
	boolean visible = true;

	if ((viewSizeLimit >= 0) && (viewSizeLimit < item.getSize())) {
		visible = false;
	}

	if (! visible) {
		_log.info("File [" + item.getTargetLocalPath() + "] is not visible.");
	}

	aceMode = item.getEditorMode();

	if ((visible) && (Validator.isNotNull(aceMode))) {
		showEditor = true;

		try {
			content = item.getStringData();

			displayName = item.getTargetLocalPath();

			if (content == null) {
				content = "";
			} else {
				content = HtmlUtil.escapeJS(content);
			}
		} catch (IOException ioe) {
			_log.error("Error reading " + item.getAbsolutePath(), ioe);
		}
	}
}
%>

<c:if test="<%= showEditor %>">
	<aui:button-row>
		<aui:button href="<%= doneViewingURL %>" value="done" />

		<span class="display-current-path"><b><liferay-ui:message key="file-colon"/></b><span class="fname-display"><%= displayName %></span></span>

		<span class="div-right"><aui:input cssClass="div-right" helpMessage="wrap-text-help" label="wrap-text" name="wrapText" type="checkbox" /></span>
	</aui:button-row>

	<div class="code-editor" id="fileEditor">
	</div>
</c:if>

<aui:button-row>
	<aui:button href="<%= doneViewingURL %>" value="done" />
</aui:button-row>

<c:if test="<%= showEditor %>">
	<aui:script use="aui-ace-editor,liferay-store,resize-base">

		if (A.all('#fileEditor').size()) {
				var editor = new A.AceEditor({
					boundingBox: '#fileEditor',
					readOnly: true,
					width: "100%",
					height: "100%",
					useWrapMode: false,
					showPrintMargin: false,
					mode: '<%= aceMode %>'
				});

		var wrapTextCheckbox = A.one('#<portlet:namespace />wrapText');

		wrapTextCheckbox.after(
			'click',
			function() {
				var checked = wrapTextCheckbox.get('checked');

				editor.set('useWrapMode', checked);
			}
		);

		var textContent = '';

	<%

			// okay, split the large content into 10k chunks.
			// I prefer using separate chunks rather than one huge piece of content

			int interval = 10240;
			int arrayLength = (int)Math.ceil(((content.length() / (double)interval)));
			String[] result = new String[arrayLength];

			int j = 0;
			int lastIndex = result.length - 1;
			int tInterval;

			for (int i = 0; i < lastIndex; i++) {
				tInterval = interval;

				while (content.charAt(j+tInterval-1) == '\\') {
					tInterval --;
				}

				result[i] = content.substring(j, j + tInterval);
				j += tInterval;
			}

			result[lastIndex] = content.substring(j);

			for (String s : result) {
				%>

				textContent = textContent + '<%= s %>';

		<%
			}
	%>

				editor.getEditor().setValue(textContent);
				editor.getEditor().clearSelection();

				editor.render();

				new A.Resize({
					handles: 'br,b',
					node: '.code-editor',
					wrap: false,
					on: {
						resize: function(event) {
							/*debugger; */
							editor.getEditor().resize(true);
						}
					}
				});
			}

	</aui:script>
</c:if>

<%!
private static Log _log = LogFactoryUtil.getLog("com.liferay.filesystemaccess.portlet.view_file_jsp");
%>