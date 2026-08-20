package services;

import model.Job;

public final class JobSelectionState {

    private static Job selectedJob;
    private static String searchQuery;

    private JobSelectionState() {
    }

    public static Job getSelectedJob() {
        return selectedJob;
    }

    public static void setSelectedJob(Job job) {
        selectedJob = job;
    }

    public static String getSearchQuery() {
        return searchQuery;
    }

    public static void setSearchQuery(String query) {
        searchQuery = query;
    }

    public static void clearSearchQuery() {
        searchQuery = null;
    }
}
