package ui.mine.favorite;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Data.FavoriteData;
import Data.FavoriteDatabaseHelper;
import base.BaseActivity;
import ui.detailed_video.VideoActivity;

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
        receiveAndShowImage();
    }

    private void receiveAndShowImage() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }

        // 情况1：接收相机图片的路径
//        String imagePath = intent.getStringExtra("IMAGE_PATH");
//        if (imagePath != null && !imagePath.isEmpty()) {
//            // 通过路径加载Bitmap（优化：避免OOM，可压缩图片）
//            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//            ivTargetImage.setImageBitmap(bitmap);
//            return;
//        }
//
//        // 情况2：接收相册图片的Uri
//        Uri imageUri = intent.getData();
//        if (imageUri != null) {
//            // 直接通过Uri设置图片（系统会自动处理Uri解析）
//            ivTargetImage.setImageURI(imageUri);
//            return;
//        }

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
                                itemHolder.imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(MyFavoritesActivity.this, VideoActivity.class);
                                        intent.putExtra("videoId",favorite.getOriginalId());
                                        startActivity(intent);
                                    }
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
                            if (holder instanceof NewFolderViewHolder) {
                                NewFolderViewHolder newFolderViewHolder = (NewFolderViewHolder) holder;

                                // 计算当前位置对应的文件夹索引（关键修复）
                                // 总偏移 = 1（Header） + （Header展开时的内容数量） + 1（CreatedFolders标题）
                                int baseOffset = 1 + (isHeaderExpanded ? folderMap.get(folderNames.get(0)).size() : 0) + 1;
                                int folderIndex = position - baseOffset; // 得到在folderNames中的索引（从1开始，排除默认文件夹）

                                // 校验索引合法性（folderNames第0项是默认文件夹，从1开始是自定义文件夹）
                                if (folderIndex < 0 || folderIndex >= folderNames.size() - 1) {
                                    return;
                                }
                                String newFolderName = folderNames.get(folderIndex + 1); // +1跳过默认文件夹

                                // 以下为原有绑定逻辑（保留）
                                holder.itemView.setOnClickListener(v -> {
                                    Intent intent = new Intent(MyFavoritesActivity.this, FolderActivity.class);
                                    intent.putExtra("folderName", newFolderName);
                                    v.getContext().startActivity(intent);
                                });
                                newFolderViewHolder.textViewNewFolderName.setText(newFolderName);
                                newFolderViewHolder.textViewFavoriteSize.setText("" + folderMap.get(newFolderName).size());
                                newFolderViewHolder.buttonNewFolderMore.setOnClickListener(v -> {
                                    FolderMoreDialogFragment folderMoreDialogFragment = new FolderMoreDialogFragment();
                                    folderMoreDialogFragment.setListener(() -> {
                                        databaseHelper.deleteFolder(newFolderName);
                                        updateFavoriteList();
                                        folderMoreDialogFragment.dismiss();
                                    });
                                    folderMoreDialogFragment.show(getSupportFragmentManager(), folderMoreDialogFragment.getTag());
                                });

                                // 加载文件夹封面（保留）
                                String folderCover = databaseHelper.getFolderCover(newFolderName);
                                if (folderCover != null && !folderCover.isEmpty()) {
                                    newFolderViewHolder.imageViewCover.setVisibility(View.VISIBLE);
                                    newFolderViewHolder.imageViewNumber.setVisibility(View.VISIBLE);
                                    newFolderViewHolder.textViewSizeCover.setVisibility(View.VISIBLE);
                                    if (folderCover.startsWith("/")) {
                                        Bitmap compressedBitmap = decodeCompressedBitmap(folderCover, 80, 80);
                                        newFolderViewHolder.imageViewCover.setImageBitmap(compressedBitmap);
                                    } else if (folderCover.startsWith("content://")) {
                                        String[] coverParts = folderCover.split("\\|");
                                        Uri coverUri = Uri.parse(coverParts[0]);
                                        try {
                                            newFolderViewHolder.imageViewCover.setImageURI(coverUri);
                                        } catch (SecurityException e) {
                                            try (InputStream inputStream = getContentResolver().openInputStream(coverUri)) {
                                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                                newFolderViewHolder.imageViewCover.setImageBitmap(bitmap);
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                                newFolderViewHolder.imageViewCover.setVisibility(View.GONE);
                                            }
                                        }
                                    }
                                    newFolderViewHolder.textViewSizeCover.setText("" + folderMap.get(newFolderName).size());
                                } else {
                                    newFolderViewHolder.imageViewCover.setVisibility(View.GONE);
                                    newFolderViewHolder.imageViewNumber.setVisibility(View.GONE);
                                    newFolderViewHolder.textViewSizeCover.setVisibility(View.GONE);
                                }
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
            TextView textViewSizeCover;
            ImageView imageViewCover;
            ImageView imageViewNumber;
            public NewFolderViewHolder(@NonNull View itemView) {
                super(itemView);
                buttonNewFolderMore = itemView.findViewById(R.id.new_folder_more);
                textViewFavoriteSize = itemView.findViewById(R.id.new_folder_size);
                textViewNewFolderName = itemView.findViewById(R.id.new_folder_name);
                textViewSizeCover = itemView.findViewById(R.id.new_folder_size_cover);
                imageViewCover = itemView.findViewById(R.id.setPicture_cover);
                imageViewNumber = itemView.findViewById(R.id.folder_number_cover);
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
    // ---------------------- 新增工具方法：压缩加载图片（避免OOM） ----------------------
    /**
     * 根据路径压缩加载图片
     * @param imagePath 图片路径
     * @param reqWidthDp 目标宽度（dp转像素后）
     * @param reqHeightDp 目标高度（dp转像素后）
     * @return 压缩后的Bitmap
     */
    private Bitmap decodeCompressedBitmap(String imagePath, int reqWidthDp, int reqHeightDp) {
        // 1. dp转像素（适配不同屏幕）
        int reqWidth = dp2px(reqWidthDp);
        int reqHeight = dp2px(reqHeightDp);

        // 2. 先获取图片原始尺寸（不加载完整图片到内存）
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        // 3. 计算压缩比例
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // 4. 加载压缩后的图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, options);
    }

    /**
     * 计算图片压缩比例（inSampleSize：2表示宽高各压缩为1/2，总像素为1/4）
     */
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int originalWidth = options.outWidth;
        final int originalHeight = options.outHeight;
        int inSampleSize = 1;

        if (originalHeight > reqHeight || originalWidth > reqWidth) {
            final int halfWidth = originalWidth / 2;
            final int halfHeight = originalHeight / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * dp转像素工具方法
     */
    private int dp2px(int dpValue) {
        final float scale = MyFavoritesActivity.this.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
