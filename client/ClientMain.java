package com.newsaggregation.client;

import com.newsaggregation.service.AppNavigationService;

public class ClientMain {

    public static void main(String[] args) {
        AppNavigationService app = new AppNavigationService();
        app.startApplication();
    }
}
