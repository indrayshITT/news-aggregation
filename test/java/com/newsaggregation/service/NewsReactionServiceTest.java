package com.newsaggregation.service;

import com.newsaggregation.dao.NewsReactionDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NewsReactionServiceTest {

    private NewsReactionDAO mockDao;
    private NewsReactionService service;

    @BeforeEach
    void setUp() throws Exception {
        
        mockDao = mock(NewsReactionDAO.class);

        service = new NewsReactionService() {
            @Override
            public void react(int userId, int newsId, String reaction) throws Exception {
                mockDao.react(userId, newsId, reaction);
            }

            @Override
            public Map<String, Integer> getReactionSummary(int newsId) throws Exception {
                return mockDao.getReactionCounts(newsId);
            }
        };
    }

    @Test
    void testReact_shouldDelegateToDaoWithoutError() throws Exception {
        doNothing().when(mockDao).react(1, 101, "LIKE");

        assertDoesNotThrow(() -> service.react(1, 101, "LIKE"));

        verify(mockDao, times(1)).react(1, 101, "LIKE");
    }

    @Test
    void testGetReactionSummary_shouldReturnMapFromDao() throws Exception {
        Map<String, Integer> expectedSummary = Map.of(
                "LIKE", 10,
                "DISLIKE", 2
        );
        when(mockDao.getReactionCounts(101)).thenReturn(expectedSummary);

        Map<String, Integer> result = service.getReactionSummary(101);

        assertNotNull(result);
        assertEquals(10, result.get("LIKE"));
        assertEquals(2, result.get("DISLIKE"));
        verify(mockDao, times(1)).getReactionCounts(101);
    }

    @Test
    void testReact_shouldThrowException() throws Exception {
        doThrow(new RuntimeException("DB error")).when(mockDao).react(2, 202, "DISLIKE");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.react(2, 202, "DISLIKE");
        });

        assertEquals("DB error", ex.getMessage());
    }
}
