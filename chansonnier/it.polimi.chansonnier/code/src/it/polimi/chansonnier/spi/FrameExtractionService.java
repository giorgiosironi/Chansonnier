/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.spi;

import java.io.File;

public interface FrameExtractionService {

    /**
     * @param String time   "00:00:10"
     */
	File getImage(File video, String time);

}
