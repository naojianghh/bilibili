package logic.network;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VideoData implements Serializable { // 实现接口
    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("isLike")
    private boolean isLike;

    @SerializedName("isLikeCount")
    private int isLikeCount;

    @SerializedName("isDislike")
    private boolean isDislike;

    @SerializedName("isCollect")
    private boolean isCollect;

    @SerializedName("isCollectCount")
    private int isCollectCount;

    @SerializedName("isCoin")
    private boolean isCoin;

    @SerializedName("isCoinCount")
    private int isCoinCount;

    @SerializedName("thumbPhoto")
    private String thumbPhoto;

    @SerializedName("upData")
    private UpData upData; // 已实现Serializable，可直接序列化

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public boolean isLike() { return isLike; }
    public void setLike(boolean like) { isLike = like; }

    public int getIsLikeCount() { return isLikeCount; }
    public void setIsLikeCount(int isLikeCount) { this.isLikeCount = isLikeCount; }

    public boolean isDislike() { return isDislike; }
    public void setDislike(boolean dislike) { isDislike = dislike; }

    public boolean isCollect() { return isCollect; }
    public void setCollect(boolean collect) { isCollect = collect; }

    public int getIsCollectCount() { return isCollectCount; }
    public void setIsCollectCount(int isCollectCount) { this.isCollectCount = isCollectCount; }

    public boolean isCoin() { return isCoin; }
    public void setCoin(boolean coin) { isCoin = coin; }

    public int getIsCoinCount() { return isCoinCount; }
    public void setIsCoinCount(int isCoinCount) { this.isCoinCount = isCoinCount; }

    public String getThumbPhoto() { return thumbPhoto; }
    public void setThumbPhoto(String thumbPhoto) { this.thumbPhoto = thumbPhoto; }

    public UpData getUpData() { return upData; }
    public void setUpData(UpData upData) { this.upData = upData; }
}
