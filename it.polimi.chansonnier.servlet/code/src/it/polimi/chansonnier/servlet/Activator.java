package it.polimi.chansonnier.servlet;

import it.polimi.chansonnier.agent.LinkGrabberAgent;
import it.polimi.chansonnier.processing.LastIndexedService;

import org.eclipse.smila.connectivity.framework.AgentController;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {
	private ServiceTracker _agentControllerTracker;
	private ServiceTracker _agentTracker;
	private ServiceTracker _lastIndexedTracker;
	public static AgentController agentController;
	public static LastIndexedService lastIndexedService; 
	public static LinkGrabberAgent linkGrabberAgent;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		_agentControllerTracker = new AgentControllerTracker(context);
		_agentControllerTracker.open();
		_agentTracker = new AgentTracker(context);
		_agentTracker.open();
		_lastIndexedTracker = new LastIndexedTracker(context);
		_lastIndexedTracker.open();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	}
	
	
	class AgentControllerTracker extends ServiceTracker
	{
		public AgentControllerTracker(BundleContext context) {
			super(context, AgentController.class.getName(), null);
		}
		
		public Object addingService(ServiceReference reference) {
			agentController = (AgentController) context.getService(reference);
			return agentController;
		}
	}
	
	class AgentTracker extends ServiceTracker
	{
		public AgentTracker(BundleContext context) {
			super(context, LinkGrabberAgent.class.getName(), null);
		}
		
		public Object addingService(ServiceReference reference) {
			linkGrabberAgent = (LinkGrabberAgent) context.getService(reference);
			return linkGrabberAgent;
		}
	}

	class LastIndexedTracker extends ServiceTracker
	{
		public LastIndexedTracker(BundleContext context) {
			super(context, LastIndexedService.class.getName(), null);
		}
		
		public Object addingService(ServiceReference reference) {
			lastIndexedService = (LastIndexedService) context.getService(reference);
			return lastIndexedService;
		}
	}

}
