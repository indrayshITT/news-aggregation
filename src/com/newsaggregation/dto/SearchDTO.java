package com.newsaggregation.dto;

import java.time.LocalDate;
import java.util.List;

public class SearchDTO {
    private List<String> keywords;
    private LocalDate fromDate;
    private LocalDate toDate;

    public SearchDTO(List<String> keywords) {
        this.keywords = keywords;
    }

    public SearchDTO(List<String> keywords, LocalDate fromDate, LocalDate toDate) {
        this.keywords = keywords;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public boolean isDateFilterApplied() {
        return fromDate != null && toDate != null;
    }
}
