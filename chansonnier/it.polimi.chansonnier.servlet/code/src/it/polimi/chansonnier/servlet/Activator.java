/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.servlet;

import it.polimi.chansonnier.agent.LinkGrabberAgent;
import it.polimi.chansonnier.processing.LastIndexedService;

import org.eclipse.smila.blackboard.BlackboardFactory;
import org.eclipse.smila.connectivity.framework.AgentController;
import org.eclipse.smila.search.api.SearchService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {
	private ServiceTracker _agentControllerTracker;
	private ServiceTracker _agentTracker;
	private ServiceTracker _lastIndexedTracker;
	private static ServiceTracker _blackboardFactoryTracker;
	private static ServiceTracker _searchServiceTracker;
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
	    _searchServiceTracker = new ServiceTracker(context, SearchService.class.getName(), null);
	    _searchServiceTracker.open();   
	    _blackboardFactoryTracker = new ServiceTracker(context, BlackboardFactory.class.getName(), null);
	    _blackboardFactoryTracker.open();
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
	
	public static SearchService getSearchService() {
		return (SearchService) _searchServiceTracker.getService();
	}
	
	public static BlackboardFactory getBlackboardFactory() {
		return (BlackboardFactory) _blackboardFactoryTracker.getService();
	}

}
