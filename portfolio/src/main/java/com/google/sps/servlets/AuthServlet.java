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

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html;");
    PrintWriter out = response.getWriter();

    // Only logged-in users can see the form
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      System.out.println("Logged in");
      String logoutUrl = userService.createLogoutURL("/comments.html");
      String nickname = getUserNickname(userService.getCurrentUser().getUserId());
      if (nickname.equals("")) {
          nickname = userService.getCurrentUser().getEmail();
      }
      createForm(out, nickname, logoutUrl);
    } else {
      System.out.println("Logged out");
      String loginUrl = userService.createLoginURL("/comments.html");
      out.println("<p>To comment, <a class=\"link\" href=\"" + loginUrl + "\">login here</a>.</p>");
    }
  }

  public String getUserNickname(String id) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query =
        new Query("UserInfo")
            .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    if (entity == null) {
      return "";
    }
    String nickname = (String) entity.getProperty("nickname");
    return nickname;
  }

  private void createForm(PrintWriter out, String nickname, String logoutUrl) {
  	out.println("<div class=\"section\" style=\"margin:0px\">");
  	out.println("<h2> Comment </h2>");
  	out.println("<p>Hello " + nickname + "! Not you? <a class=\"link\" href=\"" + logoutUrl + "\">Logout here</a></p>");
  	out.println("<p style=\"display: inline;\">Change your nickname: </p>");
  	out.println("<form method=\"POST\" action=\"/auth\" style=\"display: inline;\">");
  	out.println("<input name=\"nickname\" value=\"" + nickname + "\" />");
  	out.println("<button>Update</button>");
  	out.println("</form>");

  	out.println("<form action=\"/comment\" method=\"POST\">");
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
