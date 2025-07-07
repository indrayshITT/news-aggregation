package com.newsaggregation.listener;

import java.util.concurrent.TimeUnit;

import com.newsaggregation.scheduler.NewsFetcherScheduler;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppStartupListener implements ServletContextListener {
	
	@Override
    public void contextInitialized(ServletContextEvent sce) {
		System.out.println("[Startup] AppStartupListener triggered.");

	    String intervalParam = sce.getServletContext().getInitParameter("fetch.interval.hours");
	    int intervalInHours = 3;

	    try {
	        intervalInHours = Integer.parseInt(intervalParam);
	    } catch (NumberFormatException e) {
	        System.err.println("[Config] Invalid fetch.interval.hours. Defaulting to 3 hours.");
	    }

	    NewsFetcherScheduler.start(intervalInHours, TimeUnit.HOURS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("[Shutdown] Cleaning up application resources...");
        NewsFetcherScheduler.stop();
    }
}
