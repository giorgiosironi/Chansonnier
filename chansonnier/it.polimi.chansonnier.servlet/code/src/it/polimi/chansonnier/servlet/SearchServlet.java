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
import org.eclipse.smila.datamodel.id.Id;;

public class SearchServlet extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {
	    String lyrics = request.getParameter("lyrics");
	    if (lyrics != null) {
	    	ChansonnierSearchService customSearchService = new ChansonnierSearchService();
	    	customSearchService.setSearchService(Activator.getSearchService());
			
			try {
				List<Id> result = customSearchService.search("Lyrics", lyrics);
				Blackboard blackboard = Activator.getBlackboardFactory().createPersistingBlackboard();
				if (result.size() > 0) {
					response.getWriter().println("<h1>Search results</h1>");
					response.getWriter().println("<dl>");
			    	for (Id id : result) {
			    		String link = id.getKey().toString();
			    		_printField(response.getWriter(), blackboard, id, "Title", "title");
			    		_printField(response.getWriter(), blackboard, id, "Artist", "artist");
			    		_printField(response.getWriter(), blackboard, id, "Lyrics", "lyrics");
			    		_printField(response.getWriter(), blackboard, id, "Emotion", "emotion");
			    		response.getWriter().println("<dt class=\"key\">Link</dt>");
			    		response.getWriter().println("<dd class=\"key\">" + link + "</dd>");
					}
			    	response.getWriter().println("</dl>");
				} else {
					response.getWriter().write("<p>No results.</p>");
				}
			} catch (BlackboardAccessException e) {
				response.getWriter().println(e);
			} catch (ProcessingException e) {
				response.getWriter().println(e);
			}
		    response.setContentType("text/html;charset=UTF-8");
	    }
	}
	
	private void _printField(PrintWriter writer, Blackboard blackboard, Id id, String attributeName, String cssClass) throws BlackboardAccessException {
		String attributeValue = blackboard.getLiteral(id, new Path(attributeName)).toString();
		writer.println("<dt class=\"" + cssClass + "\">" + attributeName + "</dt>");
		writer.println("<dd class=\"" + cssClass + "\">" + attributeValue + "</dd>");
	}
}
