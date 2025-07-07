package com.newsaggregation.ingestion;

import java.util.List;

import com.newsaggregation.model.News;

public interface ExternalNewsApi {
	List<News> parseExternalApiData();
}
