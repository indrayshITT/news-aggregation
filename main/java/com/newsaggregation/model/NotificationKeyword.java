package com.newsaggregation.model;

public class NotificationKeyword {
	private int id;
    private int userId;
    private String keyword;
    private boolean enabled;

    public NotificationKeyword(int id, int userId, String keyword, boolean enabled) {
        this.id = id;
        this.userId = userId;
        this.keyword = keyword;
        this.enabled = enabled;
    }

    public NotificationKeyword(int userId, String keyword, boolean enabled) {
        this(0, userId, keyword, enabled);
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getKeyword() {
        return keyword;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
