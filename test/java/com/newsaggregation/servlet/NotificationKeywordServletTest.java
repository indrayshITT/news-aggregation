package com.newsaggregation.servlet;

import com.newsaggregation.handler.NotificationKeywordHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class NotificationKeywordServletTest {

    private NotificationKeywordHandler mockHandler;
    private NotificationKeywordServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        mockHandler = mock(NotificationKeywordHandler.class);
        servlet = new NotificationKeywordServlet(mockHandler);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    void testDoGet_delegatesToHandler() throws IOException {
        servlet.doGet(request, response);
        verify(mockHandler).handleGet(request, response);
    }

    @Test
    void testDoPost_delegatesToHandler() throws IOException {
        servlet.doPost(request, response);
        verify(mockHandler).handlePost(request, response);
    }
}
