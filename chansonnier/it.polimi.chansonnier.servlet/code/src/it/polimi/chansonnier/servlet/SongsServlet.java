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
        response.setContentType("text/html;charset=UTF-8");
		PrintWriter writer = response.getWriter();
		writer.println("Hello from AddServlet of it.polimi.chansonnier.servlet...");
	
        writer.println("<html><head></head><body>");
        /*
        <style type="text/css">
        dl {
        
        }
        dt {
            font-weight: bolder;
            width: 100px;
        }
        dd {
        }
        </style>
        */
        List<Id> result = (List<Id>) request.getAttribute("result");
        Blackboard blackboard = (Blackboard) request.getAttribute("blackboard");
        try {
            if (result.size() > 0) {
                writer.println("<h1>Search results</h1>");
                writer.println("<dl>");
                for (Id id : result) {
                    String link = id.getKey().getKey();
                    writer.println(_printField(blackboard, id, "Title", "title"));
                    writer.println(_printField(blackboard, id, "Artist", "artist"));
                    writer.println(_printField(blackboard, id, "Lyrics", "lyrics"));
                    writer.println(_printField(blackboard, id, "Emotion", "emotion"));
                    writer.println("<dt class=\"key\">Link</dt>");
                    writer.println("<dd class=\"key\">" + link + "</dd>");
                    writer.println("<dt class=\"image\">Image</dt>");
                    writer.println("<dd class=\"image\"><img src=\"attachment?id=" + link + "\" /></dt>");
                }
                writer.println("</dl>");
            } else {
                writer.println("<p>No results.</p>");
            }
        }catch (Exception e){
            writer.println(e.getMessage());
        }
        writer.println("</body></html>");
    }

	private String _printField(Blackboard blackboard, Id id, String attributeName, String cssClass) throws BlackboardAccessException {
		String attributeValue = blackboard.getLiteral(id, new Path(attributeName)).toString();
		String result = "<dt class=\"" + cssClass + "\">" + attributeName + "</dt>\n<dd class=\"" + cssClass + "\">" + attributeValue + "</dd>\n";
        return result;
	}
}
