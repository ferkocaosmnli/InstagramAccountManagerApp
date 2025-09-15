package model;

import java.sql.Date;
import java.sql.Timestamp;

public class AccountDailyStats {
    private int id;
    private int accountId;

    private int dailyPosts;
    private int dailyFollowersGain;
    private int dailyFollowersLost;
    private int dailyFollowingAdded;
    private int dailyFollowingRemoved;
    private int dailyLikes;
    private int dailyComments;

    private String notes;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Date createdDate; // generated column (DATE(created_at))

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }

    public int getDailyPosts() { return dailyPosts; }
    public void setDailyPosts(int dailyPosts) { this.dailyPosts = dailyPosts; }

    public int getDailyFollowersGain() { return dailyFollowersGain; }
    public void setDailyFollowersGain(int dailyFollowersGain) { this.dailyFollowersGain = dailyFollowersGain; }

    public int getDailyFollowersLost() { return dailyFollowersLost; }
    public void setDailyFollowersLost(int dailyFollowersLost) { this.dailyFollowersLost = dailyFollowersLost; }

    public int getDailyFollowingAdded() { return dailyFollowingAdded; }
    public void setDailyFollowingAdded(int dailyFollowingAdded) { this.dailyFollowingAdded = dailyFollowingAdded; }

    public int getDailyFollowingRemoved() { return dailyFollowingRemoved; }
    public void setDailyFollowingRemoved(int dailyFollowingRemoved) { this.dailyFollowingRemoved = dailyFollowingRemoved; }

    public int getDailyLikes() { return dailyLikes; }
    public void setDailyLikes(int dailyLikes) { this.dailyLikes = dailyLikes; }

    public int getDailyComments() { return dailyComments; }
    public void setDailyComments(int dailyComments) { this.dailyComments = dailyComments; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
}
