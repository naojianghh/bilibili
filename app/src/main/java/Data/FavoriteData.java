package Data;

public class FavoriteData {
    private String description;
    private int imageResourceId;
    private int videoImageResourceId;
    private int videoListImageResourceId;
    private String folderName;
    private int id; // 数据库自增id
    private int originalId; // 新增：原始Data的id

    // 新增构造方法：传入originalId
    public FavoriteData(String description, int imageResourceId, int videoImageResourceId, int videoListImageResourceId, int originalId) {
        this.description = description;
        this.imageResourceId = imageResourceId;
        this.videoImageResourceId = videoImageResourceId;
        this.videoListImageResourceId = videoListImageResourceId;
        this.originalId = originalId;
    }

    public FavoriteData(String description, int imageResourceId, int videoImageResourceId, int videoListImageResourceId, String folderName) {
        this.description = description;
        this.imageResourceId = imageResourceId;
        this.videoImageResourceId = videoImageResourceId;
        this.videoListImageResourceId = videoListImageResourceId;
        this.folderName = folderName;
    }

    // 修改构造方法：添加originalId参数
    public FavoriteData(String description, int imageResourceId, int videoImageResourceId, int videoListImageResourceId, String folderName, int id, int originalId) {
        this.description = description;
        this.imageResourceId = imageResourceId;
        this.videoImageResourceId = videoImageResourceId;
        this.videoListImageResourceId = videoListImageResourceId;
        this.folderName = folderName;
        this.id = id;
        this.originalId = originalId;
    }

    // 新增getter/setter
    public int getOriginalId() {
        return originalId;
    }

    public void setOriginalId(int originalId) {
        this.originalId = originalId;
    }

    // 其余方法不变...
    public String getFolderName() {
        return folderName;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public int getVideoImageResourceId(){
        return videoImageResourceId;
    }

    public int getVideoListImageResourceId(){
        return videoListImageResourceId;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public FavoriteData setId(int id) {
        this.id = id;
        return this;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}