package com.newsaggregation.model;

import java.sql.Timestamp;
import java.util.List;

public class News {
	private final int id;
    private final String title;
    private final String content;
    private final String url;
    private final String source;
    private final Timestamp date;
    private List<String> categories;

    public News(int id, String title, String content, String url, String source, Timestamp date) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.url = url;
        this.source = source;
        this.date = date;
    }
    
    public News(int id, String title, String content, String url, String source, Timestamp date, List<String> categories) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.url = url;
        this.source = source;
        this.date = date;
        this.categories = categories;
    }

    public News(String title, String content, String url, String source, Timestamp date) {
        this(0, title, content, url, source, date);
    }
    
    public News(String title, String content, String url, String source, Timestamp date, List<String> categories) {
        this(0, title, content, url, source, date, categories);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    public String getSource() {
        return source;
    }

    public Timestamp getDate() {
        return date;
    }
    
    public List<String> getCategories() {
    	return categories;
    }
}
