package it.polimi.chansonnier.agent;

import org.eclipse.smila.connectivity.framework.schema.DataSourceConnectionConfigPlugin;

public class DataSourceConnectionConfigPluginImpl implements
		DataSourceConnectionConfigPlugin {

	@Override
	public String getMessagesPackage() {
		return "it.polimi.chansonnier.agent.messages";
	}

	@Override
	public String getSchemaLocation() {
		return "schemas/YouTubeDataSourceConnectionConfigSchema.xsd";
	}

}
