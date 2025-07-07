package com.newsaggregation.servlet;

import com.newsaggregation.handler.SavedArticleHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class SavedArticleServletTest {

    private SavedArticleHandler mockHandler;
    private SavedArticleServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        mockHandler = mock(SavedArticleHandler.class);
        servlet = new SavedArticleServlet(mockHandler);
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

    @Test
    void testDoDelete_delegatesToHandler() throws IOException {
        servlet.doDelete(request, response);
        verify(mockHandler).handleDelete(request, response);
    }
}
