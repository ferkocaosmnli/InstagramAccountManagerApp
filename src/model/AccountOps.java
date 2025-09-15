package model;

import java.sql.Date;
import java.sql.Timestamp;

public class AccountOps {
    private int id;
    private int accountId;
    private Date actionDate;            // NULL ise DB CURRENT_DATE kullanır
    private String actionType;          // follow/unfollow/like/comment/dm/post/story/other
    private int quantity;
    private String tool;                // örn: instafly
    private String targetTag;
    private String notes;
    private String status;              // planned/done/skipped/failed
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }
    public Date getActionDate() { return actionDate; }
    public void setActionDate(Date actionDate) { this.actionDate = actionDate; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getTool() { return tool; }
    public void setTool(String tool) { this.tool = tool; }
    public String getTargetTag() { return targetTag; }
    public void setTargetTag(String targetTag) { this.targetTag = targetTag; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
