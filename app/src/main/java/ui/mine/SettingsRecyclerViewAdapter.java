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
import ui.MainActivity;
import ui.mine.favorite.FavoritesAdapter;
import ui.mine.favorite.MyFavoritesActivity;
import utils.SpUtils;

public class SettingsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_FUNCTION = 0;
    private static final int TYPE_QUIT = 1;



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_FUNCTION){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_settings_function,parent,false);
            return new SettingsRecyclerViewAdapter.FunctionViewHolder(view);
        }

        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_settings_quit,parent,false);
            return new SettingsRecyclerViewAdapter.QuitViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FunctionViewHolder) {

            FunctionViewHolder functionViewHolder = (FunctionViewHolder) holder;

        } else {

            QuitViewHolder quitViewHolder = (QuitViewHolder) holder;
            quitViewHolder.quitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SpUtils.clearToken(holder.itemView.getContext());
                    Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
                    holder.itemView.getContext().startActivity(intent);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return TYPE_FUNCTION;
        }
        else {
            return TYPE_QUIT;
        }
    }


    public class FunctionViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public FunctionViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class QuitViewHolder extends RecyclerView.ViewHolder{
        Button quitButton;
        public QuitViewHolder(@NonNull View itemView) {
            super(itemView);
            quitButton = itemView.findViewById(R.id.quit);
        }
    }

}
