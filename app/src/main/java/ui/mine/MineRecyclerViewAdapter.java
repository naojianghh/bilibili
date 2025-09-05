package ui.mine;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.naojianghh.bilibili3.R;

import java.util.List;

import Data.FavoriteData;
import ui.mine.favorite.FavoritesAdapter;
import ui.mine.favorite.MyFavoritesActivity;

public class MineRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_FAVORITE = 0;
    private static final int TYPE_FUNCTION = 1;
    private static final int TYPE_SETTINGS = 2;
    private Context context;

    public MineRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_FAVORITE){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mine_favorite,parent,false);
            return new MineRecyclerViewAdapter.FavoriteViewHolder(view);
        }
        else if (viewType == TYPE_FUNCTION){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mine_function,parent,false);
            return new MineRecyclerViewAdapter.FunctionViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mine_settings,parent,false);
            return new MineRecyclerViewAdapter.SettingsViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FavoriteViewHolder){

            FavoriteViewHolder favoriteViewHolder = (FavoriteViewHolder) holder;
            favoriteViewHolder.imageViewFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MyFavoritesActivity.class);
                    favoriteViewHolder.itemView.getContext().startActivity(intent);
                }
            });

        } else if (holder instanceof FunctionViewHolder) {

            FunctionViewHolder functionViewHolder = (FunctionViewHolder) holder;

        } else {

            SettingsViewHolder settingsViewHolder = (SettingsViewHolder) holder;
            settingsViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SettingsActivity.class);
                    settingsViewHolder.itemView.getContext().startActivity(intent);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return TYPE_FAVORITE;
        }
        else if (position == 1) {
            return TYPE_FUNCTION;
        }
        else {
            return TYPE_SETTINGS;
        }
    }

    public class FavoriteViewHolder extends RecyclerView.ViewHolder{
        Button imageViewFavorite;
        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewFavorite = itemView.findViewById(R.id.item_mine_favorite);
        }
    }

    public class FunctionViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public FunctionViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_mine_function);
        }
    }

    public class SettingsViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public SettingsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_mine_settings);
        }
    }

}
