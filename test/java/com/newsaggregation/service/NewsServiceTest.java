package com.newsaggregation.service;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.dao.NewsDAO;
import com.newsaggregation.model.News;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NewsServiceTest {

    private NewsDAO mockNewsDAO;
    private NewsService newsService;

    @BeforeEach
    void setup() throws Exception {
        DatabaseConnection mockConnection = mock(DatabaseConnection.class);
        mockNewsDAO = mock(NewsDAO.class);

        // Anonymous class override to inject mocked DAO
        newsService = new NewsService(mockConnection) {
            @Override
            public List<News> searchNewsByKeywordsSortedByLikes(String[] keywords) throws Exception {
                return mockNewsDAO.searchNewsByKeywords(keywords);
            }

            @Override
            public List<News> getNewsSavedTodaySortedByLikes() throws Exception {
                return mockNewsDAO.getNewsByDate(LocalDate.now());
            }

            @Override
            public List<News> getNewsByDateRangeSortedByLikes(LocalDate start, LocalDate end) throws Exception {
                return mockNewsDAO.getNewsByDateRange(start, end);
            }

            @Override
            public List<News> getNewsByDateRangeAndCategorySortedByLikes(LocalDate start, LocalDate end, int categoryId) throws Exception {
                return mockNewsDAO.getNewsByDateRangeAndCategory(start, end, categoryId);
            }

            @Override
            public List<News> getReportedNews() throws Exception {
                return mockNewsDAO.getReportedNews();
            }

            @Override
            public boolean setNewsHidden(int newsId, boolean hide) throws Exception {
                return mockNewsDAO.setHidden(newsId, hide);
            }

            @Override
            public void autoHideIfReportedOverThreshold(int newsId) throws Exception {
                mockNewsDAO.autoHideIfReportedOverThreshold(newsId);
            }
        };
    }

    @Test
    void testSearchNewsByKeywords() throws Exception {
        String[] keywords = {"economy", "market"};
        when(mockNewsDAO.searchNewsByKeywords(keywords)).thenReturn(List.of(new News()));

        List<News> result = newsService.searchNewsByKeywordsSortedByLikes(keywords);
        assertEquals(1, result.size());
    }

    @Test
    void testGetNewsSavedToday() throws Exception {
        when(mockNewsDAO.getNewsByDate(LocalDate.now())).thenReturn(List.of(new News()));

        List<News> result = newsService.getNewsSavedTodaySortedByLikes();
        assertEquals(1, result.size());
    }

    @Test
    void testGetNewsByDateRange() throws Exception {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 31);

        when(mockNewsDAO.getNewsByDateRange(start, end)).thenReturn(List.of(new News()));

        List<News> result = newsService.getNewsByDateRangeSortedByLikes(start, end);
        assertEquals(1, result.size());
    }

    @Test
    void testGetNewsByDateRangeAndCategory() throws Exception {
        LocalDate start = LocalDate.of(2024, 5, 1);
        LocalDate end = LocalDate.of(2024, 5, 10);

        when(mockNewsDAO.getNewsByDateRangeAndCategory(start, end, 3))
                .thenReturn(List.of(new News()));

        List<News> result = newsService.getNewsByDateRangeAndCategorySortedByLikes(start, end, 3);
        assertEquals(1, result.size());
    }

    @Test
    void testGetReportedNews() throws Exception {
        when(mockNewsDAO.getReportedNews()).thenReturn(List.of(new News()));

        List<News> result = newsService.getReportedNews();
        assertEquals(1, result.size());
    }

    @Test
    void testSetNewsHidden() throws Exception {
        when(mockNewsDAO.setHidden(101, true)).thenReturn(true);

        boolean result = newsService.setNewsHidden(101, true);
        assertTrue(result);
    }

    @Test
    void testAutoHideIfReportedOverThreshold() throws Exception {
        doNothing().when(mockNewsDAO).autoHideIfReportedOverThreshold(55);

        assertDoesNotThrow(() -> newsService.autoHideIfReportedOverThreshold(55));
        verify(mockNewsDAO).autoHideIfReportedOverThreshold(55);
    }
}
