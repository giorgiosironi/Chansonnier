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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.smila.connectivity.ConnectivityException;

public class AddServlet extends AbstractServlet {
	/**
	 * 0.1.0 version.
	 */
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		printHeader(response, "Add a song");
		PrintWriter writer = response.getWriter();
		writer.println("<p>From this page you can add a link to a page containing a video to the processing queue.</p>");
        writer.println("<form action=\"add\" method=\"post\">");
        writer.println("<fieldset>");
        writer.println("<label>Video link: <input type=\"text\" name=\"link\" /></label>");
        writer.println("</fieldset>");
        writer.println("<button type=\"submit\">Add to queue</button>");
        writer.println("</form");
        printFooter(response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String link = request.getParameter("link");
			LinkGrabberAgent _agent;
			try {
				_agent = Activator.getLinkGrabberAgent();
			} catch (ConnectivityException e) {
				throw new ServletException(e);
			}
			printHeader(response, "Add a song");
			response.getWriter().println("<p>The link <a href=\"" + link + "\">" +link + "</a> was added to the queue.</p>");
			response.getWriter().println("<p>You may want to monitor the <a href=\"last\">last indexed songs page</a> to discover when its processing phase has been completed.</p>");
			printFooter(response);
			_agent.addLink(link);
	}
}
