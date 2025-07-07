package com.newsaggregation.servlet;

import com.newsaggregation.handler.NewsReactionHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class NewsReactionServletTest {

    private NewsReactionServlet servlet;
    private NewsReactionHandler mockHandler;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;

    @BeforeEach
    void setUp() {
        mockHandler = mock(NewsReactionHandler.class);
        servlet = new NewsReactionServlet(mockHandler); // inject mock
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
    }

    @Test
    void testDoPost_shouldDelegateToHandler() throws IOException {
        servlet.doPost(mockRequest, mockResponse);

        verify(mockHandler).handlePost(mockRequest, mockResponse);
        verifyNoMoreInteractions(mockHandler);
    }

    @Test
    void testDoGet_shouldDelegateToHandler() throws IOException {
        servlet.doGet(mockRequest, mockResponse);

        verify(mockHandler).handleGet(mockRequest, mockResponse);
        verifyNoMoreInteractions(mockHandler);
    }
}
