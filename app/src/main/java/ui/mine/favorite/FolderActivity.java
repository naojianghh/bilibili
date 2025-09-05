package ui.mine.favorite;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.naojianghh.bilibili3.R;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import Data.Data;
import Data.FavoriteDatabaseHelper;
import Data.FavoriteData;
import base.BaseActivity;

public class FolderActivity extends BaseActivity {

    private Button buttonReturn;
    private TextView textViewFolderName;
    private RecyclerView recyclerView;
    private FavoriteDatabaseHelper databaseHelper;
    private List<FavoriteData> favorites;
    private List<FavoriteData> currentFavorites;
    private ImageView setPicture;

    @Override
    protected void initViews() {
        databaseHelper = new FavoriteDatabaseHelper(this);
        favorites = databaseHelper.getALLFavorites();
        buttonReturn = findViewById(R.id.folder_return);
        textViewFolderName = findViewById(R.id.folder_name);
        recyclerView = findViewById(R.id.folder_rv);
        setPicture = findViewById(R.id.setPicture_folder);

        String folderName = getIntent().getStringExtra("folderName");
        if (folderName != null){
            currentFavorites = new ArrayList<FavoriteData>();
            for (FavoriteData favoriteData : favorites){
                if (favoriteData.getFolderName().equals(folderName)){
                    currentFavorites.add(favoriteData);
                }
            }
            String coverPath = databaseHelper.getFolderCover(folderName);
            if (coverPath != null && !coverPath.isEmpty()){
                setPicture.setVisibility(View.VISIBLE);
                // 区分封面类型：路径（相机图）/ Uri字符串（相册图）
                if (coverPath.startsWith("/")) {
                    // 相机图：通过路径加载图片（压缩避免OOM）
                    Bitmap compressedBitmap = decodeCompressedBitmap(coverPath, 80, 80); // 80dp对应尺寸
                    setPicture.setImageBitmap(compressedBitmap);
                } // 在加载相册图片的地方修改
                else if (coverPath.startsWith("content://")) {
                    // 检查是否有我们添加的权限标记
                    String[] coverParts = coverPath.split("\\|");
                    Uri coverUri = Uri.parse(coverParts[0]);

                    try {
                        // 尝试直接设置图片
                        setPicture.setImageURI(coverUri);
                    } catch (SecurityException e) {
                        // 如果直接设置失败，尝试使用ContentResolver打开并获取Bitmap
                        try (InputStream inputStream = getContentResolver().openInputStream(coverUri)) {
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            setPicture.setImageBitmap(bitmap);
                        } catch (Exception ex) {
                            // 处理所有可能的异常
                            ex.printStackTrace();
                            setPicture.setVisibility(View.GONE);
                        }
                    }
                }
            } else {
                setPicture.setVisibility(View.GONE);
            }

        }



        FavoritesAdapter adapter = new FavoritesAdapter(currentFavorites,FolderActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        textViewFolderName.setText(folderName);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_folder;
    }

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
        final float scale = FolderActivity.this.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
