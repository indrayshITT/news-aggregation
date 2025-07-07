package com.newsaggregation.service;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.dao.BlockedKeywordDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BlockedKeywordServiceTest {

    private DatabaseConnection mockDbConnection;
    private BlockedKeywordService service;

    @BeforeEach
    void setUp() {
        mockDbConnection = mock(DatabaseConnection.class);
        service = new BlockedKeywordService(mockDbConnection);
    }

    @Test
    void testAddBlockedKeyword_callsDAO() throws Exception {
        try (MockedConstruction<BlockedKeywordDAO> mocked = mockConstruction(BlockedKeywordDAO.class,
                (mock, context) -> {
                    doNothing().when(mock).addBlockedKeyword("spam");
                })) {
            service.addBlockedKeyword("spam");

            BlockedKeywordDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).addBlockedKeyword("spam");
            verify(mockDao).close();
        }
    }

    @Test
    void testRemoveBlockedKeyword_callsDAO() throws Exception {
        try (MockedConstruction<BlockedKeywordDAO> mocked = mockConstruction(BlockedKeywordDAO.class,
                (mock, context) -> {
                    doNothing().when(mock).removeBlockedKeyword("scam");
                })) {
            service.removeBlockedKeyword("scam");

            BlockedKeywordDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).removeBlockedKeyword("scam");
            verify(mockDao).close();
        }
    }

    @Test
    void testGetAllBlockedKeywords_returnsList() throws Exception {
        List<String> expected = Arrays.asList("spam", "scam");

        try (MockedConstruction<BlockedKeywordDAO> mocked = mockConstruction(BlockedKeywordDAO.class,
                (mock, context) -> {
                    when(mock.getAllBlockedKeywords()).thenReturn(expected);
                })) {
            List<String> result = service.getAllBlockedKeywords();

            assertEquals(expected, result);
            BlockedKeywordDAO mockDao = mocked.constructed().get(0);
            verify(mockDao).getAllBlockedKeywords();
            verify(mockDao).close();
        }
    }
}
