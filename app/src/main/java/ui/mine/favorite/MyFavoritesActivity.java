package ui.mine.favorite;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.naojianghh.bilibili3.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Data.FavoriteData;
import Data.FavoriteDatabaseHelper;
import base.BaseActivity;

public class MyFavoritesActivity extends BaseActivity {
    private RecyclerView favoriteRecyclerView;
    private FavoriteDatabaseHelper databaseHelper;
    private Map<String, List<FavoriteData>> folderMap;
    private FavoriteAdapter favoriteAdapter;
    private final static String DEFAULT_FOLDER_NAME = "默认收藏夹";
    private boolean isHeaderExpanded = true;
    private boolean isCreatedFoldersExpanded = false;
    private Button buttonReturn;
    private Button buttonCreateFolder;
    private static final int NEW_FOLDER_NAME = 1;

    @Override
    protected void initViews() {
        favoriteRecyclerView = findViewById(R.id.favorites_rv);
        databaseHelper = new FavoriteDatabaseHelper(this);
        favoriteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        buttonReturn = findViewById(R.id.mine_return);
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        buttonCreateFolder = findViewById(R.id.mine_create_folder);
        buttonCreateFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyFavoritesActivity.this,CreateFolderActivity.class);
                startActivityForResult(intent,NEW_FOLDER_NAME);
            }
        });

        updateFavoriteList();
    }

    private void updateFavoriteList() {
        List<String> allFolderNames = databaseHelper.getAllFolderNames();
        List<FavoriteData> favorites = databaseHelper.getALLFavorites();
        folderMap = new HashMap<>();

        List<FavoriteData> defaultFolderFavorites = new ArrayList<>();
        for (FavoriteData favorite : favorites) {
            if (favorite.getFolderName().equals(DEFAULT_FOLDER_NAME)) {
                defaultFolderFavorites.add(favorite);
            }
        }
        folderMap.put(DEFAULT_FOLDER_NAME, defaultFolderFavorites);

        for (FavoriteData favorite : favorites) {
            String folderName = favorite.getFolderName();
            if (!folderName.equals(DEFAULT_FOLDER_NAME)) {
                if (!folderMap.containsKey(folderName)) {
                    folderMap.put(folderName, new ArrayList<>());
                }
                folderMap.get(folderName).add(favorite);
            }
        }

        for (String folderName : allFolderNames) {
            if (!folderMap.containsKey(folderName)) {
                folderMap.put(folderName, new ArrayList<>());
            }
        }

        List<String> folderNames = new ArrayList<>(folderMap.keySet());
        folderNames.remove(DEFAULT_FOLDER_NAME);
        folderNames.add(0, DEFAULT_FOLDER_NAME);

        favoriteAdapter = new FavoriteAdapter(folderNames,folderMap);
        favoriteRecyclerView.setAdapter(favoriteAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateFavoriteList();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_favorities;
    }

    public class FavoriteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM = 1;
        private static final int TYPE_LAST = 2;
        private static final int TYPE_NEW_FOLDER = 3;
        private static final int TYPE_CREATED_FOLDERS = 4;
        private List<String> folderNames;
        private Map<String, List<FavoriteData>> folderMap;
        private Map<String, Boolean> isExpandedMap;

        public FavoriteAdapter(List<String> folderNames,Map<String,List<FavoriteData>> folderMap) {
            this.folderNames = folderNames;
            this.folderMap = folderMap;
            isExpandedMap = new HashMap<>();
            for (String folderName : folderNames){
                isExpandedMap.put(folderName, false);
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEADER) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder_header,parent,false);
                return new HeaderViewHolder(view);
            } else if (viewType == TYPE_ITEM ){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_list,parent,false);
                return new ItemViewHolder(view);
            }
            else if (viewType == TYPE_LAST){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder_last,parent,false);
                return new LastViewHolder(view);
            }
            else if (viewType == TYPE_NEW_FOLDER){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder_new,parent,false);
                return new NewFolderViewHolder(view);
            }
            else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_created_folders,parent,false);
                return new CreatedFoldersViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            for (String folderName : folderNames) {
                if (position == 0) {
                    if (holder instanceof HeaderViewHolder) {
                        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
                        headerHolder.folderNameTextView.setText(folderName);
                        headerHolder.folderCount.setText(""+folderMap.get(folderName).size());

                        headerHolder.moreButton.setOnClickListener(v -> {
                            FolderMoreDialogFragment folderMoreDialogFragment = new FolderMoreDialogFragment();
                            folderMoreDialogFragment.setListener(new FolderMoreDialogFragment.OnDeleteButtonClickListener() {
                                @Override
                                public void onDeleteButtonClick() {
                                    databaseHelper.deleteFolder(folderName);
                                    updateFavoriteList();
                                    folderMoreDialogFragment.dismiss();
                                }
                            });
                            folderMoreDialogFragment.show(getSupportFragmentManager(),folderMoreDialogFragment.getTag());
                        });
                        if (isHeaderExpanded){
                            headerHolder.expand.setImageResource(R.drawable.favorite_expanded);
                        }
                        else {
                            headerHolder.expand.setImageResource(R.drawable.favorite_unexpanded);
                        }
                        headerHolder.expand.setOnClickListener(v -> {
                            isHeaderExpanded = !isHeaderExpanded;
                            notifyDataSetChanged();
                        });
                    }
                    return;
                } else {
                    int itemIndex = 1;
                    if (isHeaderExpanded) {
                        List<FavoriteData> favorites = folderMap.getOrDefault(folderName,new ArrayList<>());
                        if (position < itemIndex + favorites.size()) {
                            if (holder instanceof ItemViewHolder) {
                                ItemViewHolder itemHolder = (ItemViewHolder) holder;
                                FavoriteData favorite = favorites.get(position - itemIndex);
                                itemHolder.imageView.setImageResource(favorite.getVideoListImageResourceId());
                                itemHolder.moreButton.setOnClickListener(v -> {
                                    VideoMoreDialogFragment videoMoreDialogFragment = new VideoMoreDialogFragment();
                                    videoMoreDialogFragment.setListener(new VideoMoreDialogFragment.OnDeleteButtonClickListener() {
                                        @Override
                                        public void onDeleteButtonClick() {
                                            databaseHelper.deleteFavorite(favorite.getId());
                                            updateFavoriteList();
                                            videoMoreDialogFragment.dismiss();
                                        }
                                    });
                                    videoMoreDialogFragment.show(getSupportFragmentManager(),videoMoreDialogFragment.getTag());
                                });

                            }
                            return;
                        }
                        itemIndex += favorites.size();
                    }
                    if (position == itemIndex) {
                        if (holder instanceof CreatedFoldersViewHolder){
                            CreatedFoldersViewHolder createdFoldersViewHolder = (CreatedFoldersViewHolder) holder;
                            createdFoldersViewHolder.textViewCreatedFoldersCount.setText("" + (folderNames.size() - 1));

                            if (isCreatedFoldersExpanded) {
                                createdFoldersViewHolder.imageViewCreatedFoldersExpend.setImageResource(R.drawable.favorite_expanded);
                            } else {
                                createdFoldersViewHolder.imageViewCreatedFoldersExpend.setImageResource(R.drawable.favorite_unexpanded);
                            }
                            createdFoldersViewHolder.imageViewCreatedFoldersExpend.setOnClickListener(v -> {
                                isCreatedFoldersExpanded = !isCreatedFoldersExpanded;
                                notifyDataSetChanged();
                            });
                        }
                    } else {
                        itemIndex ++;
                        if (position < itemIndex + (folderNames.size() - 1)) {
                            if (holder instanceof NewFolderViewHolder){
                                NewFolderViewHolder newFolderViewHolder = (NewFolderViewHolder) holder;
                                String newFolderName = folderNames.get(position - itemIndex + 1);

                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(MyFavoritesActivity.this,FolderActivity.class);
                                        intent.putExtra("folderName",newFolderName);
                                        v.getContext().startActivity(intent);
                                    }
                                });

                                newFolderViewHolder.textViewNewFolderName.setText(newFolderName);
                                newFolderViewHolder.textViewFavoriteSize.setText("" + folderMap.get(newFolderName).size());
                                newFolderViewHolder.buttonNewFolderMore.setOnClickListener(v -> {
                                    FolderMoreDialogFragment folderMoreDialogFragment = new FolderMoreDialogFragment();
                                    folderMoreDialogFragment.setListener(new FolderMoreDialogFragment.OnDeleteButtonClickListener() {
                                        @Override
                                        public void onDeleteButtonClick() {
                                            databaseHelper.deleteFolder(newFolderName);
                                            updateFavoriteList();
                                            folderMoreDialogFragment.dismiss();
                                        }
                                    });
                                    folderMoreDialogFragment.show(getSupportFragmentManager(), folderMoreDialogFragment.getTag());
                                });
                            }
                        }
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            int count = 2;
            if (isHeaderExpanded){
                count += folderMap.get(folderNames.get(0)).size();
            }
            if (isCreatedFoldersExpanded){
                count += folderNames.size() - 1;
            }
            count += 1;
            return count;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0){
                return TYPE_HEADER;
            }
            int index = 1;
            if (isHeaderExpanded){
                List<FavoriteData> headerFavorites = folderMap.get(folderNames.get(0));
                if (position < index + headerFavorites.size()) {
                    return TYPE_ITEM;
                }
                index += headerFavorites.size();
            }
            if (position == index){
                return TYPE_CREATED_FOLDERS;
            }
            index++;
            if (isCreatedFoldersExpanded){
                if (position < index + (folderNames.size() - 1)) {
                    return TYPE_NEW_FOLDER;
                }
            }
            if (position == getItemCount() - 1) {
                return TYPE_LAST;
            }
            return -1;
        }

        private class HeaderViewHolder extends RecyclerView.ViewHolder {
            TextView folderNameTextView;
            TextView folderCount;
            ImageView expand;
            ImageView moreButton;

            public HeaderViewHolder(@NonNull View itemView) {
                super(itemView);
                folderNameTextView = itemView.findViewById(R.id.folder_name_tv);
                folderCount = itemView.findViewById(R.id.folder_count);
                expand = itemView.findViewById(R.id.favorites_expand);
                moreButton = itemView.findViewById(R.id.folder_more);
            }
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            Button moreButton;

            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.favorites_iv);
                moreButton = itemView.findViewById(R.id.favorites_more);
            }
        }

        public class LastViewHolder extends RecyclerView.ViewHolder {

            public LastViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }

        public class NewFolderViewHolder extends RecyclerView.ViewHolder {
            ImageView buttonNewFolderMore;
            TextView textViewFavoriteSize;
            TextView textViewNewFolderName;
            public NewFolderViewHolder(@NonNull View itemView) {
                super(itemView);
                buttonNewFolderMore = itemView.findViewById(R.id.new_folder_more);
                textViewFavoriteSize = itemView.findViewById(R.id.new_folder_size);
                textViewNewFolderName = itemView.findViewById(R.id.new_folder_name);
            }
        }

        public class CreatedFoldersViewHolder extends RecyclerView.ViewHolder {
            TextView textViewCreatedFoldersCount;
            ImageView imageViewCreatedFoldersExpend;
            public CreatedFoldersViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewCreatedFoldersCount = itemView.findViewById(R.id.created_folders_count);
                imageViewCreatedFoldersExpend = itemView.findViewById(R.id.created_folders_favorites_expand);
            }
        }
    }
}
