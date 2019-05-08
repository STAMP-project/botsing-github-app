package eu.stamp.botsing.utility;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

public class FileUtility {

	public static void copyJavaFile(File sourceFolder, File targetFolder) {

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

	public static Properties parsePropertiesString(String s) throws IOException {
	    final Properties p = new Properties();
	    p.load(new StringReader(s));
	    return p;
	}

	/**
	 * Return the list of file found if none of them contains the regex
	 *
	 * @param folder
	 * @param regex
	 * @param extensions of files considered
	 * @return the list of file found
	 * @throws IOException
	 */
	public static Collection<File> search(String folder, String regex, String[] extensions) throws IOException {
		Collection<File> files = FileUtils.listFiles(new File(folder), extensions, true);

        for (File file : files) {

        	if (searchInFile(file, regex)) {
				return null;
			}
        }

		return files;
	}

	protected static boolean searchInFile(File file, String regex) throws IOException {
		boolean result = false;

		LineIterator it = FileUtils.lineIterator(file, "UTF-8");

		while (it.hasNext()) {
			String line = it.nextLine();
			if (line.matches(regex)) {
				result = true;
				break;
			}
		}

		return result;
	}

}