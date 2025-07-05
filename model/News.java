package com.newsaggregation.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class News {
	private int id;
    private String title;
    private String description;
    private String url;
    private String source;
    private Timestamp date;
    private List<String> categories = new ArrayList<>();
    
    public News() {
    	
    }

    public News(int id, String title, String description, String url, String source, Timestamp date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.url = url;
        this.source = source;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return description;
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

	public void setId(int id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setContent(String description) {
		this.description = description;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public void setCategories(List<String> categories) {
        this.categories = categories;
    }

}
