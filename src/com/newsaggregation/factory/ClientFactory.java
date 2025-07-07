package com.newsaggregation.factory;

import com.newsaggregation.client.*;

public class ClientFactory {
    private final HeadlinesClient headlinesClient;
    private final NewsReactionClient newsReactionClient;
    private final SavedArticleClient savedArticleClient;
    private final SearchClient searchClient;
    private final NotificationClient notificationClient;

    public ClientFactory() {
        this.headlinesClient = new HeadlinesClient();
        this.newsReactionClient = new NewsReactionClient();
        this.savedArticleClient = new SavedArticleClient(headlinesClient, newsReactionClient);
        this.searchClient = new SearchClient();
        this.notificationClient = new NotificationClient();
    }

    public HeadlinesClient getHeadlinesClient() {
        return headlinesClient;
    }

    public NewsReactionClient getNewsReactionClient() {
        return newsReactionClient;
    }

    public SavedArticleClient getSavedArticleClient() {
        return savedArticleClient;
    }

    public SearchClient getSearchClient() {
        return searchClient;
    }

    public NotificationClient getNotificationClient() {
        return notificationClient;
    }
}
