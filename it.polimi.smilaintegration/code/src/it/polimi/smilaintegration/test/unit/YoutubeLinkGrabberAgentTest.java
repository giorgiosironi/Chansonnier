package it.polimi.smilaintegration.test.unit;

import it.polimi.smilaintegration.LinkGrabberAgent;
import it.polimi.smilaintegration.YoutubeLinkGrabberAgent;

import org.eclipse.smila.test.DeclarativeServiceTestCase;

public class YoutubeLinkGrabberAgentTest extends DeclarativeServiceTestCase {
	LinkGrabberAgent _agent;
	
	public void setUp() {
		_agent = new YoutubeLinkGrabberAgent();
	}
}
