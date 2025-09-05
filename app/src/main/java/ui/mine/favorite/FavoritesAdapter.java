package ui.mine.favorite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.naojianghh.bilibili3.R;

import java.util.List;

import Data.FavoriteData;
import ui.detailed_video.VideoActivity;

public class FavoritesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<FavoriteData> favorites;
    private static final int TYPE_FAVORITE = 0;
    private static final int TYPE_FUNCTION = 1;
    private Context context;

    public FavoritesAdapter(List<FavoriteData> favorites,Context context) {
        this.favorites = favorites;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_FAVORITE){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder_favorite_list,parent,false);
            return new FavoriteViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder_favorite_function,parent,false);
            return new FavoriteFunctionViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof FavoriteViewHolder){
            FavoriteViewHolder favoriteViewHolder = (FavoriteViewHolder) holder;
            favoriteViewHolder.imageViewFavorite.setImageResource(favorites.get(position - 1).getVideoListImageResourceId());
            favoriteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, VideoActivity.class);
                    intent.putExtra("videoId",favorites.get(position - 1).getOriginalId());
                    context.startActivity(intent);
                }
            });
        }
        else {
            FavoriteFunctionViewHolder favoriteFunctionViewHolder = (FavoriteFunctionViewHolder) holder;
            favoriteFunctionViewHolder.textViewSize.setText("" + favorites.size() + "个内容");
        }
    }

    @Override
    public int getItemCount() {
        if (favorites.size() == 0){
            return 0;
        }
        else {
            return favorites.size() + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return TYPE_FUNCTION;
        }
        else {
            return TYPE_FAVORITE;
        }
    }

    public class FavoriteViewHolder extends RecyclerView.ViewHolder{
        ImageView imageViewFavorite;
        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewFavorite = itemView.findViewById(R.id.folder_favorite);
        }
    }

    public class FavoriteFunctionViewHolder extends RecyclerView.ViewHolder{
        TextView textViewSize;

        public FavoriteFunctionViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSize = itemView.findViewById(R.id.folder_favorite_size);

        }
    }
}
