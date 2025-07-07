package com.newsaggregation.model;

public class SavedArticle {
	private final int id;
    private final int userId;
    private final int newsId;

    public SavedArticle(int id, int userId, int newsId) {
        this.id = id;
        this.userId = userId;
        this.newsId = newsId;
    }

    public SavedArticle(int userId, int newsId) {
        this(0, userId, newsId);
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getNewsId() {
        return newsId;
    }
}
