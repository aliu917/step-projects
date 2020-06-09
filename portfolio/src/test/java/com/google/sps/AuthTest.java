package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.io.*;
import javax.servlet.http.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AuthTest {

    @Mock
    UserService userServiceMock;

    @InjectMocks
    AuthServlet auth;

    @Before
    public void setupTests() {
        userServiceMock = mock(UserService.class);
    }

    @Test
    public void testAuthServletLogin() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);    

        when(request.getParameter("guest")).thenReturn("false");
        when(userServiceMock.createLoginURL(anyString())).thenReturn("some url");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        (new TestableAuthServlet()).doGet(request, response);

        verify(request, atLeast(1)).getParameter("guest");
        writer.flush(); 
        assertTrue(stringWriter.toString().contains("To comment,") || stringWriter.toString().contains("nickname"));
        assertFalse(stringWriter.toString().contains("To continue as a user"));
    }

    @Test
    public void testAuthServletGuest() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);    

        when(request.getParameter("guest")).thenReturn("true");
        when(userServiceMock.createLoginURL(anyString())).thenReturn("some url");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        (new TestableAuthServlet()).doGet(request, response);

        verify(request, atLeast(1)).getParameter("guest");
        writer.flush(); 
        assertFalse(stringWriter.toString().contains("To comment,"));
        assertFalse(stringWriter.toString().contains("nickname"));
        assertTrue(stringWriter.toString().contains("To continue as a user"));
    }

    private class TestableAuthServlet extends AuthServlet {
        @Override
        public UserService createUserService() {
            return userServiceMock;
        }
    }
}