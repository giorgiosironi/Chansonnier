/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import org.eclipse.smila.processing.*;
import org.eclipse.smila.blackboard.*;
import org.eclipse.smila.blackboard.path.*;
import org.eclipse.smila.datamodel.id.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SongsServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter writer = response.getWriter();
        String format = request.getParameter("format");
        if (format == null) {
            format = "html";
        }
        if (format.equals("html")) {
            response.setContentType("text/html;charset=UTF-8");
        } else if (format.equals("xml")) {
            response.setContentType("text/xml");
            writer.println("<?xml version=\"1.0\"?>");
            writer.println("<songs>");
        } else {
            writer.println("<html><head>");
            writer.println("<title>" + request.getAttribute("title") + "</title>");
            writer.println("<link rel=\"Stylesheet\" href=\"style.css\" type=\"text/css\" media=\"screen\" />");
            writer.println("</head><body>");
        }
        
        List<Id> result = (List<Id>) request.getAttribute("result");
        Blackboard blackboard = (Blackboard) request.getAttribute("blackboard");
        try {
            if (result.size() > 0) {
                if (format.equals("html")) {
                    writer.println("<h1>Search results</h1>");
                    writer.println("<dl>");
                }
                for (Id id : result) {
                    String link = id.getKey().getKey();
                    if (format.equals("html")) {
                        writer.println(_printField(blackboard, id, "Title", "title"));
                        writer.println(_printField(blackboard, id, "Artist", "artist"));
                        writer.println(_printField(blackboard, id, "Lyrics", "lyrics"));
                        writer.println(_printField(blackboard, id, "Emotion", "emotion"));
                        writer.println("<dt class=\"key\">Link</dt>");
                        writer.println("<dd class=\"key\">" + link + "</dd>");
                        writer.println("<dt class=\"image\">Image</dt>");
                        writer.println("<dd class=\"image\">");
                        int i = 1;
                        while (blackboard.hasAttachment(id, "Image" + i)) {
                            
                            writer.println("<img src=\"attachment?name=Image" + i + "&id=" + link + "\" />");
                            i++;
                        }
                        writer.println("</dd>");
                    } else{
                        writer.println("<song>");
                        writer.println("<title>" + getValue(blackboard, id, "Title") + "</title>");
                        writer.println("<artist>" + getValue(blackboard, id, "Artist") + "</artist>");
                        writer.println("<emotion>" + getValue(blackboard, id, "Emotion") + "</emotion>");
                        writer.println("<lyrics>" + getValue(blackboard, id, "Lyrics") + "</lyrics>");
                        writer.println("</song>");
                    }
                }
                if (format.equals("html")) {
                    writer.println("</dl>");
                }
            } else {
                if (format.equals("html")) {
                    writer.println("<p>No results.</p>");
                }
            }
        }catch (Exception e){
            writer.println(e.getMessage());
        }
        if (format.equals("html")) {
            writer.println("</body></html>");
        } else {
            writer.println("</songs>");
        }
    }

	private String _printField(Blackboard blackboard, Id id, String attributeName, String cssClass) throws BlackboardAccessException {
		String attributeValue = getValue(blackboard, id, attributeName);
        String result = "<dt class=\"" + cssClass + "\">" + attributeName + "</dt>\n<dd class=\"" + cssClass + "\">" + attributeValue + "</dd>\n";
        return result;
	}

    private String getValue(Blackboard blackboard, Id id, String attributeName) throws BlackboardAccessException {
        return blackboard.getLiteral(id, new Path(attributeName)).toString();
    }
}
