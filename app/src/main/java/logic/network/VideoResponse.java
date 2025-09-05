package logic.network;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class VideoResponse {
    @SerializedName("code")
    private int code;

    @SerializedName("msg")
    private String msg;

    @SerializedName("data")
    private List<VideoData> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<VideoData> getData() {
        return data;
    }

    public void setData(List<VideoData> data) {
        this.data = data;
    }
}
