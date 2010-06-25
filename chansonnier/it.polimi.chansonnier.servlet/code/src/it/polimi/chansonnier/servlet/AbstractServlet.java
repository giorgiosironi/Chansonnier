package it.polimi.chansonnier.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractServlet extends HttpServlet {

	public AbstractServlet() {
		super();
	}

	protected void printHeader(HttpServletResponse response, String title) throws IOException {
		response.getWriter().println("<html><head>");
		response.getWriter().println("<title>" + title + "</title>");
		response.getWriter().println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" media=\"screen\" />");
		response.getWriter().println("</head><body>");
		response.getWriter().println("<div id=\"wrap\">"); 
		response.getWriter().println("<div id=\"header\"><h1>Chansonnier</h1></div>");
	}

	protected void printFooter(HttpServletResponse response) throws IOException {
		response.getWriter().println("</div></body></html>");
	}

}