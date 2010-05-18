package it.polimi.chansonnier.agent.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import it.polimi.chansonnier.agent.LinkGrabberAgent;
import it.polimi.chansonnier.agent.URLUtils;
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
	  
	  private static final String LINK = "http://www.youtube.com/watch?v=e7K1A0bh9Cs";

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
	 * @throws IOException 
	   * 
	   * @see AgentControllerCallback#add(String, DeltaIndexingType, Record, String)
	   */
	  public void add(final String sessionId, final DeltaIndexingType deltaIndexingType, final Record record, final String hash) {
	    assertNotNull(record);
	    assertEquals(_config.getDataSourceID(), record.getId().getSource());
	    // TODO: maybe the link saved should be the rev="canonical" value
	    assertEquals(LINK, record.getId().getKey().getKey());
		assertEquals("See Ya In Anotha Life, Brotha !", record.getMetadata().getAttribute("PageTitle").getLiteral().toString());
		assertEquals("Favorite quote from the favorite quasi-nuts character of Lost", record.getMetadata().getAttribute("Description").getLiteral().toString());
		assertEquals("desmond, lost, see, you, in, another, life, brother, ya, anotha, brotha", record.getMetadata().getAttribute("Keywords").getLiteral().toString());
		
		try {
			byte[] attachment = record.getAttachment("Original");
			assertNotNull(attachment);
			assertEquals(URLUtils.readStart("test/flv/desmond.flv"),
						 URLUtils.readStart(new ByteArrayInputStream(attachment)));
		} catch (Exception e) {
			fail(e.toString());
		}
		
		
	    _addCount++;
	  }

	  /**
	   * {@inheritDoc}
	   * 
	   * @see AgentControllerCallback#delete(String, DeltaIndexingType, Id)
	   */
	  public void delete(final String sessionId, final DeltaIndexingType deltaIndexingType, final Id id) {
		  assertTrue(false);
	    // should never be called in this test
	  }

	  /**
	   * {@inheritDoc}
	   * 
	   * @see AgentControllerCallback#unregister(String, DeltaIndexingType, String)
	   */

	  public void unregister(final String sessionId, final DeltaIndexingType deltaIndexingType, String dataSourceId) {
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

	  public void testAgent() throws Exception {
	    assertEquals(0, _addCount);

	    _agent.addLink(LINK);
	    _agent.start(this, new AgentState(), _config, "dummy_session_id");
	    Thread.sleep(PAUSE);
	    _agent.stop();
	    Thread.sleep(PAUSE);

	    assertEquals(1, _addCount);
	  }

}
