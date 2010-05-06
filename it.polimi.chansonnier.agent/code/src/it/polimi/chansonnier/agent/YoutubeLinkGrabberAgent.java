package it.polimi.chansonnier.agent;


import java.util.LinkedList;
import java.util.Queue;

import org.eclipse.smila.connectivity.framework.AbstractAgent;
import org.eclipse.smila.connectivity.framework.AgentException;
import org.eclipse.smila.connectivity.framework.util.AgentThreadState;
import org.eclipse.smila.connectivity.framework.util.ConnectivityIdFactory;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.datamodel.record.impl.AttributeImpl;
import org.eclipse.smila.datamodel.record.impl.LiteralImpl;

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
		        	Attribute[] idAttributes = new Attribute[1];
		        	idAttributes[0] = new AttributeImpl();
		        	idAttributes[0].setName("key");
		        	Literal idL = new LiteralImpl(); 
		        	idL.setStringValue("42");
		        	idAttributes[0].addLiteral(idL);
		        	 final Id id =
		        	      ConnectivityIdFactory.getInstance().createId(getConfig().getDataSourceID(), idAttributes);
		        	newRecord.setId(id);
		            final MObject metadata = _factory.createMetadataObject();
		            newRecord.setMetadata(metadata);
		            Attribute attribute = new AttributeImpl();
		            attribute.setName("PageTitle");
		            Literal l = new LiteralImpl();
		            l.setStringValue("U2 - With or without you");
		            attribute.addLiteral(l);
		            newRecord.getMetadata().setAttribute("PageTitle", attribute);
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
