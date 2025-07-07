package com.newsaggregation.service;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.dao.UserCategoryDAO;
import com.newsaggregation.model.UserCategory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserCategoryServiceTest {

    private DatabaseConnection mockDbConnection;
    private UserCategoryDAO mockDao;
    private UserCategoryService service;

    @BeforeEach
    void setUp() {
        mockDbConnection = mock(DatabaseConnection.class);
        mockDao = mock(UserCategoryDAO.class);

        service = new UserCategoryService(mockDbConnection) {
            @Override
            public void setCategoryWithKeywords(int userId, int categoryId, boolean enabled, List<String> keywords) throws Exception {
                if (enabled) {
                    mockDao.enableCategoryWithKeywords(userId, categoryId, keywords);
                } else {
                    mockDao.disableCategory(userId, categoryId);
                }
            }

            @Override
            public List<UserCategory> getUserCategories(int userId) throws Exception {
                return mockDao.getAllByUserId(userId);
            }

            @Override
            public List<String> getKeywords(int userId, int categoryId) throws Exception {
                return mockDao.getKeywordsByUserAndCategory(userId, categoryId);
            }

            @Override
            public void addKeyword(int userId, int categoryId, String keyword) throws Exception {
                mockDao.addKeyword(userId, categoryId, keyword);
            }

            @Override
            public void updateKeyword(int userId, int categoryId, String oldKeyword, String newKeyword) throws Exception {
                mockDao.updateKeyword(userId, categoryId, oldKeyword, newKeyword);
            }

            @Override
            public void deleteKeyword(int userId, int categoryId, String keyword) throws Exception {
                mockDao.deleteKeyword(userId, categoryId, keyword);
            }
        };
    }

    @Test
    void testSetCategoryWithKeywords_enabledTrue_callsEnable() throws Exception {
        List<String> keywords = List.of("politics", "tech");

        service.setCategoryWithKeywords(1, 2, true, keywords);

        verify(mockDao).enableCategoryWithKeywords(1, 2, keywords);
    }

    @Test
    void testSetCategoryWithKeywords_enabledFalse_callsDisable() throws Exception {
        service.setCategoryWithKeywords(1, 2, false, List.of());

        verify(mockDao).disableCategory(1, 2);
    }

    @Test
    void testGetUserCategories_returnsList() throws Exception {
        List<UserCategory> expected = List.of(new UserCategory(1, 2, true));
        when(mockDao.getAllByUserId(5)).thenReturn(expected);

        List<UserCategory> result = service.getUserCategories(5);

        assertEquals(expected, result);
    }

    @Test
    void testGetKeywords_returnsKeywordList() throws Exception {
        List<String> keywords = List.of("tech", "ai");
        when(mockDao.getKeywordsByUserAndCategory(3, 6)).thenReturn(keywords);

        List<String> result = service.getKeywords(3, 6);

        assertEquals(keywords, result);
    }

    @Test
    void testAddKeyword_executesWithoutError() throws Exception {
        doNothing().when(mockDao).addKeyword(4, 7, "economy");

        assertDoesNotThrow(() -> service.addKeyword(4, 7, "economy"));
    }

    @Test
    void testUpdateKeyword_executesSuccessfully() throws Exception {
        doNothing().when(mockDao).updateKeyword(1, 5, "old", "new");

        assertDoesNotThrow(() -> service.updateKeyword(1, 5, "old", "new"));
    }

    @Test
    void testDeleteKeyword_executesSuccessfully() throws Exception {
        doNothing().when(mockDao).deleteKeyword(2, 9, "keyword");

        assertDoesNotThrow(() -> service.deleteKeyword(2, 9, "keyword"));
    }
}
