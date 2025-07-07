package com.newsaggregation.service;

import com.newsaggregation.dto.NewsReportDTO;

import org.json.JSONObject;

public class NewsReportService {

    public String submitReport(NewsReportDTO report) {
        String payload = report.toJson();
        if (payload == null) return "Invalid report data.";

        try {
            String response = APIService.send("/api/news/report", "POST", payload);
            JSONObject json = new JSONObject(response);
            return json.optString("message", "Report submitted.");
        } catch (Exception e) {
            return "Unable to report news at the moment.";
        }
    }
}
