package eu.stamp.botsing.controller.event.actions;

import java.io.File;

public abstract class TestFiles {

	private File 	dataFile,
					scaffoldingFile;
	private String 	dataFileName,
					scaffoldingFileName;
	
	protected File getDataFile() {
		return dataFile;
	}

	protected File getScaffoldingFile() {
		return scaffoldingFile;
	}

	
	
	protected String getDataFileName() {
		return dataFileName;
	}

	protected String getScaffoldingFileName() {
		return scaffoldingFileName;
	}

	public void setDataFile(String fileName,File dataFile) {
		this.dataFile = dataFile;
		this.dataFileName = fileName;
	}

	public void setScaffoldingFile(String fileName,File scaffoldingFile) {
		this.scaffoldingFile = scaffoldingFile;
		this.scaffoldingFileName = fileName;
	}
	
	public boolean isCompleted ()
	{
		return this.dataFile != null && this.scaffoldingFile != null;
	}

	
}
