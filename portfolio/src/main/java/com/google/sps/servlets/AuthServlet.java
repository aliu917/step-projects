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
import java.io.BufferedReader;
import java.io.FileReader; 
import java.nio.file.Files; 
import java.nio.file.Paths;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.sps.utils.UserUtils;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {

  static final String COMMENT_FORM_FILE_STRING = "/home/arliu/step/portfolio/src/main/java/com/google/sps/files/comment-form.txt";
  static final String GUEST_FIELD_FILE_STRING = "/home/arliu/step/portfolio/src/main/java/com/google/sps/files/guest-name-field.txt";
  static final String USER_FIELD_FILE_STRING = "/home/arliu/step/portfolio/src/main/java/com/google/sps/files/user-name-field.txt";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html;");
    PrintWriter out = response.getWriter();

    UserService userService = UserServiceFactory.getUserService();
    String loginUrl = userService.createLoginURL("/comments.html");
    String isGuest = request.getParameter("guest");

    if (isGuest != null && isGuest.equals("true")) {
	  String guestGreeting = "<p>Hello Guest! To continue as a user, <a class=\"link\" href=\"" + loginUrl + "\">login here</a></p>";
      try {
        createForm(out, guestGreeting, "Anonymous", true);
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else if (userService.isUserLoggedIn()) {
      String logoutUrl = userService.createLogoutURL("/comments.html");
      String id = userService.getCurrentUser().getUserId();
      String nickname = UserUtils.getUserNickname(id, userService);
      String userGreeting = "<p>Hello " + nickname + "! Not you? <a class=\"link\" href=\"" + logoutUrl + "\">Logout here</a></p>";
      try {
        createForm(out, userGreeting, nickname, false);
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      out.println("<p>To comment, <a class=\"link\" href=\"" + loginUrl + "\">login here</a> or <button class=\"text-button link\" style=\"font-size: x-large\" onclick=\"showGuestForm()\">continue as a guest</button>.</p>");
    }
  }

  private void createForm(PrintWriter out, String greetingLine, String displayNickname, boolean guest) throws Exception {
    String userNameField = "";
    String guestNameField = "";
    if (!guest) {
      userNameField = new String(Files.readAllBytes(Paths.get(USER_FIELD_FILE_STRING)));
      userNameField = String.format(userNameField, displayNickname);
    } else {
      guestNameField = new String(Files.readAllBytes(Paths.get(GUEST_FIELD_FILE_STRING)));
    }
    String commentFormHtml = new String(Files.readAllBytes(Paths.get(COMMENT_FORM_FILE_STRING)));
    commentFormHtml = String.format(commentFormHtml, greetingLine, userNameField, guestNameField);
    out.println(commentFormHtml);
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
