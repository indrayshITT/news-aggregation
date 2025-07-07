package com.newsaggregation.servlet;

import com.newsaggregation.handler.BlockedKeywordHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class BlockedKeywordServletTest {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private BlockedKeywordHandler handlerMock;

    @BeforeEach
    void setup() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        handlerMock = mock(BlockedKeywordHandler.class);
    }

    @Test
    void testDoGet_shouldDelegateToHandler() throws Exception {
        BlockedKeywordServlet servlet = new BlockedKeywordServlet(handlerMock);
        servlet.doGet(request, response);

        verify(handlerMock).handleGet(request, response);
        verifyNoMoreInteractions(handlerMock);
    }

    @Test
    void testDoPost_shouldDelegateToHandler() throws Exception {
        BlockedKeywordServlet servlet = new BlockedKeywordServlet(handlerMock);
        servlet.doPost(request, response);

        verify(handlerMock).handlePost(request, response);
        verifyNoMoreInteractions(handlerMock);
    }

    @Test
    void testDoDelete_shouldDelegateToHandler() throws Exception {
        BlockedKeywordServlet servlet = new BlockedKeywordServlet(handlerMock);
        servlet.doDelete(request, response);

        verify(handlerMock).handleDelete(request, response);
        verifyNoMoreInteractions(handlerMock);
    }
}
