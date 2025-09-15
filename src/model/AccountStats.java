package model;

import java.sql.Timestamp;

public class AccountStats {
    private int id;
    private int accountId;
    private int posts;
    private int followers;
    private int following;
    private Timestamp updatedAt;

    // GETTER & SETTER
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }

    public int getPosts() { return posts; }
    public void setPosts(int posts) { this.posts = posts; }

    public int getFollowers() { return followers; }
    public void setFollowers(int followers) { this.followers = followers; }

    public int getFollowing() { return following; }
    public void setFollowing(int following) { this.following = following; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
