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
		final Id id = createBlackboardRecord("source", "id");
		setAttribute(id, new Path("Title"), TITLE);
		setAttribute(id, new Path("Artist"), ARTIST);
    
		Id[] result = _service.process(getBlackboard(), new Id[] { id });
		
		assertEquals(1, result.length);
		assertEquals(TITLE, _service.getLastTitle());
	}
}
