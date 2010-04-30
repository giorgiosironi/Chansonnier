package it.polimi.chansonnier.test.unit;

import it.polimi.chansonnier.LinkGrabberAgent;
import it.polimi.chansonnier.YoutubeLinkGrabberAgent;

import org.eclipse.smila.test.DeclarativeServiceTestCase;

public class YoutubeLinkGrabberAgentTest extends DeclarativeServiceTestCase {
	LinkGrabberAgent _agent;
	
	public void setUp() {
		_agent = new YoutubeLinkGrabberAgent();
	}
}
