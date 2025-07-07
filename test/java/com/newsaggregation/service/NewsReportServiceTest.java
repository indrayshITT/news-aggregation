package com.newsaggregation.service;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.dao.NewsReportDAO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class NewsReportServiceTest {

    private DatabaseConnection mockConnection;
    private NewsReportDAO mockReportDAO;
    private NewsReportService service;

    @BeforeEach
    void setUp() {
        mockConnection = mock(DatabaseConnection.class);
        mockReportDAO = mock(NewsReportDAO.class);

        service = new NewsReportService(mockConnection) {
            @Override
            public boolean report(int userId, int newsId, String reason) throws Exception {
                try {
                    return mockReportDAO.saveReport(userId, newsId, reason);
                } finally {
                    mockReportDAO.close();
                }
            }
        };
    }

    @Test
    void testReport_SuccessfulReport() throws Exception {
        when(mockReportDAO.saveReport(1, 101, "Fake news")).thenReturn(true);

        boolean result = service.report(1, 101, "Fake news");

        assertTrue(result);
        verify(mockReportDAO).saveReport(1, 101, "Fake news");
        verify(mockReportDAO).close();
    }

    @Test
    void testReport_FailureReport() throws Exception {
        when(mockReportDAO.saveReport(1, 101, "Invalid content")).thenReturn(false);

        boolean result = service.report(1, 101, "Invalid content");

        assertFalse(result);
        verify(mockReportDAO).saveReport(1, 101, "Invalid content");
        verify(mockReportDAO).close();
    }

    @Test
    void testReport_ExceptionThrown() throws Exception {
        when(mockReportDAO.saveReport(anyInt(), anyInt(), anyString()))
                .thenThrow(new RuntimeException("DB failure"));

        Exception ex = assertThrows(RuntimeException.class, () -> {
            service.report(2, 202, "Spam");
        });

        assertEquals("DB failure", ex.getMessage());
        verify(mockReportDAO).close();
    }
}
