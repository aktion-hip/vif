package org.hip.vif.www;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestServlet extends HttpServlet {
	private static final String HTML = "<html><head><title>VIF Test</title></head>" +
			"<body>" +
			"<h1>Hallo Velo</h1>" +
			"</body>" +
			"</html>";
	
	@Override
	protected void doPost(HttpServletRequest inReq, HttpServletResponse inResp) throws ServletException, IOException {
		doGet(inReq, inResp);
	}

	@Override
	protected void doGet(HttpServletRequest inReq, HttpServletResponse inResp) throws ServletException, IOException {
		inResp.getWriter().write(HTML);
	}
	
}
