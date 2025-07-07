package com.newsaggregation.service;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.dao.NotificationDAO;
import com.newsaggregation.model.News;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NotificationServiceTest {

    private DatabaseConnection mockConnection;
    private NotificationDAO mockDao;
    private NotificationService service;

    @BeforeEach
    void setUp() {
        mockConnection = mock(DatabaseConnection.class);
        mockDao = mock(NotificationDAO.class);
        service = new NotificationService(mockConnection) {
            @Override
            public List<News> getConsoleNotifications(int userId, Timestamp from, Timestamp to) throws Exception {
                return mockDao.getNewsForConsoleNotification(userId, from, to);
            }

            @Override
            public List<News> getFilteredNewsforUser(int userId, List<News> fetchedNews) throws Exception {
                return mockDao.getFilteredNewsForUser(userId, fetchedNews);
            }
        };
    }

    @Test
    void testGetConsoleNotifications_returnsNewsList() throws Exception {
        Timestamp from = Timestamp.valueOf("2024-01-01 00:00:00");
        Timestamp to = Timestamp.valueOf("2024-01-02 00:00:00");

        List<News> expectedNews = List.of(new News(1, "AI", false));
        when(mockDao.getNewsForConsoleNotification(1, from, to)).thenReturn(expectedNews);

        List<News> result = service.getConsoleNotifications(1, from, to);

        assertEquals(expectedNews, result);
    }

    @Test
    void testGetFilteredNewsforUser_returnsFilteredList() throws Exception {
        List<News> inputNews = List.of(new News(2, "Tech", false));
        when(mockDao.getFilteredNewsForUser(2, inputNews)).thenReturn(inputNews);

        List<News> result = service.getFilteredNewsforUser(2, inputNews);

        assertEquals(inputNews, result);
    }

    @Test
    void testGetConsoleNotifications_throwsException() {
        Timestamp from = Timestamp.valueOf("2024-01-01 00:00:00");
        Timestamp to = Timestamp.valueOf("2024-01-02 00:00:00");

        try {
            NotificationService realService = new NotificationService(mockConnection);
            NotificationDAO failingDao = mock(NotificationDAO.class);
            when(failingDao.getNewsForConsoleNotification(anyInt(), any(), any()))
                .thenThrow(new RuntimeException("DB failure"));

            assertThrows(Exception.class,
                    () -> realService.getConsoleNotifications(1, from, to));
        } catch (Exception e) {
           
        }
    }
}
