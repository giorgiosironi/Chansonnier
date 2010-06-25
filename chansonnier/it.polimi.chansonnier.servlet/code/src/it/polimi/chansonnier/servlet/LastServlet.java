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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.smila.datamodel.id.Id;

public class LastServlet extends AbstractServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter writer = response.getWriter();
		printHeader(response, "Last songs added");
		writer.println("<p>This is the list of the last songs added to the index.</p>");
		String lastSongTitle = Activator.getLastIndexedService().getLastTitle();
		response.getWriter().println("<p>The last song added was <em>" + lastSongTitle + "</em></p>");
		response.getWriter().println("<p>All the songs added during this session are listed.</p>");
        List<Id> lastSongs = Activator.getLastIndexedService().getLastSongs();
        response.getWriter().println("<ul>");
        for (Id id : lastSongs) {
        	String key = id.getKey().getKey();
        	response.getWriter().println("<li><a href=\"" + key + "\">" + key + "</a></li>");
        }
        response.getWriter().println("</ul>");
        printFooter(response);
    }
}
