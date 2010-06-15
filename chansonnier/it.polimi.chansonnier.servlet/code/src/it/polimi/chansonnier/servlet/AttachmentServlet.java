/****************************************************************************
 * Copyright (c) 2010 Giorgio Sironi. All rights reserved.
 * This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package it.polimi.chansonnier.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.id.impl.IdImpl;
import org.eclipse.smila.datamodel.id.impl.KeyImpl;

public class AttachmentServlet extends HttpServlet {
	private IdFactory idFactory = IdFactory.DEFAULT_INSTANCE;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {
        response.setContentType("image/png");
	    String key = request.getParameter("id");
	    String name = request.getParameter("name");
        try {
            Id id = idFactory.createId("youtube", key);
            Blackboard blackboard = Activator.getBlackboard();
            InputStream is = blackboard.getAttachmentAsStream(id, name);
            OutputStream o = response.getOutputStream();
            byte[] buf = new byte[32 * 1024]; // 32k buffer
            int nRead = 0;
            while( (nRead=is.read(buf)) != -1 ) {
                o.write(buf, 0, nRead);
            }
            o.flush();
            o.close();
        } catch (BlackboardAccessException e) {
            response.getWriter().println(e);
        } catch (IOException e) {
            response.getWriter().println(e);
        }
	}
}
