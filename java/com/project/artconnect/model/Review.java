package com.project.artconnect.model;

import java.time.LocalDate;

public class Review {
    private Artwork artwork;
    private CommunityMember reviewer;
    private int rating; // 1-5
    private String comment;
    private LocalDate reviewDate;

    public Review() {
    }

    public Review(CommunityMember reviewer, Artwork artwork, int rating, String comment) {
        this.artwork = artwork;
        this.reviewer = reviewer;
        this.rating = rating;
        this.comment = comment;
        
    }

    public CommunityMember getReviewer() {
        return reviewer;
    }

    public void setReviewer(CommunityMember reviewer) {
        this.reviewer = reviewer;
    }

    public Artwork getArtwork() {
        return artwork;
    }

    public void setArtwork(Artwork artwork) {
        this.artwork = artwork;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }
}
