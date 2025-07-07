package com.newsaggregation.servlet;

import com.newsaggregation.handler.NewsHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class NewsServletTest {

    private NewsHandler mockHandler;
    private NewsServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setup() {
        mockHandler = mock(NewsHandler.class);
        servlet = new NewsServlet(mockHandler);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    void testDoGet_delegatesToHandleGetRequests() throws IOException {
        servlet.doGet(request, response);
        verify(mockHandler).handleGetRequests(request, response);
    }

    @Test
    void testService_patchMethod_delegatesToHandleToggleVisibility() throws ServletException, IOException {
        when(request.getMethod()).thenReturn("PATCH");

        servlet.service(request, response);

        verify(mockHandler).handleToggleVisibility(request, response);
    }

    @Test
    void testDoPost_setsMethodNotAllowed() throws IOException {
        servlet.doPost(request, response);
        verify(response).setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
}
