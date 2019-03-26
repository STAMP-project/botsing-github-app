package eu.stamp.botsing.utility;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtilityTest {

	Logger log = LoggerFactory.getLogger(FileUtilityTest.class);

	@Test
	public void copyFolderTest() {

		String repoFolderPath = "/tmp/1545393228401-0";

		try {

			File source = new File(repoFolderPath + "/crash-reproduction-tests");
			File dest = new File(repoFolderPath + "/src/test/java/");
			FileUtility.copyDirectory(source, dest);

			log.debug("New Tests added to source folder");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
