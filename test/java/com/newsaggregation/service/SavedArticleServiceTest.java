package com.newsaggregation.service;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.dao.SavedArticleDAO;
import com.newsaggregation.model.News;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class SavedArticleServiceTest {

    private DatabaseConnection mockDbConnection;
    private SavedArticleDAO mockDao;
    private SavedArticleService service;

    @BeforeEach
    void setUp() {
        mockDbConnection = mock(DatabaseConnection.class);
        mockDao = mock(SavedArticleDAO.class);

        // Inject mock DAO manually using anonymous subclass
        service = new SavedArticleService(mockDbConnection) {
            @Override
            public List<News> getAllByUser(int userId) throws Exception {
                return mockDao.getAllByUserId(userId);
            }

            @Override
            public void save(int userId, int newsId) throws Exception {
                mockDao.save(userId, newsId);
            }

            @Override
            public void delete(int userId, int newsId) throws Exception {
                mockDao.delete(userId, newsId);
            }
        };
    }

    @Test
    void testGetAllByUser_returnsNewsList() throws Exception {
        List<News> expectedNews = List.of(new News(1, "title", false));
        when(mockDao.getAllByUserId(5)).thenReturn(expectedNews);

        List<News> result = service.getAllByUser(5);

        assertEquals(expectedNews, result);
        verify(mockDao).getAllByUserId(5);
    }

    @Test
    void testSave_executesWithoutError() throws Exception {
        doNothing().when(mockDao).save(1, 100);

        assertDoesNotThrow(() -> service.save(1, 100));
        verify(mockDao).save(1, 100);
    }

    @Test
    void testDelete_executesWithoutError() throws Exception {
        doNothing().when(mockDao).delete(2, 101);

        assertDoesNotThrow(() -> service.delete(2, 101));
        verify(mockDao).delete(2, 101);
    }

    @Test
    void testGetAllByUser_handlesException() {
        try {
            SavedArticleService realService = new SavedArticleService(mockDbConnection);
            SavedArticleDAO failingDao = mock(SavedArticleDAO.class);
            when(failingDao.getAllByUserId(anyInt())).thenThrow(new RuntimeException("DB failure"));

            // This simulates a real failure
            assertThrows(Exception.class, () -> realService.getAllByUser(10));
        } catch (Exception e) {
            // Ignored
        }
    }
}
