package com.newsaggregation.servlet;

import com.newsaggregation.handler.ExternalServerHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class ExternalServerServletTest {

    private ExternalServerServlet servlet;
    private ExternalServerHandler mockHandler;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;

    @BeforeEach
    void setUp() {
        mockHandler = mock(ExternalServerHandler.class);
        servlet = new ExternalServerServlet(mockHandler); // inject mock
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
    }

    @Test
    void testDoGet_callsHandlerHandleGet() throws IOException {
        servlet.doGet(mockRequest, mockResponse);

        verify(mockHandler).handleGet(mockRequest, mockResponse);
        verifyNoMoreInteractions(mockHandler);
    }

    @Test
    void testDoPost_callsHandlerHandlePost() throws IOException {
        servlet.doPost(mockRequest, mockResponse);

        verify(mockHandler).handlePost(mockRequest, mockResponse);
        verifyNoMoreInteractions(mockHandler);
    }
}
