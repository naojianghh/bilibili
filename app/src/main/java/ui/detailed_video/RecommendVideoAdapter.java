package ui.detailed_video;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.naojianghh.bilibili3.R;

import java.util.List;

import Data.Data;
import Data.FavoriteData;
import Data.FavoriteDatabaseHelper;

public class RecommendVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Data> recommendVideos;

    private static final int VIEW_TYPE_INFORMATION = 0;
    private static final int VIEW_TYPE_RECOMMEND_VIDEO = 1;
    private static final int VIEW_TYPE_FAVORITE_BUTTON = 2;
    private static final int VIEW_TYPE_SELECT = 3;
    private int videoId;
    private List<Data> dataList = Data.getDataList();
    private FavoriteDatabaseHelper databaseHelper;
    private final FavoriteActionListener listener;

    public RecommendVideoAdapter(List<Data> recommendVideos, Context context,int videoId,FavoriteActionListener listener) {
        this.recommendVideos = recommendVideos;
        this.context = context;
        this.videoId = videoId;
        this.databaseHelper = new FavoriteDatabaseHelper(context);
        this.listener = listener;
    }

    public interface FavoriteActionListener {
        void onAddToDefaultFolder(Data data);
        void onChangeFolderRequest(Data data, FavoriteData favorite);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder result;
        if (viewType == VIEW_TYPE_INFORMATION){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_information,parent,false);
            result = new VideoInformationViewHolder(view);
        }
        else if (viewType == VIEW_TYPE_RECOMMEND_VIDEO){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_recommend,parent,false);
            result = new RecommendVideoViewHolder(view);
        }
        else if (viewType == VIEW_TYPE_SELECT){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_select,parent,false);
            result = new SelectViewHolder(view); // 对应position=0的视图类型
        }
        else { // VIEW_TYPE_FAVORITE_BUTTON
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_favorite_button,parent,false);
            result = new FavoriteButtonViewHolder(view);
        }
        return result;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof SelectViewHolder) {

        }
        else if (holder instanceof VideoInformationViewHolder) {
            VideoInformationViewHolder videoInformationViewHolder = (VideoInformationViewHolder) holder;
            videoInformationViewHolder.imageView.setImageResource(dataList.get(videoId).getVideoInformationId());
        }
        else if (holder instanceof FavoriteButtonViewHolder) {
            Data currentData = dataList.get(videoId);
            FavoriteButtonViewHolder favoriteButtonViewHolder = (FavoriteButtonViewHolder) holder;

            boolean isFavorite = databaseHelper.isFavoriteExists(currentData);
            currentData.setFavorite(isFavorite);
            updateFavoriteIcon(favoriteButtonViewHolder.imageView,isFavorite);

            favoriteButtonViewHolder.buttonFavorite.setOnClickListener(v -> {
                boolean newFavoriteState = !currentData.isFavorite();
                if (newFavoriteState) {
                    listener.onAddToDefaultFolder(dataList.get(videoId));
                } else {
                    databaseHelper.deleteFavorite(currentData);
                    CustomToast.showCustomToast(context,"已取消收藏");
                }
                currentData.setFavorite(newFavoriteState);
                updateFavoriteIcon(favoriteButtonViewHolder.imageView,newFavoriteState);
                notifyItemChanged(position);
            });
        }

        else if (holder instanceof RecommendVideoViewHolder) {
            RecommendVideoViewHolder recommendVideoViewHolder = (RecommendVideoViewHolder) holder;

            int videoIndex = position - 3;
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
    public int getItemViewType(int position) {
        if (position == 0){
            return VIEW_TYPE_SELECT;
        }
        else if (position == 1){
            return VIEW_TYPE_INFORMATION;
        }
        else if (position == 2){
            return VIEW_TYPE_FAVORITE_BUTTON;
        }
        else {
            return VIEW_TYPE_RECOMMEND_VIDEO;
        }
    }

    @Override
    public int getItemCount() {
        return recommendVideos.size() + 3;
    }


    public class RecommendVideoViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public RecommendVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.video_recommend);
        }
    }

    public class VideoInformationViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public VideoInformationViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.video_information);
        }
    }

    public class FavoriteButtonViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        Button buttonFavorite;
        public FavoriteButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.video_is_favorite);
            buttonFavorite = itemView.findViewById(R.id.video_favorite_button);
        }
    }

    private class SelectViewHolder extends RecyclerView.ViewHolder {
        public SelectViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private void updateFavoriteIcon(ImageView imageView, boolean isFavorite) {
        imageView.setImageResource(isFavorite ?
                R.drawable.is_favorite : R.drawable.is_not_favorite);
    }
}
