/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.core.test;

import it.polimi.chansonnier.processing.LastIndexedService;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;

public class LastIndexedServiceTest extends ProcessingServiceTest {
	LastIndexedService _service;
	private static final String TITLE = "We will rock you";
	private static final String ARTIST = "Queen";

	@Override
	protected void init() throws Exception {
		_service = new LastIndexedService();
	}
	
	public void testStoresLastRecordProcessedTitleAttribute() throws Exception {
		final Id id = createBlackboardRecord("source", "key");
		setAttribute(id, new Path("title"), TITLE);
		setAttribute(id, new Path("artist"), ARTIST);
    
		Id[] result = _service.process(getBlackboard(), new Id[] { id });
		
		assertEquals(1, result.length);
		assertEquals(TITLE, _service.getLastTitle());
	}
}
