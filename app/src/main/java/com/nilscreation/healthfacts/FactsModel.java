package com.nilscreation.healthfacts;

public class FactsModel {

    String title;

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    boolean isLiked = false;

    public FactsModel(String title) {
        this.title = title;
    }

    public FactsModel(boolean isLiked) {
        this.isLiked = isLiked;
    }

    public String getTitle() {
        return title;
    }

}
