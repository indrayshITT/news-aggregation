package com.newsaggregation.servlet;

import com.newsaggregation.handler.NewsReportHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class NewsReportServletTest {

    private NewsReportHandler mockHandler;
    private NewsReportServlet servlet;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;

    @BeforeEach
    void setUp() {
        mockHandler = mock(NewsReportHandler.class);
        servlet = new NewsReportServlet(mockHandler);
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
    }

    @Test
    void testDoPost_delegatesToHandler() throws IOException {
        servlet.doPost(mockRequest, mockResponse);

        verify(mockHandler, times(1)).handleReport(mockRequest, mockResponse);
        verifyNoMoreInteractions(mockHandler);
    }
}
