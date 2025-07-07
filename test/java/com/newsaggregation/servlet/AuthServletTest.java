package com.newsaggregation.servlet;

import com.newsaggregation.handler.AuthHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;

public class AuthServletTest {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseWriter;
    private PrintWriter printWriter;
    private AuthHandler handlerMock;

    @BeforeEach
    void setup() throws Exception {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        responseWriter = new StringWriter();
        printWriter = new PrintWriter(responseWriter);
        when(response.getWriter()).thenReturn(printWriter);
        handlerMock = mock(AuthHandler.class);
    }

    @Test
    void testDoPost_loginDelegatesToHandler() throws Exception {
        when(request.getPathInfo()).thenReturn("/login");

        AuthServlet servlet = new AuthServlet(handlerMock);
        servlet.doPost(request, response);

        verify(handlerMock).handleLogin(request, response);
        verifyNoMoreInteractions(handlerMock);
    }

    @Test
    void testDoPost_signupDelegatesToHandler() throws Exception {
        when(request.getPathInfo()).thenReturn("/signup");

        AuthServlet servlet = new AuthServlet(handlerMock);
        servlet.doPost(request, response);

        verify(handlerMock).handleSignup(request, response);
        verifyNoMoreInteractions(handlerMock);
    }

    @Test
    void testDoPost_logoutDelegatesToHandler() throws Exception {
        when(request.getPathInfo()).thenReturn("/logout");

        AuthServlet servlet = new AuthServlet(handlerMock);
        servlet.doPost(request, response);

        verify(handlerMock).handleLogout(request, response);
        verifyNoMoreInteractions(handlerMock);
    }

    @Test
    void testDoPost_invalidActionReturns404() throws Exception {
        when(request.getPathInfo()).thenReturn("/invalid");

        AuthServlet servlet = new AuthServlet(handlerMock);
        servlet.doPost(request, response);

        printWriter.flush();
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        assert responseWriter.toString().contains("Invalid auth action");
        verifyNoInteractions(handlerMock);
    }

    @Test
    void testDoPost_nullPathReturns404() throws Exception {
        when(request.getPathInfo()).thenReturn(null);

        AuthServlet servlet = new AuthServlet(handlerMock);
        servlet.doPost(request, response);

        printWriter.flush();
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        assert responseWriter.toString().contains("Invalid auth route.");
        verifyNoInteractions(handlerMock);
    }
}
