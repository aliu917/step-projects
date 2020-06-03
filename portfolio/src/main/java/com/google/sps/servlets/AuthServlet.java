package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.sps.utils.UserUtils;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html;");
    PrintWriter out = response.getWriter();

    UserService userService = UserServiceFactory.getUserService();
    String loginUrl = userService.createLoginURL("/comments.html");
    String isGuest = request.getParameter("guest");

    if (isGuest != null && isGuest.equals("true")) {
	  String guestGreeting = "<p>Hello Guest! To continue as a user, <a class=\"link\" href=\"" + loginUrl + "\">login here</a></p>";
      createForm(out, guestGreeting, "Anonymous", true);
    } else if (userService.isUserLoggedIn()) {
      String logoutUrl = userService.createLogoutURL("/comments.html");
      String id = userService.getCurrentUser().getUserId();
      String nickname = UserUtils.getUserNickname(id, userService);
      String userGreeting = "<p>Hello " + nickname + "! Not you? <a class=\"link\" href=\"" + logoutUrl + "\">Logout here</a></p>";
      createForm(out, userGreeting, nickname, false);
    } else {
      out.println("<p>To comment, <a class=\"link\" href=\"" + loginUrl + "\">login here</a> or <button class=\"text-button link\" style=\"font-size: x-large\" onclick=\"showGuestForm()\">continue as a guest</button>.</p>");
    }
  }

  private void createForm(PrintWriter out, String greetingLine, String displayNickname, boolean guest) {
  	out.println("<div class=\"section\" style=\"margin:0px\">");
  	out.println("<h2> Comment </h2>");
  	out.println(greetingLine);
    
    if (!guest) {
      out.println("<p style=\"display: inline;\">Change your nickname: </p>");
  	  out.println("<form method=\"POST\" action=\"/auth\" style=\"display: inline;\">");
  	  out.println("<input name=\"nickname\" value=\"" + displayNickname + "\" />");
  	  out.println("<button>Update</button>");
  	  out.println("</form>");
    }

  	out.println("<form action=\"/comment\" method=\"POST\">");
    if (guest) {
      out.println("<p style=\"display: inline;\">Name:</p>");
      out.println("<input style=\"margin-bottom: 10px\" type=\"text\" name=\"username\" placeholder=\"Insert name\">");
    }
  	out.println("<p>Write a comment:</p>");
  	out.println("<textarea type=\"text\" name=\"user-comment\" placeholder=\"Enter a comment.\" style=\"width: 90%; height: 100px; margin-left: 15px\"></textarea>");        
  	out.println("<br/><br/>");
  	out.println("<input class=\"styled-button\" type=\"submit\" value=\"Comment\"/>");
  	out.println("</form>");
    out.println("</div>");
  	out.println("<br/>");
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();

    String nickname = request.getParameter("nickname");
    String id = userService.getCurrentUser().getUserId();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity entity = new Entity("UserInfo", id);
    entity.setProperty("id", id);
    entity.setProperty("nickname", nickname);
    datastore.put(entity);

    response.sendRedirect("/comments.html");
  }

}
