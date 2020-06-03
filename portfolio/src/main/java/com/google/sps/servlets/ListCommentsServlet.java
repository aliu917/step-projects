package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.sps.utils.UserUtils;

/** Servlet responsible for listing previous comments. */
@WebServlet("/list-comments")
public class ListCommentsServlet extends HttpServlet {

  class Comment {
    String text;
    long timestamp;
    String username;
    long id;
    boolean currentUserComment;

    Comment(long id, String text, long timestamp, String username, boolean currentUserComment) {
      this.text = text;
      this.timestamp = timestamp;
      this.username = username;
      this.id = id;
      this.currentUserComment = currentUserComment;
    }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    ArrayList<Comment> allComments = new ArrayList<>();
    int displayCount = Integer.parseInt(request.getParameter("count"));

    for (Entity entity : results.asIterable()) {
      if (displayCount == 0) {
          break;
      }
      long id = entity.getKey().getId();
      String text = (String) entity.getProperty("text");
      long timestamp = (long) entity.getProperty("timestamp");
      String username;
      String userId = (String) entity.getProperty("userId");
      boolean currentUserComment = false;
      if (userId == null || userId.equals("")) {
	    username = (String) entity.getProperty("username");
      } else {
        UserService userService = UserServiceFactory.getUserService();
        if (userService.isUserLoggedIn() && userId.equals(userService.getCurrentUser().getUserId())) {
            currentUserComment = true;
        }
        if (UserUtils.hasNickname(userId, userService)) {
            username = UserUtils.getUserNickname(userId, userService);
        } else {
            username = (String) entity.getProperty("username");
        }
      }
      Comment c = new Comment(id, text, timestamp, username, currentUserComment);
      allComments.add(c);
      displayCount --;
    }

    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(allComments));
  }

}