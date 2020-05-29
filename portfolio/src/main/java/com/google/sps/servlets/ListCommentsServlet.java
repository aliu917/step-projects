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

  int prevDisplayCount = 5;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    ArrayList<String> allComments = new ArrayList<>();
    int displayCount = getCountInput(request);
    if (displayCount == -1) {
      response.setContentType("text/html");
      response.getWriter().println("Please enter an integer between 1 and 3.");
      return;
    } else {
        prevDisplayCount = displayCount;
    }

    for (Entity entity : results.asIterable()) {
      if (displayCount == 0) {
          break;
      }
      String text = (String) entity.getProperty("text");
      allComments.add(text);
      displayCount --;
    }

    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(allComments));
  }

  private int getCountInput(HttpServletRequest request) {
    System.out.println("REQUEST: " + request.getQueryString());
    String displayCountString = request.getParameter("display-count");
    System.out.println("COUNT: " + displayCountString);
    if (displayCountString == null) {
      return prevDisplayCount;
    }
    int displayCount;
    try {
      displayCount = Integer.parseInt(displayCountString);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + displayCountString);
      return -1;
    }
    if (displayCount < 5 || displayCount > 20) {
      System.err.println("Comment display count is out of range: " + displayCountString);
      return -1;
    }
    return displayCount;
  }

}