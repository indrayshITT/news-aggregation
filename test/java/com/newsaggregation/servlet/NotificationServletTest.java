package com.newsaggregation.servlet;

import com.newsaggregation.handler.NotificationHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class NotificationServletTest {

    private NotificationHandler mockHandler;
    private NotificationServlet servlet;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;

    @BeforeEach
    void setUp() {
        mockHandler = mock(NotificationHandler.class);
        servlet = new NotificationServlet(mockHandler);
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
    }

    @Test
    void testDoGet_delegatesToHandler() throws IOException {
        servlet.doGet(mockRequest, mockResponse);
        verify(mockHandler).handleGet(mockRequest, mockResponse);
    }
}
