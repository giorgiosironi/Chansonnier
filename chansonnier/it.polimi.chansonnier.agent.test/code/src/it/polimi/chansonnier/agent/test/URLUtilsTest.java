/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.agent.test;

import java.net.URL;

import it.polimi.chansonnier.utils.URLUtils;
import junit.framework.TestCase;

public class URLUtilsTest extends TestCase {
	public void testMakesAnHttpConnectionToRetrieveUrlContent() throws Exception {
		String content = URLUtils.retrieve(new URL("http://www.google.com"));
		System.out.println(content);
		assertTrue(content.contains("<html>"));
	}
}
