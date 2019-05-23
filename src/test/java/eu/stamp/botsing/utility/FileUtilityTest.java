package eu.stamp.botsing.utility;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtilityTest {

	Logger log = LoggerFactory.getLogger(FileUtilityTest.class);

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	@Test
	public void copyFolderTest() throws IOException {


		File source = tmpFolder.newFolder();
		File dest = tmpFolder.newFolder();

		// Write txt file
		File tempTxtFile = new File(source, "sample.txt");
		FileUtils.writeStringToFile(tempTxtFile, "hello txt world", "UTF-8");

		// Write java file
		File tempJavaFile = new File(source, "Sample.java");
		FileUtils.writeStringToFile(tempJavaFile, "hello java world", "UTF-8");

		FileUtility.copyJavaFile(source, dest);

		File shouldExist = new File(dest, "Sample.java");
		File shouldNotExist = new File(dest, "sample.txt");

		assertTrue("Java file should be copied", shouldExist.exists());
		assertTrue("Not Java file should not be copied", !shouldNotExist.exists());
	}
}
