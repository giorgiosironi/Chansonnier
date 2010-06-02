/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.servlet;

import it.polimi.chansonnier.agent.LinkGrabberAgent;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.smila.connectivity.ConnectivityException;

public class AddServlet extends HttpServlet {
	/**
	 * 0.1.0 version.
	 */
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter writer = response.getWriter();
        writer.println("<form action=\"add\" method=\"post\">");
        writer.println("<label>Video link: <input type=\"text\" name=\"link\" /></label>");
        writer.println("<button type=\"submit\">Add to queue</button>");
        writer.println("</form");
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String link = request.getParameter("link");
			LinkGrabberAgent _agent = Activator.linkGrabberAgent;
			if (_agent == null) {
				try {
					Activator.agentController.startAgent("youtube");
				} catch (ConnectivityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				_agent = Activator.linkGrabberAgent;
			}
			if (_agent == null) {	
				throw new ServletException("LinkGrabberAgent is not initialized.");
			}
			response.getWriter().println("Success...");
			_agent.addLink(link);
	}
}
