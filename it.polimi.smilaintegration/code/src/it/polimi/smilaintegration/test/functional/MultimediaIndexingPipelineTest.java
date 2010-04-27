package it.polimi.smilaintegration.test.functional;

import org.eclipse.smila.processing.bpel.test.AWorkflowProcessorTest;

public class MultimediaIndexingPipelineTest extends AWorkflowProcessorTest {
	public static final String PIPELINE_NAME = "MultimediaIndexingPipeline";
	
	@Override
	protected String getPipelineName() {
	    return PIPELINE_NAME;
	}

	public void testNothing() {
		// TODO
		assertTrue(true);
	}
}
