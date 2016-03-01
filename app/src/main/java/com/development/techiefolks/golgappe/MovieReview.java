package com.development.techiefolks.golgappe;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by deban on 24-02-2016.
 */
public class MovieReview {
    String reviewer,review;
    int rating;
    String reviewer_dp;

    public MovieReview(String reviewer, String review, int rating, String reviewer_dp) {
        this.reviewer = reviewer;
        this.review = review;
        this.rating = rating;
        this.reviewer_dp = reviewer_dp;
    }


    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReviewer_dp() {
        return reviewer_dp;
    }

    public void setReviewer_dp(String reviewer_dp) {
        this.reviewer_dp = reviewer_dp;
    }
}
