package it.polimi.smilaintegration.youtubegrabber;

import org.eclipse.smila.connectivity.framework.schema.DataSourceConnectionConfigPlugin;

public class DataSourceConnectionConfigPluginImpl implements
		DataSourceConnectionConfigPlugin {

	@Override
	public String getMessagesPackage() {
		return "it.polimi.smilaintegration.youtubegrabber.messages";
	}

	@Override
	public String getSchemaLocation() {
		// TODO Auto-generated method stub
		return "schemas/YoutubeLinkGrabberAgentDataSourceConnectionConfig.xsd";
	}

}
