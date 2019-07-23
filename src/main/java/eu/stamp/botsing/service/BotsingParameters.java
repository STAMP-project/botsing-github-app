package eu.stamp.botsing.service;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

public class BotsingParameters {


	private final String CRASH_LOG = "crash_log",
						GROUP_ID = "group_id",
						ARTIFACT_ID = "artifact_id",
						VERSION = "version";
	
	private final String 	SEARCH_BUDGET = "search_budget",
							GLOBAL_TIMEOUT = "global_timeout",
							POPULATION = "population",
							PACKAGE_FILTER ="package_filter";
	
	private HashMap<String, String> mandatoryParameters,
									optionalParameters;
	
	public BotsingParameters(String groupId,String  artifactId,String  version,String  searchBudget,String  globalTimeout,String  population,
			String  packageFilter) throws InvalidParameterException
	{
		if (groupId == null || groupId.trim().isEmpty() || artifactId == null || artifactId.trim().isEmpty() || version == null || version.trim().isEmpty()) throw new InvalidParameterException("Missing mandatory parameter");
		
		this.mandatoryParameters = new HashMap<String, String>();
		this.mandatoryParameters.put(GROUP_ID, groupId);
		this.mandatoryParameters.put(ARTIFACT_ID, artifactId);
		this.mandatoryParameters.put(VERSION, version);
		
		this.optionalParameters = new HashMap<String, String>();
		this.optionalParameters.put(SEARCH_BUDGET, searchBudget);
		this.optionalParameters.put(GLOBAL_TIMEOUT, globalTimeout);
		this.optionalParameters.put(POPULATION, population);
		this.optionalParameters.put(PACKAGE_FILTER, packageFilter);

	}


	public String getGroupId() {
		return this.mandatoryParameters.get(GROUP_ID);
	}


	public String getArtifactId() {
		return this.mandatoryParameters.get(ARTIFACT_ID);
	}


	public String getVersion() {
		return this.mandatoryParameters.get(VERSION);
	}

	public Map<String, String> getMandatoryParameters (File crashLogFile) throws InvalidParameterException
	{
		try
		{
			this.mandatoryParameters.put(CRASH_LOG, crashLogFile.getAbsolutePath());
		} catch (RuntimeException e)
		{
			throw new InvalidParameterException("Invalid crash file");
		}
		
		return this.mandatoryParameters;
		
		
	}

	public String getSearchBudget() {
		return this.optionalParameters.get(SEARCH_BUDGET);
	}


	public String getGlobalTimeout() {
		return this.optionalParameters.get(GLOBAL_TIMEOUT);
	}


	public String getPopulation() {
		return this.optionalParameters.get(POPULATION);
	}




	public String getPackageFilter() {
		return this.optionalParameters.get(PACKAGE_FILTER);
	}
	
	public Map<String, String> getOptionalParameters ()
	{
		return this.optionalParameters;
	}
	
}
