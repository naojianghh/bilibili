package Data;

public class FavoriteData {
    private String description;
    private int imageResourceId;
    private int videoImageResourceId;
    private int videoListImageResourceId;
    private String folderName;
    private int id;

    public FavoriteData(String description, int imageResourceId, int videoImageResourceId, int videoListImageResourceId) {
        this.description = description;
        this.imageResourceId = imageResourceId;
        this.videoImageResourceId = videoImageResourceId;
        this.videoListImageResourceId = videoListImageResourceId;
    }

    public FavoriteData(String description, int imageResourceId, int videoImageResourceId, int videoListImageResourceId, String folderName) {
        this.description = description;
        this.imageResourceId = imageResourceId;
        this.videoImageResourceId = videoImageResourceId;
        this.videoListImageResourceId = videoListImageResourceId;
        this.folderName = folderName;
    }

    public FavoriteData(String description, int imageResourceId, int videoImageResourceId, int videoListImageResourceId, String folderName, int id) {
        this.description = description;
        this.imageResourceId = imageResourceId;
        this.videoImageResourceId = videoImageResourceId;
        this.videoListImageResourceId = videoListImageResourceId;
        this.folderName = folderName;
        this.id = id;
    }

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
