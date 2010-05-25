/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
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
