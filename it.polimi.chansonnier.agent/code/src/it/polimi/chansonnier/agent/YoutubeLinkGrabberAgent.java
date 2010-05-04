package it.polimi.chansonnier.agent;


import java.util.LinkedList;
import java.util.Queue;

import org.eclipse.smila.connectivity.framework.AbstractAgent;
import org.eclipse.smila.connectivity.framework.AgentException;
import org.eclipse.smila.connectivity.framework.AgentState;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;
import org.eclipse.smila.connectivity.framework.util.AgentControllerCallback;
import org.eclipse.smila.connectivity.framework.util.AgentThreadState;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;

public class YoutubeLinkGrabberAgent extends AbstractAgent implements LinkGrabberAgent {
	RecordFactory _factory = RecordFactory.DEFAULT_INSTANCE;
	Queue<String> _linksToProcess = new LinkedList<String>();
	
	@Override
	public void addLink(String link) {
		_linksToProcess.add(link);		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
		      while (!isStopThread()) {
		    	  if (!_linksToProcess.isEmpty()) {
		    		  System.out.println("Test...");
		    		  String newLink = _linksToProcess.poll();
		        	Record newRecord = _factory.createRecord();
		        	getControllerCallback().add(null, null, newRecord, null);
		    	  }
		      } // while
		    } catch (Throwable t) {
		      getAgentState().setLastError(t);
		      getAgentState().setState(AgentThreadState.Aborted);
		      throw new RuntimeException(t);
		    } finally {
		      try {
		        stop();
		      } catch (Exception e) {
		        throw new RuntimeException(e);
		      }
		    }

	}

	@Override
	protected void initialize() throws AgentException {
		// TODO Auto-generated method stub
		
	}
}
