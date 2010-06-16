/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.processing;

import it.polimi.chansonnier.spi.FrameExtractionService;

import java.io.File;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.ProcessingService;

public class FrameExtractionProcessingService implements ProcessingService {

	private FrameExtractionService frameExtractionService;

	@Override
	public Id[] process(Blackboard blackboard, Id[] recordIds)
			throws ProcessingException {
		try {
			for (Id id : recordIds) {
				File original = blackboard.getAttachmentAsFile(id, "original");
				File shot = frameExtractionService.getImage(original, "00:00:10");
				blackboard.setAttachmentFromFile(id, "image1", shot);
				File shot2 = frameExtractionService.getImage(original, "00:00:30");
				blackboard.setAttachmentFromFile(id, "image2", shot2);
				File shot3 = frameExtractionService.getImage(original, "00:00:50");
				blackboard.setAttachmentFromFile(id, "image3", shot3);
			}
		} catch (BlackboardAccessException e) {
			throw new ProcessingException(e);
		}
		return recordIds;
	}

	public void setFrameExtractionService(
			FrameExtractionService shotDetectionService) {
		frameExtractionService = shotDetectionService;
		
	}

}
