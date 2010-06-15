/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.servlet;

import javax.servlet.ServletException;

import it.polimi.chansonnier.agent.LinkGrabberAgent;
import it.polimi.chansonnier.processing.LastIndexedService;
import it.polimi.chansonnier.utils.FixtureManager;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.BlackboardFactory;
import org.eclipse.smila.connectivity.ConnectivityException;
import org.eclipse.smila.connectivity.framework.AgentController;
import org.eclipse.smila.processing.WorkflowProcessor;
import org.eclipse.smila.search.api.SearchService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {
	private static ServiceTracker _agentControllerTracker;
	private static ServiceTracker _agentTracker;
	private static ServiceTracker _lastIndexedTracker;
	private static ServiceTracker _blackboardFactoryTracker;
	private static ServiceTracker _searchServiceTracker;
	private static ServiceTracker _workflowProcessorTracker;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		_agentControllerTracker = new ServiceTracker(context, AgentController.class.getName(), null);
		_agentControllerTracker.open();
		_agentTracker = new ServiceTracker(context, LinkGrabberAgent.class.getName(), null);
		_agentTracker.open();
		_lastIndexedTracker = new ServiceTracker(context, LastIndexedService.class.getName(), null);
		_lastIndexedTracker.open();
	    _searchServiceTracker = new ServiceTracker(context, SearchService.class.getName(), null);
	    _searchServiceTracker.open();   
	    _blackboardFactoryTracker = new ServiceTracker(context, BlackboardFactory.class.getName(), null);
	    _blackboardFactoryTracker.open();
	    _workflowProcessorTracker = new ServiceTracker(context, WorkflowProcessor.class.getName(), null);
	    _workflowProcessorTracker.open();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	}
	
	private static AgentController getAgentController() {
		return (AgentController) _agentControllerTracker.getService();
	}

	public static LinkGrabberAgent getLinkGrabberAgent() throws ServletException, ConnectivityException {
		LinkGrabberAgent agent = (LinkGrabberAgent) _agentTracker.getService();
		if (agent == null) {
			getAgentController().startAgent("youtube");
			agent = Activator.getLinkGrabberAgent();
		}
		if (agent == null) {	
			throw new ServletException("LinkGrabberAgent is not initialized.");
		}
		return agent;
	}
	
	public static LastIndexedService getLastIndexedService() {
		return (LastIndexedService) _lastIndexedTracker.getService();
	}
	
	public static SearchService getSearchService() {
		return (SearchService) _searchServiceTracker.getService();
	}
	
	private static BlackboardFactory getBlackboardFactory() {
		return (BlackboardFactory) _blackboardFactoryTracker.getService();
	}

	public static Blackboard getBlackboard() throws BlackboardAccessException {
		return getBlackboardFactory().createPersistingBlackboard();
	}
	
	public static FixtureManager getFixtureManager(String pipelineName) throws BlackboardAccessException {
		return new FixtureManager(getWorkflowProcessor(), getBlackboard(), pipelineName);
	}

	private static WorkflowProcessor getWorkflowProcessor() {
		return (WorkflowProcessor) _workflowProcessorTracker.getService();
	}

}
