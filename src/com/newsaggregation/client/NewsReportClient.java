package com.newsaggregation.client;

import java.util.Scanner;

import com.newsaggregation.dto.NewsReportDTO;
import com.newsaggregation.service.NewsReportService;
import com.newsaggregation.util.InputUtil;
import com.newsaggregation.util.UserSession;

public class NewsReportClient {

	private final NewsReportService reportService;

    public NewsReportClient() {
        this.reportService = new NewsReportService();
    }

    public void reportNews(Scanner scanner, int newsId) {
        String reason = InputUtil.readString(scanner, "Enter report reason: ");
        if (reason == null || reason.isBlank()) {
            System.out.println("Report reason cannot be empty.");
            return;
        }

        NewsReportDTO report = new NewsReportDTO(UserSession.getUserId(), newsId, reason);
        String message = reportService.submitReport(report);
        System.out.println(message);
    }
}
