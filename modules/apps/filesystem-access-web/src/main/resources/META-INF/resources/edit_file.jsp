
<%@ include file="/init.jsp" %>

<portlet:renderURL var="cancelURL">
	<portlet:param name="mvcPath" value="/view.jsp" />
	<portlet:param name="redirect" value="<%= currentURL %>" />
	<portlet:param name="currentPath" value="<%= currentPath %>" />
</portlet:renderURL>

<%
String target = renderRequest.getParameter(Constants.PARM_TARGET);

String targetFile = currentPath;

if (! targetFile.endsWith(StringPool.SLASH)) {
	targetFile = targetFile + StringPool.SLASH;
}

targetFile = targetFile + target;

FilesystemItem item = _filesystemAccessService.getFilesystemItem(rootPath, targetFile, themeDisplay.getTimeZone());

String aceMode = StringPool.BLANK;
String content = StringPool.BLANK;
String displayName = StringPool.BLANK;
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
			}
			else {
				content = HtmlUtil.escapeJS(content);
			}
		}
		catch (IOException ioe) {
			_log.error("Error reading " + item.getAbsolutePath(), ioe);
		}

		if (content == null) {
			content = StringPool.BLANK;
		}
	}
}
%>

<c:if test="<%= showEditor %>">
	<portlet:actionURL name="/save_file" var="saveURL">
		<portlet:param name="redirect" value="<%= currentURL %>" />
		<portlet:param name="currentPath" value="<%= currentPath %>" />
	</portlet:actionURL>

	<form action="<%= saveURL %>" id="<portlet:namespace/>formS" method="post" name="<portlet:namespace/>formS">
		<aui:input name="<%= ActionRequest.ACTION_NAME %>" type="hidden" />
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
		<aui:input name="currentPath" type="hidden" value="<%= currentPath %>" />
		<aui:input name="target" type="hidden" value="<%= target %>" />
		<aui:input name="content" type="hidden" value="" />

		<aui:button-row>
			<aui:button id="save1" type="submit" value="save" />
			<aui:button href="<%= cancelURL %>" value="cancel" />

			<span class="display-current-path"><b><liferay-ui:message key="file-colon"/></b><span class="fname-display"><%= displayName %></span></span>

			<span class="div-right"><aui:input cssClass="div-right" helpMessage="wrap-text-help" label="wrap-text" name="wrapText" type="checkbox" /></span>
		</aui:button-row>

		<div class="code-editor" id="fileEditor">
		</div>

		<aui:button-row>
			<aui:button id="save2" type="submit" value="save" />

			<aui:button href="<%= cancelURL %>" value="cancel" />
		</aui:button-row>
	</form>

	<aui:script use="aui-ace-editor,liferay-store,resize-base">

		if (A.all('#fileEditor').size()) {
			var editor = new A.AceEditor({
				boundingBox: '#fileEditor',
				width: "100%",
				height: "100%",
				useWrapMode: false,
				showPrintMargin: false,
				mode: '<%= aceMode %>'
			});

			var wrapTextCheckbox = A.one('#<portlet:namespace />wrapText');

			wrapTextCheckbox.after('click', function() {
				var checked = wrapTextCheckbox.get('checked');

				editor.set('useWrapMode', checked);
			});

			var textContent = '';

		<%
			if (content.length() > 0) {

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

			// bind up the submit buttons so we save the editor content.
			A.all('#<portlet:namespace/>save1').on('click', function() {
				var content = editor.getSession().getValue();

				A.all('#<portlet:namespace/>content').val(content);
			});
			A.all('#<portlet:namespace/>save2').on('click', function() {
				var content = editor.getSession().getValue();

				A.all('#<portlet:namespace/>content').val(content);
			});
		}

	</aui:script>
</c:if>

<c:if test="<%= !showEditor %>">
	<aui:button-row>
		<aui:button href="<%= cancelURL %>" value="cancel" />
	</aui:button-row>
</c:if>

<%!
private static Log _log = LogFactoryUtil.getLog("com.liferay.filesystemaccess.portlet.edit_file_jsp");
%>