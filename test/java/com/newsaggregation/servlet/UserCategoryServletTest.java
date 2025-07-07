package com.newsaggregation.servlet;

import com.newsaggregation.handler.UserCategoryHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class UserCategoryServletTest {

    private UserCategoryHandler mockHandler;
    private UserCategoryServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        mockHandler = mock(UserCategoryHandler.class);
        servlet = new UserCategoryServlet(mockHandler);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    void testDoPost_callsHandlerHandlePost() throws IOException {
        servlet.doPost(request, response);
        verify(mockHandler).handlePost(request, response);
    }

    @Test
    void testDoGet_callsHandlerHandleGet() throws IOException {
        servlet.doGet(request, response);
        verify(mockHandler).handleGet(request, response);
    }
}
