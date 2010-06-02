/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;

public class SearchServlet extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {
	    String lyrics = request.getParameter("lyrics");
	    String emotion = request.getParameter("emotion");
	    if (lyrics != null || emotion != null) {
	    	ChansonnierSearchService customSearchService = new ChansonnierSearchService();
	    	customSearchService.setSearchService(Activator.getSearchService());
			
			try {
                List<Id> result;
                if (lyrics != null) {
                    result = customSearchService.search("Lyrics", lyrics);
                } else {
                    result = customSearchService.search("Emotion", emotion);
                }
                request.setAttribute("result", result);
                request.setAttribute("title", "Search result");
				Blackboard blackboard = Activator.getBlackboardFactory().createPersistingBlackboard();
                request.setAttribute("blackboard", blackboard);
                getServletContext().getRequestDispatcher("/songs").forward(request, response);
			} catch (BlackboardAccessException e) {
				response.getWriter().println(e);
			} catch (ProcessingException e) {
				response.getWriter().println(e);
			}
	    }
	}
}
