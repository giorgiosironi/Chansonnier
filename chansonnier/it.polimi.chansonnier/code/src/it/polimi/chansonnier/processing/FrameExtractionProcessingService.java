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
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.ProcessingService;

public class FrameExtractionProcessingService extends AbstractProcessingService implements ProcessingService {

	private FrameExtractionService frameExtractionService;

	@Override
	public Id[] process(Blackboard blackboard, Id[] recordIds)
			throws ProcessingException {
		try {
			for (Id id : recordIds) {
				String outputPath = getOutputPath(blackboard, id);
				System.out.println(outputPath);
				
				File original = blackboard.getAttachmentAsFile(id, getInputPath(blackboard, id));
				File frame = frameExtractionService.getImage(original, "00:00:10");
				addImageAttribute(blackboard, id, outputPath + "1");
				blackboard.setAttachmentFromFile(id, outputPath + "1", frame);
				
				File frame2 = frameExtractionService.getImage(original, "00:00:30");
				addImageAttribute(blackboard, id, outputPath + "2");
				blackboard.setAttachmentFromFile(id, outputPath + "2", frame2);
				
				File frame3 = frameExtractionService.getImage(original, "00:00:50");
				addImageAttribute(blackboard, id, outputPath + "3");
				blackboard.setAttachmentFromFile(id, outputPath + "3", frame3);
			}
		} catch (BlackboardAccessException e) {
			throw new ProcessingException(e);
		}
		return recordIds;
	}

	private void addImageAttribute(Blackboard blackboard, Id id, String attachmentName) throws BlackboardAccessException {
		Literal literal = blackboard.createLiteral(id);
		literal.setStringValue(attachmentName);
		blackboard.addLiteral(id, new Path(getOutputPath(blackboard, id)), literal);
	}

	public void setFrameExtractionService(
			FrameExtractionService shotDetectionService) {
		frameExtractionService = shotDetectionService;
		
	}

}
