package com.liferay.filesystemaccess.util;

import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;

/**
 * class FilenameUtil: This is a simple utility class that can do a couple of things with a filename.
 * @author dnebinger
 */
public class FilenameUtil {

	/**
	 * getBaseName: Returns the base name of a given path.
	 * @param path The path to get the base for.
	 * @return String The base name.
	 */
	public static String getBaseName(String path) {
		if (Validator.isNull(path)) {
			return StringPool.SLASH;
		}

		if (path.trim().length() == 1) {
			return path.trim();
		}

		int pos = path.lastIndexOf(StringPool.SLASH);

		return path.substring(pos+1);
	}

	/**
	 * getParentPath: Returns the parent path from the given path
	 * @param path The path to determine the parent for
	 * @return String The parent path.
	 */
	public static String getParentPath(String path) {

		// if path is null/empty, return slash

		if (Validator.isNull(path)) {
			return StringPool.SLASH;
		}

		// if path is only one char, return it.

		if (path.trim().length() == 1) {
			return path;
		}

		// find the last slash

		int pos = path.trim().lastIndexOf(StringPool.SLASH);

		// if the first char, then parent is slash

		if (pos == 0) {
			return StringPool.SLASH;
		}

		// return everything up to the last slash

		return path.trim().substring(0, pos);
	}

}