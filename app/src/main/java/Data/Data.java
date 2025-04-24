package Data;
import com.naojianghh.bilibili3.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Data implements Serializable {
    private String description;
    private int imageResourceId;
    private int videoImageResourceId;
    private int videoListImageResourceId;
    private int videoInformationId;
    private int id;
    private boolean isFavorite;


    public Data(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public int getVideoImageResourceId(){
        return videoImageResourceId;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public Data(int id,String description, int imageResourceId, int videoImageResourceId, int videoListImageResourceId,int videoInformationId) {
        this.description = description;
        this.videoInformationId = videoInformationId;
        this.imageResourceId = imageResourceId;
        this.videoImageResourceId = videoImageResourceId;
        this.videoListImageResourceId = videoListImageResourceId;
        this.id = id;
    }

    public int getVideoListImageResourceId(){
        return videoListImageResourceId;
    }

    public int getVideoInformationId() {
        return videoInformationId;
    }

    public static List<Data> getDataList(){
        List<Data> dataList = new ArrayList<>();

        dataList.add(new Data(0,"特朗普关税王八拳，打中国，打...",R.drawable.data1,R.drawable.data1_video,R.drawable.data1_videolist,R.drawable.data1_information));
        dataList.add(new Data(1,"加关税，和普通人有什么关系？",R.drawable.data2,R.drawable.data2_video,R.drawable.data2_videolist,R.drawable.data2_information));
        dataList.add(new Data(2,"《富士山下》完整版来了！",R.drawable.data3,R.drawable.data3_video,R.drawable.data3_videolist,R.drawable.data3_information));
        dataList.add(new Data(3,"唯一以中国人为主角的GTA，却是再也无... ",R.drawable.data4,R.drawable.data4_video,R.drawable.data4_videolist,R.drawable.data4_information));
        dataList.add(new Data(4,"我把美国脸丢没了！两个中国人教美国人... ",R.drawable.data5,R.drawable.data5_video,R.drawable.data5_videolist,R.drawable.data5_information));
        dataList.add(new Data(5,"乌克兰防线说崩就崩！俄军攻占卡捷林诺... ",R.drawable.data6,R.drawable.data6_video,R.drawable.data6_videolist,R.drawable.data6_information));
        dataList.add(new Data(6,"冒充英雄被揭穿的话，人类生涯...",R.drawable.data7,R.drawable.data7_video,R.drawable.data7_videolist,R.drawable.data7_information));
        dataList.add(new Data(7,"一口气了解洗钱，它能玩得有多花？",R.drawable.data8,R.drawable.data8_video,R.drawable.data8_videolist,R.drawable.data8_information));
        dataList.add(new Data(8,"【传染病简史10】淋病：尿道流...",R.drawable.data9,R.drawable.data9_video,R.drawable.data9_videolist,R.drawable.data9_information));
        dataList.add(new Data(9,"统一台湾并不难，为什么拖了80年！",R.drawable.data10,R.drawable.data10_video,R.drawable.data10_videolist,R.drawable.data10_information));
        dataList.add(new Data(10,"【骚话公式】作文极简捞分，迅速提档",R.drawable.data11,R.drawable.data11_video,R.drawable.data11_videolist,R.drawable.data11_information));
        dataList.add(new Data(11,"看懂中国法术！网上看到的法术是真实存...",R.drawable.data12,R.drawable.data12_video,R.drawable.data12_videolist,R.drawable.data12_information));
        dataList.add(new Data(12,"《 小 妖 精 的 秋 天 》",R.drawable.data13,R.drawable.data13_video,R.drawable.data13_videolist,R.drawable.data13_information));
        dataList.add(new Data(13,"野蔷薇村的冬天",R.drawable.data14,R.drawable.data14_video,R.drawable.data14_videolist,R.drawable.data14_information));
        dataList.add(new Data(14,"开年最炸国产游戏！记忆被无数人侵犯！...",R.drawable.data15,R.drawable.data15_video,R.drawable.data15_videolist,R.drawable.data15_information));
//        dataList.add(new Data(15,"",R.drawable.data16,R.drawable.data16_video,R.drawable.data16_videolist,R.drawable.data16_information));
//        dataList.add(new Data(16,"",R.drawable.data17,R.drawable.data17_video,R.drawable.data17_videolist,R.drawable.data17_information));
//        dataList.add(new Data(17,"",R.drawable.data18,R.drawable.data18_video,R.drawable.data18_videolist,R.drawable.data18_information));
//        dataList.add(new Data(18,"",R.drawable.data19,R.drawable.data19_video,R.drawable.data19_videolist,R.drawable.data19_information));
        return dataList;
    }

}
