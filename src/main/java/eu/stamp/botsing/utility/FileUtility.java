package eu.stamp.botsing.utility;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

public class FileUtility {

	public static void copyDirectory(File sourceFolder, File targetFolder) {

		try {
			// Create a filter for ".java" files
			IOFileFilter txtSuffixFilter = FileFilterUtils.suffixFileFilter(".java");
			IOFileFilter txtFiles = FileFilterUtils.and(FileFileFilter.FILE, txtSuffixFilter);

			// Create a filter for either directories or ".java" files
			FileFilter filter = FileFilterUtils.or(DirectoryFileFilter.DIRECTORY, txtFiles);

			FileUtils.copyDirectory(sourceFolder, targetFolder, filter);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}