package com.newsaggregation.service;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.dao.ExternalServerDAO;
import com.newsaggregation.dao.NewsDAO;
import com.newsaggregation.model.ExternalServer;
import com.newsaggregation.model.News;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExternalServerServiceTest {

    private DatabaseConnection mockDb;
    private ExternalServerService service;

    @BeforeEach
    void setUp() {
        mockDb = mock(DatabaseConnection.class);
        service = new ExternalServerService(mockDb);
    }

    @Test
    void testGetAll_returnsListOfServers() throws Exception {
        List<ExternalServer> servers = Arrays.asList(
                new ExternalServer(1, "Server A", "API_KEY_A"),
                new ExternalServer(2, "Server B", "API_KEY_B")
        );

        try (MockedConstruction<ExternalServerDAO> mocked = mockConstruction(ExternalServerDAO.class,
                (mock, context) -> when(mock.getAll()).thenReturn(servers))) {

            List<ExternalServer> result = service.getAll();

            assertEquals(servers, result);
            ExternalServerDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).getAll();
            verify(mockDao).close();
        }
    }

    @Test
    void testGetAllDetails_returnsListOfServers() throws Exception {
        List<ExternalServer> servers = Arrays.asList(
                new ExternalServer(3, "NewsAPI", "KEY123")
        );

        try (MockedConstruction<ExternalServerDAO> mocked = mockConstruction(ExternalServerDAO.class,
                (mock, context) -> when(mock.getAll()).thenReturn(servers))) {

            List<ExternalServer> result = service.getAllDetails();

            assertEquals(servers, result);
            verify(mocked.constructed().get(0)).close();
        }
    }

    @Test
    void testUpdateApiKey_updatesSuccessfully() throws Exception {
        try (MockedConstruction<ExternalServerDAO> mocked = mockConstruction(ExternalServerDAO.class,
                (mock, context) -> doNothing().when(mock).updateApiKey(5, "NEW_KEY"))) {

            service.updateApiKey(5, "NEW_KEY");

            ExternalServerDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).updateApiKey(5, "NEW_KEY");
            verify(mockDao).close();
        }
    }

    @Test
    void testSaveDataFromApiToDB_savesAllNewsAndCategories() throws Exception {
        News sampleNews = new News();
        sampleNews.setTitle("Sample");
        sampleNews.setUrl("http://news.com/sample");
        sampleNews.setCategories(Arrays.asList("Tech", "AI"));

        List<News> newsList = Arrays.asList(sampleNews);

        try (MockedConstruction<NewsDAO> mocked = mockConstruction(NewsDAO.class,
                (mock, context) -> {
                    when(mock.getLatestNewsArticleId()).thenReturn(1001);
                    when(mock.getOrInsertCategoryId("Tech")).thenReturn(10);
                    when(mock.getOrInsertCategoryId("AI")).thenReturn(11);
                    when(mock.insertNewsCategoryMapping(eq(1001), anyInt())).thenReturn(true);
                })) {

            service.saveDataFromApiToDB(newsList);

            NewsDAO newsDao = mocked.constructed().get(0);
            verify(newsDao, times(1)).save(any(News.class));
            verify(newsDao, times(1)).getLatestNewsArticleId();
            verify(newsDao).getOrInsertCategoryId("Tech");
            verify(newsDao).getOrInsertCategoryId("AI");
            verify(newsDao).insertNewsCategoryMapping(eq(1001), eq(10));
            verify(newsDao).insertNewsCategoryMapping(eq(1001), eq(11));
        }
    }

    @Test
    void testSaveDataFromApiToDB_handlesMappingFailure() throws Exception {
        News news = new News();
        news.setTitle("Failure Case");
        news.setUrl("http://fail.com");
        news.setCategories(List.of("FailCat"));

        List<News> newsList = List.of(news);

        try (MockedConstruction<NewsDAO> mocked = mockConstruction(NewsDAO.class,
                (mock, context) -> {
                    when(mock.getLatestNewsArticleId()).thenReturn(42);
                    when(mock.getOrInsertCategoryId("FailCat")).thenReturn(99);
                    when(mock.insertNewsCategoryMapping(42, 99)).thenReturn(false);
                })) {

            // No assertion required: we're testing internal execution and coverage
            service.saveDataFromApiToDB(newsList);

            NewsDAO dao = mocked.constructed().get(0);
            verify(dao).insertNewsCategoryMapping(42, 99);
        }
    }
}
