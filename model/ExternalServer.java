package com.newsaggregation.model;

import java.sql.Timestamp;

public class ExternalServer {
	private final int id;
    private final String name;
    private final String apiKey;
    private final boolean active;
    private final String apiUrl;
    private final Timestamp lastAccessed;

    public ExternalServer(int id, String name, String apiKey, boolean active, String apiUrl, Timestamp lastAccessed) {
        this.id = id;
        this.name = name;
        this.apiKey = apiKey;
        this.active = active;
        this.apiUrl = apiUrl;
        this.lastAccessed = lastAccessed;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getApiKey() {
        return apiKey;
    }

    public boolean isActive() {
        return active;
    }
    
    public String getUrl() {
    	return apiUrl;
    }

    public Timestamp getLastAccessed() {
        return lastAccessed;
    }
}
