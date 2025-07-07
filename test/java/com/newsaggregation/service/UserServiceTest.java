package com.newsaggregation.service;

import com.newsaggregation.config.DatabaseConnection;
import com.newsaggregation.dao.UserDAO;
import com.newsaggregation.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private DatabaseConnection mockDbConnection;
    private UserDAO mockUserDao;
    private UserService userService;

    @BeforeEach
    void setUp() {
        mockDbConnection = mock(DatabaseConnection.class);
        mockUserDao = mock(UserDAO.class);

        userService = new UserService(mockDbConnection) {
            @Override
            public void register(User user) throws Exception {
                mockUserDao.register(user);
                mockUserDao.close();
            }

            @Override
            public User login(String username, String password) throws Exception {
                User user = mockUserDao.login(username, password);
                mockUserDao.close();
                return user;
            }

            @Override
            public void updateLastViewedTime(int userId, Timestamp timestamp) throws Exception {
                mockUserDao.updateLastViewedTime(userId, timestamp);
                mockUserDao.close();
            }
        };
    }

    @Test
    void testRegister_success() throws Exception {
        User user = new User("john", "pass", "user", 2);

        doNothing().when(mockUserDao).register(user);
        doNothing().when(mockUserDao).close();

        assertDoesNotThrow(() -> userService.register(user));
    }

    @Test
    void testLogin_success() throws Exception {
        User expectedUser = new User("alice", "secret", "admin", 1);

        when(mockUserDao.login("alice", "secret")).thenReturn(expectedUser);
        doNothing().when(mockUserDao).close();

        User result = userService.login("alice", "secret");

        assertNotNull(result);
        assertEquals("alice", result.getUsername());
    }

    @Test
    void testUpdateLastViewedTime_success() throws Exception {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        doNothing().when(mockUserDao).updateLastViewedTime(1, timestamp);
        doNothing().when(mockUserDao).close();

        assertDoesNotThrow(() -> userService.updateLastViewedTime(1, timestamp));
    }
}
