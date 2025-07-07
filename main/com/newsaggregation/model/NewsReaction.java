package com.newsaggregation.model;

public class NewsReaction {
	private final int id;
    private final int userId;
    private final int newsId;
    private final String reaction;

    public NewsReaction(int id, int userId, int newsId, String reaction) {
        this.id = id;
        this.userId = userId;
        this.newsId = newsId;
        this.reaction = reaction;
    }

    public NewsReaction(int userId, int newsId, String reaction) {
        this(0, userId, newsId, reaction);
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

    public String getReaction() {
        return reaction;
    }
}
