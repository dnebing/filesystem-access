package com.liferay.filesystemaccess.api;

import java.io.File;
import java.io.FilenameFilter;

/**
 * class HiddenFilenameFilter: This is a filter to hide hidden files/folders
 * from external access.
 * @author dnebinger
 */
public class HiddenFilenameFilter implements FilenameFilter {

	/**
	 * HIDDEN_FILENAME_FILTER: A singleton instance of the hidden filename filter.
	 */
	public static final HiddenFilenameFilter HIDDEN_FILENAME_FILTER =
		new HiddenFilenameFilter();

	/**
	 * accept: Checks if the file should be included.
	 * @param dir The directory the file is in.
	 * @param name The name of the file to check.
	 * @return boolean <code>true</code> if the file can be included, otherwise
	 * it is excluded.
	 */
	@Override
	public boolean accept(File dir, String name) {

		// any file starting with a period is not visible.

		if (name.startsWith(".")) {
			return false;
		}

		File target = new File(dir, name);

		// if the os thinks the file is hidden, we will respect that

		if (target.isHidden()) {
			return false;
		}

		// if we cannot read nor write nor execute the target, don't show it.

		if (! target.canRead() && ! target.canExecute() && !target.canWrite()) {
			return false;
		}

		// if we get here it is okay to show the file.

		return true;
	}

}