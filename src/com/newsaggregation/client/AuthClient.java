package com.newsaggregation.client;

import java.util.Scanner;


import com.newsaggregation.dto.UserDTO;
import com.newsaggregation.service.AuthService;
import com.newsaggregation.util.InputUtil;

public class AuthClient {
	private final AuthService authService = new AuthService();
	
	public void signup(Scanner sc) {
	    String username = InputUtil.readString(sc, "Enter username: ");
	    String email = InputUtil.readString(sc, "Enter email: ");
	    String password = InputUtil.readString(sc, "Enter password: ");
	    UserDTO user = new UserDTO(username, email, password);
	    authService.register(user);
	}

	public boolean login(Scanner sc) {
	    String username = InputUtil.readString(sc, "Enter username: ");
	    String password = InputUtil.readString(sc, "Enter password: ");
	    UserDTO user = new UserDTO(username, password);
	    return authService.authenticate(user);
	}
}
