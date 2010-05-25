package it.polimi.chansonnier.test.functional;

import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.processing.bpel.test.AWorkflowProcessorTest;

public class DummyPipelineTest extends AWorkflowProcessorTest {
	  /**
	   * name of pipeline to test.
	   */
	  public static final String PIPELINE_NAME = "DummyPipeline";

	  /**
	   * {@inheritDoc}
	   * 
	   * @see org.eclipse.smila.processing.bpel.test.AWorkflowProcessorTest#getPipelineName()
	   */
	  @Override
	  protected String getPipelineName() {
	    return PIPELINE_NAME;
	  }

	  /**
	   * test code.
	   * 
	   * @throws Exception
	   *           test fails
	   */
	  public void testEchoID() throws Exception {
	      final Id request = createBlackboardRecord("source", "key");
	      final Id[] result = getProcessor().process(PIPELINE_NAME, getBlackboard(), new Id[] { request });
	      assertEquals(1, result.length);
	      assertEquals(request, result[0]);
	  }
	  
	  public void testTouchesFilenameAttribute() throws Exception {
		  final Id request = createBlackboardRecord("source", "key");
		  final Literal filenameValue = getBlackboard().createLiteral(request);
		  filenameValue.setStringValue("readme.txt");
		  final Path filenamePath = new Path("Filename");
		  getBlackboard().setLiteral(request, filenamePath, filenameValue);
		  final Id[] result = getProcessor().process(PIPELINE_NAME, getBlackboard(), new Id[] { request });
		  assertEquals(1, result.length);
		  assertTrue(getBlackboard().hasAttribute(result[0], filenamePath));
		  assertEquals(1, getBlackboard().getLiteralsSize(result[0], filenamePath));
		  assertEquals("readme.txt ...edited by DummyProcessingService", getBlackboard().getLiteral(result[0], filenamePath).toString());
	  }

}
