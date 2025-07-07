package com.newsaggregation.service;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.dao.CategoryDAO;
import com.newsaggregation.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {

    private DatabaseConnection mockDb;
    private CategoryService categoryService;

    @BeforeEach
    void setup() {
        mockDb = mock(DatabaseConnection.class);
        categoryService = new CategoryService(mockDb);
    }

    @Test
    void testAddCategory_callsDAO() throws Exception {
        try (MockedConstruction<CategoryDAO> mocked = mockConstruction(CategoryDAO.class,
                (mock, context) -> doNothing().when(mock).addCategory("Science"))) {

            categoryService.addCategory("Science");

            CategoryDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).addCategory("Science");
            verify(mockDao).close();
        }
    }

    @Test
    void testGetAllCategories_returnsList() throws Exception {
        List<Category> mockList = Arrays.asList(
                new Category(1, "Tech", true),
                new Category(2, "Sports", false)
        );

        try (MockedConstruction<CategoryDAO> mocked = mockConstruction(CategoryDAO.class,
                (mock, context) -> when(mock.getAll()).thenReturn(mockList))) {

            List<Category> result = categoryService.getAllCategories();

            assertEquals(mockList, result);
            CategoryDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).getAll();
            verify(mockDao).close();
        }
    }

    @Test
    void testToggleCategoryVisibility_returnsTrue() throws Exception {
        try (MockedConstruction<CategoryDAO> mocked = mockConstruction(CategoryDAO.class,
                (mock, context) -> when(mock.toggleVisibility(5)).thenReturn(true))) {

            boolean result = categoryService.toggleCategoryVisibility(5);

            assertTrue(result);
            CategoryDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).toggleVisibility(5);
            verify(mockDao).close();
        }
    }

    @Test
    void testToggleCategoryVisibility_returnsFalseOnException() {
        try (MockedConstruction<CategoryDAO> mocked = mockConstruction(CategoryDAO.class,
                (mock, context) -> when(mock.toggleVisibility(999)).thenThrow(new RuntimeException("DB error")))) {

            boolean result = categoryService.toggleCategoryVisibility(999);

            assertFalse(result);
        }
    }
}
