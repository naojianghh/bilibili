package logic.network;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UpData implements Serializable { // 实现接口
    @SerializedName("name")
    private String name;

    @SerializedName("uid")
    private String uid;

    @SerializedName("fans")
    private int fans;

    @SerializedName("videoCount")
    private int videoCount;

    @SerializedName("isFollow")
    private boolean isFollow;

    @SerializedName("avator")
    private String avator;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public int getFans() { return fans; }
    public void setFans(int fans) { this.fans = fans; }

    public int getVideoCount() { return videoCount; }
    public void setVideoCount(int videoCount) { this.videoCount = videoCount; }

    public boolean isFollow() { return isFollow; }
    public void setFollow(boolean follow) { isFollow = follow; }

    public String getAvator() { return avator; }
    public void setAvator(String avator) { this.avator = avator; }
}
