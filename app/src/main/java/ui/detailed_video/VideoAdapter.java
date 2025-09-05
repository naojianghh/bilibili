package ui.detailed_video;

import static java.lang.Math.random;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.naojianghh.bilibili3.R;

import java.util.List;
import java.util.Random;

import Data.Data;
import logic.network.VideoData;

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public final static int TYPE_UP_DATA = 0;
    public final static int TYPE_VIDEO_INFORMATION = 2;
    public final static int TYPE_VIDEO_SELECT = 1;
    public final static int TYPE_VIDEO_FUNCTION = 3;
    public final static int TYPE_RECOMMEND = 4;
    private Context context;
    private List<Data> recommendVideos;
    private VideoData videoData;

    public VideoAdapter(List<Data> recommendVideos, Context context, VideoData videoData) {
        this.recommendVideos = recommendVideos;
        this.context = context;
        this.videoData = videoData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_RECOMMEND){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_recommend,parent,false);
            return new RecommandVideoViewHolder(view);
        }
        else if (viewType == TYPE_UP_DATA){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_up,parent,false);
            return new UpDataViewHolder(view);
        }
        else if(viewType == TYPE_VIDEO_INFORMATION){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_information2,parent,false);
            return new VideoInformationViewHolder(view);
        }
        else if (viewType == TYPE_VIDEO_FUNCTION){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_function,parent,false);
            return new FunctionViewHolder(view);
        }
        else if (viewType == TYPE_VIDEO_SELECT){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_select,parent,false);
            return new SelectViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SelectViewHolder){

        } else if (holder instanceof UpDataViewHolder){
            UpDataViewHolder upDataViewHolder = (UpDataViewHolder) holder;
            upDataViewHolder.name.setText(videoData.getUpData().getName());
            upDataViewHolder.fan.setText(videoData.getUpData().getFans() > 10000
                    ? String.format("%.1f万粉丝", videoData.getUpData().getFans() / 10000.0)
                    : String.format("%d粉丝",videoData.getUpData().getFans()));
            upDataViewHolder.video.setText(videoData.getUpData().getVideoCount() + "视频");
            String avatarOriginal = videoData.getUpData().getAvator();
            String avatarUniqueUrl = avatarOriginal.replace("https://picsum.photos/", "https://picsum.photos/id/" + ((videoData.getUpData().getVideoCount() + 3)%100) + "/");
            Log.d("avatar", avatarUniqueUrl);
            Glide.with(holder.itemView.getContext())
                    .load(avatarUniqueUrl)
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(upDataViewHolder.imageView);

        } else if (holder instanceof VideoInformationViewHolder) {

            Random random = new Random();

            int play = random.nextInt(100) + 1;
            int month = random.nextInt(12) + 1;
            int day = random.nextInt(28) + 1;
            int hour = random.nextInt(24);
            int minute = random.nextInt(60);
            int watch = random.nextInt(100) + 1;

            VideoInformationViewHolder videoInformationViewHolder = (VideoInformationViewHolder) holder;
            videoInformationViewHolder.title.setText(videoData.getTitle());
            videoInformationViewHolder.play.setText(String.format("%.1f万",(videoData.getIsLikeCount() % 100 + 3)/10.0));
            videoInformationViewHolder.time.setText(String.format("%d 2025%d月%d日 %02d:%02d",play,month,day,hour,minute));
            videoInformationViewHolder.watch.setText(String.format("%d人正在看",watch));

        } else if (holder instanceof FunctionViewHolder) {
            FunctionViewHolder functionViewHolder = (FunctionViewHolder) holder;

            Random random = new Random();
            int share = random.nextInt(1000);


            functionViewHolder.shareText.setText(String.valueOf(share));
            functionViewHolder.coinText.setText(String.valueOf(videoData.getIsCoinCount()));
            functionViewHolder.collectText.setText(String.valueOf(videoData.getIsCollectCount()));
            functionViewHolder.likeText.setText(videoData.getIsLikeCount() > 10000
                    ? String.format("%.1f万", videoData.getIsLikeCount() / 10000.0)
                    : String.valueOf(videoData.getIsLikeCount()));
            functionViewHolder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!videoData.isLike()){
                        functionViewHolder.like.setImageResource(R.drawable.like_pink);
                        videoData.setLike(!videoData.isLike());
                        functionViewHolder.likeText.setText(videoData.getIsLikeCount() > 10000
                                ? String.format("%.1f万", videoData.getIsLikeCount() / 10000.0)
                                : String.valueOf(videoData.getIsLikeCount() + 1));
                    } else {
                        functionViewHolder.like.setImageResource(R.drawable.like);
                        videoData.setLike(!videoData.isLike());
                        functionViewHolder.likeText.setText(videoData.getIsLikeCount() > 10000
                                ? String.format("%.1f万", videoData.getIsLikeCount() / 10000.0)
                                : String.valueOf(videoData.getIsLikeCount()));
                    }
                }
            });
            functionViewHolder.dislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (videoData.isDislike()){
                        functionViewHolder.dislike.setImageResource(R.drawable.dislike_pink);
                        videoData.setDislike(!videoData.isDislike());
                    } else {
                        functionViewHolder.dislike.setImageResource(R.drawable.dislike);
                        videoData.setDislike(!videoData.isDislike());
                    }
                }
            });

        } else if (holder instanceof RecommandVideoViewHolder){
            RecommandVideoViewHolder recommendVideoViewHolder = (RecommandVideoViewHolder) holder;

            int videoIndex = position - 4;
            if (videoIndex >= 0 && videoIndex < recommendVideos.size()) {
                Data videoData = recommendVideos.get(videoIndex);
                recommendVideoViewHolder.imageView.setImageResource(videoData.getVideoListImageResourceId());
                recommendVideoViewHolder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, VideoActivity.class);
                    intent.putExtra("videoId", videoData.getId());
                    context.startActivity(intent);
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return 4 + recommendVideos.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return TYPE_VIDEO_SELECT;
        } else if (position == 1){
            return TYPE_UP_DATA;
        } else if (position == 2) {
            return TYPE_VIDEO_INFORMATION;
        } else if (position == 3) {
            return TYPE_VIDEO_FUNCTION;
        } else {
          return TYPE_RECOMMEND;
        }
    }

    public class SelectViewHolder extends RecyclerView.ViewHolder {
        public SelectViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    public class UpDataViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView fan,video,name;
        public UpDataViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.up_avatar);
            fan = itemView.findViewById(R.id.fans_count);
            video = itemView.findViewById(R.id.video_count);
            name = itemView.findViewById(R.id.up_video_name);

        }
    }
    public class VideoInformationViewHolder extends RecyclerView.ViewHolder {
        TextView title,play,time,watch;
        public VideoInformationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.video_title);
            play = itemView.findViewById(R.id.play_count);
            time = itemView.findViewById(R.id.video_time);
            watch = itemView.findViewById(R.id.video_watch);
        }
    }
    public class FunctionViewHolder extends RecyclerView.ViewHolder {
        ImageView like,dislike,coin,collect,share;
        TextView likeText,dislikeText,coinText,collectText,shareText;
        public FunctionViewHolder(@NonNull View itemView) {
            super(itemView);
            like = itemView.findViewById(R.id.like);
            dislike = itemView.findViewById(R.id.dislike);
            coin = itemView.findViewById(R.id.dislike);
            collect = itemView.findViewById(R.id.collect);
            share = itemView.findViewById(R.id.share);
            likeText = itemView.findViewById(R.id.like_text);
            coinText = itemView.findViewById(R.id.coin_text);
            collectText = itemView.findViewById(R.id.collect_text);
            shareText = itemView.findViewById(R.id.share_text);
        }
    }
    public class RecommandVideoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public RecommandVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.video_recommend);
        }
    }

}
