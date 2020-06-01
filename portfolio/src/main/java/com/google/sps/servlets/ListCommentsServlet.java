package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet responsible for listing previous comments. */
@WebServlet("/list-comments")
public class ListCommentsServlet extends HttpServlet {

  class Comment {
    String text;
    Long timestamp;

    Comment(String text, Long timestamp) {
      this.text = text;
      this.timestamp = timestamp;
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
      String text = (String) entity.getProperty("text");
      Long timestamp = (Long) entity.getProperty("timestamp");
      Comment c = new Comment(text, timestamp);
      allComments.add(c);
      displayCount --;
    }

    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(allComments));
  }

}