package eu.stamp.botsing.service;

import java.io.File;
import java.util.Properties;

public interface CICDService 
{

	Properties getInputProperties(BotsingParameters parameters) throws Exception;

	String getData(BotsingParameters parameters) throws Exception;

	String sendDataString (BotsingParameters parameters, String dataString) throws Exception;

	String sendDataFile(BotsingParameters parameters, File file)throws Exception;

}