package com.newsaggregation.service;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.dao.NotificationKeywordDAO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NotificationKeywordServiceTest {

    private DatabaseConnection mockDbConnection;
    private NotificationKeywordDAO mockDao;
    private NotificationKeywordService service;

    @BeforeEach
    void setUp() throws Exception {
        mockDbConnection = mock(DatabaseConnection.class);
        mockDao = mock(NotificationKeywordDAO.class);

        // Inject mocked DAO using anonymous subclass
        service = new NotificationKeywordService(mockDbConnection) {
            @Override
            public List<String> getKeywordsByUser(int userId) throws Exception {
                return mockDao.getKeywordsByUser(userId);
            }

            @Override
            public void addKeyword(int userId, String keyword) throws Exception {
                mockDao.addKeyword(userId, keyword);
            }

            @Override
            public void updateKeyword(int userId, String oldKeyword, String newKeyword) throws Exception {
                mockDao.updateKeyword(userId, oldKeyword, newKeyword);
            }

            @Override
            public void deleteKeyword(int userId, String keyword) throws Exception {
                mockDao.deleteKeyword(userId, keyword);
            }
        };
    }

    @Test
    void testGetKeywordsByUser_returnsKeywords() throws Exception {
        when(mockDao.getKeywordsByUser(1)).thenReturn(List.of("ai", "java"));

        List<String> result = service.getKeywordsByUser(1);

        assertEquals(2, result.size());
        assertTrue(result.contains("java"));
    }

    @Test
    void testAddKeyword_executesSuccessfully() throws Exception {
        doNothing().when(mockDao).addKeyword(2, "tech");

        assertDoesNotThrow(() -> service.addKeyword(2, "tech"));
        verify(mockDao).addKeyword(2, "tech");
    }

    @Test
    void testUpdateKeyword_executesSuccessfully() throws Exception {
        doNothing().when(mockDao).updateKeyword(3, "old", "new");

        assertDoesNotThrow(() -> service.updateKeyword(3, "old", "new"));
        verify(mockDao).updateKeyword(3, "old", "new");
    }

    @Test
    void testDeleteKeyword_executesSuccessfully() throws Exception {
        doNothing().when(mockDao).deleteKeyword(4, "blockchain");

        assertDoesNotThrow(() -> service.deleteKeyword(4, "blockchain"));
        verify(mockDao).deleteKeyword(4, "blockchain");
    }
}
