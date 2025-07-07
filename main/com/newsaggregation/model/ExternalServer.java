package com.newsaggregation.model;

import java.sql.Timestamp;

public class ExternalServer {
	private int id;
    private String name;
    private String apiKey;
    private boolean active;
    private String apiUrl;
    private Timestamp lastAccessed;
    private String secondaryUrl;
    
    public ExternalServer(int id, String name, String apiKey) {
        this.id = id;
        this.name = name;
        this.apiKey = apiKey;
    }

    public ExternalServer(int id, String name, String apiKey, boolean active, String apiUrl, Timestamp lastAccessed, String secondaryUrl) {
        this.id = id;
        this.name = name;
        this.apiKey = apiKey;
        this.active = active;
        this.apiUrl = apiUrl;
        this.lastAccessed = lastAccessed;
        this.secondaryUrl = secondaryUrl;
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
    
    public String getSecondaryUrl() {
    	return secondaryUrl;
    }
}
