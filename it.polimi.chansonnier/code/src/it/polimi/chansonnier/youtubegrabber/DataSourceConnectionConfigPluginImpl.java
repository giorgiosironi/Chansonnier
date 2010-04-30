package it.polimi.chansonnier.youtubegrabber;

import org.eclipse.smila.connectivity.framework.schema.DataSourceConnectionConfigPlugin;

public class DataSourceConnectionConfigPluginImpl implements
		DataSourceConnectionConfigPlugin {

	@Override
	public String getMessagesPackage() {
		return "it.polimi.chansonnier.youtubegrabber.messages";
	}

	@Override
	public String getSchemaLocation() {
		// TODO Auto-generated method stub
		return "schemas/YoutubeLinkGrabberAgentDataSourceConnectionConfig.xsd";
	}

}
