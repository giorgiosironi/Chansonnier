/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.driver.lyricwiki;

public interface LyricWikiBackend {

	/**
	 * @param title
	 * @param artist
	 * @return representation of song result in some format (XML, JSON, ...)
	 */
	String getSong(String title, String artist);

}
