package it.polimi.chansonnier.core.test;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;

import it.polimi.chansonnier.fixtures.Fixtures;
import it.polimi.chansonnier.processing.FrameExtractionProcessingService;
import it.polimi.chansonnier.spi.FrameExtractionService;
import it.polimi.chansonnier.utils.URLUtils;

public class FrameExtractionProcessingServiceTest extends ProcessingServiceTest implements FrameExtractionService {
	private FrameExtractionProcessingService _service;

	@Override
	protected void init() throws Exception {
		_service = new FrameExtractionProcessingService();
		_service.setFrameExtractionService(this);
	}
	
	public void testExtractsThreeFrameAtRegularIntervals() throws Exception {
		Id id = createBlackboardRecord("youtube", "http://www.youtube.com/dummy");
		setAttachment(id, "original", Fixtures.getAsFile("hero.flv"));
		
		_service.process(getBlackboard(), new Id[] { id });
		
		List<Literal> attachmentNames = getAttributes(id, new Path("image"));
		assertEquals(3, attachmentNames.size());
		for (Literal name : attachmentNames) {
			File png = getAttachment(id, name.getStringValue());
			assertEquals(URLUtils.readStart(Fixtures.getAsFile("hero_frame.png")), URLUtils.readStart(png));
		}
	}

	@Override
	public File getImage(File video, String time) {
		try {
			return Fixtures.getAsFile("hero_frame.png");
		} catch (IOException e) {
			fail(e.getMessage());
		}
		return null;
	}

}
