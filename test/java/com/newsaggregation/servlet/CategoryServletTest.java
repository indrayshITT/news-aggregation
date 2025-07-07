package com.newsaggregation.servlet;

import com.newsaggregation.handler.CategoryHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class CategoryServletTest {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private CategoryHandler handlerMock;
    private CategoryServlet servlet;

    @BeforeEach
    void setup() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        handlerMock = mock(CategoryHandler.class);
        servlet = new CategoryServlet(handlerMock);
    }

    @Test
    void testDoGet_delegatesToHandler() throws Exception {
        servlet.doGet(request, response);

        verify(handlerMock).handleGet(request, response);
        verifyNoMoreInteractions(handlerMock);
    }

    @Test
    void testDoPost_delegatesToHandler() throws Exception {
        servlet.doPost(request, response);

        verify(handlerMock).handlePost(request, response);
        verifyNoMoreInteractions(handlerMock);
    }

    @Test
    void testService_whenPatch_delegatesToHandler() throws Exception {
        when(request.getMethod()).thenReturn("PATCH");

        servlet.service(request, response);

        verify(handlerMock).handlePatch(request, response);
        verifyNoMoreInteractions(handlerMock);
    }
}
