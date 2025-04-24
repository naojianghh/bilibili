package ui.detailed_video;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.naojianghh.bilibili3.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FoldersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Map<String, Integer> folderDataCountMap = new HashMap<>();
    private List<String> folderNames = new ArrayList<>();
    private RecyclerView folderListView;

    public void setFolderDataCountMap(Map<String, Integer> folderDataCountMap) {
        this.folderDataCountMap = folderDataCountMap;
    }

    public void setFolderNames(List<String> folderNames) {
        this.folderNames = folderNames;
    }

    public void setFolderListView(RecyclerView folderListView) {
        this.folderListView = folderListView;
    }


    public interface OnSelectClickListener {
        void onSelectClick(String folderName);
    }

    private OnSelectClickListener listener;

    public void setListener(OnSelectClickListener listener) {
        this.listener = listener;
    }

    public FoldersAdapter(Map<String, Integer> folderDataCountMap,List<String> folderNames) {
        this.folderDataCountMap = folderDataCountMap;
        this.folderNames = folderNames;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_folder,parent,false);
        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        FolderViewHolder folderViewHolder = (FolderViewHolder) holder;
        folderViewHolder.textViewFolderName.setText(folderNames.get(position));
        if (folderDataCountMap.get(folderNames.get(position)) == null){
            folderViewHolder.textViewFolderSize.setText( "" + 0 + "个内容");
        }
        else {
            folderViewHolder.textViewFolderSize.setText("" + folderDataCountMap.get(folderNames.get(position)) + "个内容");
        }
        if (position == 0){
            folderViewHolder.isSelected = true;
            listener.onSelectClick(folderNames.get(0));
        }

        if (folderViewHolder.isSelected){
            folderViewHolder.imageViewSelected.setImageResource(R.drawable.video_folder_select);
        } else {
            folderViewHolder.imageViewSelected.setImageResource(R.drawable.video_folder_unselect);
        }

        folderViewHolder.imageViewSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String clickedFolderName = folderNames.get(position);
                if (listener != null){
                    listener.onSelectClick(clickedFolderName);
                }
                folderViewHolder.isSelected =!folderViewHolder.isSelected;
                if (folderViewHolder.isSelected) {
                    folderViewHolder.imageViewSelected.setImageResource(R.drawable.video_folder_select);
                    for (int i = 0; i < getItemCount(); i++) {
                        if (i != position) {
                            RecyclerView.ViewHolder otherHolder = folderListView.findViewHolderForAdapterPosition(i);
                            if (otherHolder instanceof FolderViewHolder) {
                                ((FolderViewHolder) otherHolder).isSelected = false;
                                ((FolderViewHolder) otherHolder).imageViewSelected.setImageResource(R.drawable.video_folder_unselect);
                            }
                        }
                    }
                } else {
                    folderViewHolder.imageViewSelected.setImageResource(R.drawable.video_folder_unselect);
                    boolean allUnselected = true;
                    for (int i = 0; i < getItemCount(); i++) {
                        RecyclerView.ViewHolder otherHolder = folderListView.findViewHolderForAdapterPosition(i);
                        if (otherHolder instanceof FolderViewHolder) {
                            if (((FolderViewHolder) otherHolder).isSelected) {
                                allUnselected = false;
                                break;
                            }
                        }
                    }
                    if (allUnselected && listener != null) {
                        listener.onSelectClick(null);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return folderNames.size();
    }

    public class FolderViewHolder extends RecyclerView.ViewHolder{
        TextView textViewFolderName;
        TextView textViewFolderSize;
        ImageView imageViewSelected;
        boolean isSelected = false;
        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFolderName = itemView.findViewById(R.id.video_folder_name);
            textViewFolderSize = itemView.findViewById(R.id.video_folder_size);
            imageViewSelected = itemView.findViewById(R.id.video_folder_select);
        }
    }

}
