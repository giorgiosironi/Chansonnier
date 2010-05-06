package it.polimi.chansonnier.agent.test;

import it.polimi.chansonnier.agent.LinkGrabberAgent;
import it.polimi.chansonnier.agent.YoutubeLinkGrabberAgent;

import org.eclipse.smila.connectivity.framework.AgentState;
import org.eclipse.smila.connectivity.framework.schema.ConfigurationLoader;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;
import org.eclipse.smila.connectivity.framework.schema.config.DeltaIndexingType;
import org.eclipse.smila.connectivity.framework.util.AgentControllerCallback;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

public class LinkGrabberAgentTest extends DeclarativeServiceTestCase implements AgentControllerCallback  {
	  /**
	   * timeout for service detection.
	   */
	  private static final long WAIT_FOR_SERVICE_DELAY = 30000;

	  /**
	   * The Constant PAUSE.
	   */
	  private static final int PAUSE = 10000;

	  /**
	   * the Crawler.
	   */
	  private LinkGrabberAgent _agent;

	  /**
	   * The data source configuration.
	   */
	  private DataSourceConnectionConfig _config;

	  /**
	   * The unregistered flag.
	   */
	  private boolean _unregistered;

	  /**
	   * Add counter.
	   */
	  private int _addCount;

	  /**
	   * {@inheritDoc}
	   * 
	   * @see junit.framework.TestCase#setUp()
	   */
	  @Override
	  protected void setUp() throws Exception {
		  System.out.println("SetUp");
	    forceStartBundle("org.eclipse.smila.connectivity.impl");
		  System.out.println("Connectivity component started");
		  _config = ConfigurationLoader.unmarshall(LinkGrabberAgentTest.class.getResourceAsStream("ConfigExample.xml"));

	    // register the service, because it's configuration uses immediate="false"
	    _agent = registerService(new YoutubeLinkGrabberAgent(), null, YoutubeLinkGrabberAgent.class, WAIT_FOR_SERVICE_DELAY);
	    System.out.println("Service registered");
	    System.out.println("Agent running");
	    assertNotNull(_agent);
	    
	    assertNotNull(_config);
	    Thread.sleep(PAUSE);
	  }

	  /**
	   * {@inheritDoc}
	   * 
	   * @see junit.framework.TestCase#tearDown()
	   */
	  @Override
	  protected void tearDown() throws Exception {
	  }

	  /**
	   * {@inheritDoc}
	   * 
	   * @see AgentControllerCallback#add(String, DeltaIndexingType, Record, String)
	   */
	  public void add(final String sessionId, final DeltaIndexingType deltaIndexingType, final Record record, final String hash) {
	    //assertNotNull(record);
	    //assertEquals(_config.getDataSourceID(), record.getId().getSource());
		  assertEquals("U2 - With or without you", record.getMetadata().getAttribute("PageTitle").getLiteral().toString());
	    _addCount++;
	  }

	  /**
	   * {@inheritDoc}
	   * 
	   * @see AgentControllerCallback#delete(String, DeltaIndexingType, Id)
	   */
	  public void delete(final String sessionId, final DeltaIndexingType deltaIndexingType, final Id id) {
	    // should never be called in this test
	    //assertNotNull(id);
	    //assertEquals(_config.getDataSourceID(), id.getSource());
	  }

	  /**
	   * {@inheritDoc}
	   * 
	   * @see AgentControllerCallback#unregister(String, DeltaIndexingType, String)
	   */

	  public void unregister(final String sessionId, final DeltaIndexingType deltaIndexingType, String dataSourceId) {
	    //assertNotNull(dataSourceId);
	    //assertEquals(_config.getDataSourceID(), dataSourceId);
	    //_unregistered = true;
	  }

	  /**
	   * {@inheritDoc}
	   * 
	   * @see AgentControllerCallback#doCheckForUpdate(DeltaIndexingType)
	   */

	  public boolean doCheckForUpdate(final DeltaIndexingType deltaIndexingType) {
	    return true;
	  }

	  /**
	   * {@inheritDoc}
	   * 
	   * @see AgentControllerCallback#doDeltaIndexing(DeltaIndexingType)
	   */
	  public boolean doDeltaIndexing(final DeltaIndexingType deltaIndexingType) {
	    return true;
	  }

	  /**
	   * {@inheritDoc}
	   * 
	   * @see AgentControllerCallback#doDeltaDelete(DeltaIndexingType)
	   */
	  public boolean doDeltaDelete(final DeltaIndexingType deltaIndexingType) {
	    return true;
	  }

	  /**
	   * Test crawler.
	   * 
	   * @throws Exception
	   *           the exception
	   */
	  public void testAgent() throws Exception {
	    assertEquals(0, _addCount);
	    assertFalse(_unregistered);

	    _agent.addLink("http://www.youtube.com/watch?v=e7K1A0bh9Cs");
	    System.out.println("1");
	    _agent.start(this, new AgentState(), _config, "dummy_session_id");
	    System.out.println("2");
	    Thread.sleep(PAUSE);
	    System.out.println("3");
	    _agent.stop();
	    System.out.println("4");
	    Thread.sleep(PAUSE);

	    assertEquals(1, _addCount);
	    //assertTrue(_unregistered);
	  }

}
