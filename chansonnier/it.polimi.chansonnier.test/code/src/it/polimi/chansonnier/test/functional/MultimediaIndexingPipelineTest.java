package it.polimi.chansonnier.test.functional;

import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.processing.bpel.test.AWorkflowProcessorTest;

public class MultimediaIndexingPipelineTest extends AWorkflowProcessorTest {
	public static final String PIPELINE_NAME = "MultimediaIndexingPipeline";
	
	@Override
	protected String getPipelineName() {
	    return PIPELINE_NAME;
	}

	public void testNothing() throws Exception {
		Id stairwayToHeaven = createSong();
		final Id[] result = getProcessor().process(PIPELINE_NAME, getBlackboard(), new Id[] { stairwayToHeaven });
		  assertEquals(1, result.length);
		  //assertTrue(getBlackboard().hasAttribute(result[0], filenamePath));
		  //assertEquals(1, getBlackboard().getLiteralsSize(result[0], filenamePath));
		  //assertEquals("readme.txt ...edited by DummyProcessingService", getBlackboard().getLiteral(result[0], filenamePath).toString());

		assertTrue(true);
	}
	
	public Id createSong() throws Exception {
		final Id song = createBlackboardRecord("source", "key");
		final Literal pageTitle = getBlackboard().createLiteral(song);
		pageTitle.setStringValue("Led Zeppelin - Stairway to heaven");
		getBlackboard().setLiteral(song, new Path("PageTitle"), pageTitle);
		return song;
	}
}
