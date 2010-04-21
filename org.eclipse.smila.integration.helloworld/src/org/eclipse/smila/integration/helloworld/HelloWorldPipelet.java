/*******************************************************************************
 * Copyright (c) 2010 Empolis GmbH and brox IT Solutions GmbH. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Igor Novakovic (Empolis GmbH) - initial implementation
 *******************************************************************************/

package org.eclipse.smila.integration.helloworld;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.SimplePipelet;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;

/**
 * HelloWorldPipelet is a very simple example of an processing pipelet.
 * Pipelet's configuration (the name of the source and the target attribute)
 * is read from the pipeline directly.
 * This pipelet implements a trivial business logic:
 * 	  Get the value of record's source attribute, append the string 
 * 	  " --- Hello world!!!" to it and set the result as a value of the 
 *    target attribute. 
 * */
public class HelloWorldPipelet implements SimplePipelet {

	private final String SOURCE_ATT = "source_attribute_name";
	private final String TARGET_ATT = "target_attribute_name";
	private String _sourceAttr = "";
	private String _targetAttr = "";
	private Log _log = LogFactory.getLog(HelloWorldPipelet.class);

	public Id[] process(Blackboard blackboard, Id[] recordIds)
			throws ProcessingException {
		for (Id id : recordIds) {
			try {
				String inValue = "";
				String outValue = "";

				Path inPath = new Path(_sourceAttr);
				Path outPath = new Path(_targetAttr);
				if (blackboard.hasAttribute(id, inPath)) {
					Literal inLiteral = blackboard.getLiteral(id, inPath);
					inValue = inLiteral.getStringValue();
				}
				Literal outLiteral = (Literal) RecordFactory.DEFAULT_INSTANCE
						.createLiteral();

				outValue = inValue + " --- Hello world!!!";

				outLiteral.setStringValue(outValue);
				blackboard.setLiteral(id, outPath, outLiteral);

			} catch (Exception e) {
				_log.error(
						"Error while calling HelloWorldPipelet for record: '"
								+ id + "':" + e.getMessage(), e);
				throw new ProcessingException(e);
			}
		}
		return recordIds;
	}

	public void configure(PipeletConfiguration config)
			throws ProcessingException {
		_sourceAttr = (String) config.getPropertyFirstValueNotNull(SOURCE_ATT);
		_targetAttr = (String) config.getPropertyFirstValueNotNull(TARGET_ATT);
	}
}
