package ui.detailed_video;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.naojianghh.bilibili3.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Data.FavoriteData;
import Data.Data;
import Data.FavoriteDatabaseHelper;
import base.BaseActivity;
import ui.MainActivity;

public class VideoActivity extends BaseActivity implements RecommendVideoAdapter.FavoriteActionListener {
    private ImageView favoriteButton;
    private FavoriteDatabaseHelper databaseHelper;
    private List<Data> dataList = Data.getDataList();
    private Button buttonReturn;
    private Button buttonGotoHome;


    @Override
    protected void initViews() {

        buttonReturn = findViewById(R.id.video_return);
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonGotoHome = findViewById(R.id.video_goto_home);
        buttonGotoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VideoActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        Intent intent = getIntent();
        if (intent != null){
            int videoId = intent.getIntExtra("videoId",-1);
            ImageView imageView = findViewById(R.id.video_iv);
            imageView.setImageResource(dataList.get(videoId).getVideoImageResourceId());


            databaseHelper = new FavoriteDatabaseHelper(this);


            List<Data> recommendVideos = new ArrayList<>();
            List<Integer> selectedVideos = new ArrayList<>();
            selectedVideos.add(videoId);
            Random random = new Random();
            int dataSize = dataList.size();
            while (recommendVideos.size() < 3 && dataSize > 0){
                int randomIndex = random.nextInt(dataSize);
                if (!selectedVideos.contains(randomIndex)) {
                    recommendVideos.add(dataList.get(randomIndex));
                    selectedVideos.add(randomIndex);
                    dataSize--;
                }
            }

            RecyclerView recyclerViewRecommendVideo = findViewById(R.id.recommend_video_list);
            RecommendVideoAdapter adapter = new RecommendVideoAdapter(recommendVideos,this,videoId,this);
            recyclerViewRecommendVideo.setLayoutManager(new LinearLayoutManager(this));

            recyclerViewRecommendVideo.setAdapter(adapter);

        }

        getSupportFragmentManager().setFragmentResultListener("request_key",this,((requestKey, result) -> {
            String selectedFolderName = result.getString("selected_folder_name");
            if (selectedFolderName != null) {
                Snackbar modifiedSnackbar = Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_SHORT);

                View snackbarView = modifiedSnackbar.getView();
                snackbarView.setBackgroundColor(Color.TRANSPARENT);

                ViewGroup.LayoutParams params = snackbarView.getLayoutParams();
                if (params instanceof CoordinatorLayout.LayoutParams) {
                    CoordinatorLayout.LayoutParams clParams = (CoordinatorLayout.LayoutParams) params;
                    clParams.setMargins(8, 0, 8, 0);
                    snackbarView.setLayoutParams(clParams);
                }

                View customToastModifiedView = getLayoutInflater().inflate(R.layout.snack_bar_custom_modified, null);

                customToastModifiedView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));

                TextView toastModifiedText = customToastModifiedView.findViewById(R.id.toast_modified_text);
                toastModifiedText.setText("已加入\"" + selectedFolderName + "\"");
                Button goToLook = customToastModifiedView.findViewById(R.id.toast_modified_look);
                goToLook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbarView;
                snackbarLayout.removeAllViews();
                snackbarLayout.addView(customToastModifiedView);

                modifiedSnackbar.show();
            }
        }));


    }



    @Override
    protected int getLayoutId() {
        return R.layout.activity_video;
    }


    @Override
    public void onAddToDefaultFolder(Data data) {
        handleFavoriteAction(data);
    }

    @Override
    public void onChangeFolderRequest(Data data, FavoriteData favorite) {
        handleChangeFolder(data,favorite);
    }

    private void handleFavoriteAction(Data data) {
        String defaultFolderName = "默认收藏夹";
        FavoriteData favorite = createFavoriteData(data, defaultFolderName);
        databaseHelper.addFavorite(favorite);
        showCustomSnackbar(data, favorite);
    }

    private void handleChangeFolder(Data data, FavoriteData favorite) {
        deleteFromDefaultFolder(favorite);
        showFavoriteDialog(data);
    }

    private FavoriteData createFavoriteData(Data data, String folderName) {
        FavoriteData favorite = new FavoriteData(
                data.getDescription(),
                data.getImageResourceId(),
                data.getVideoImageResourceId(),
                data.getVideoListImageResourceId()
        );
        favorite.setFolderName(folderName);
        return favorite;
    }

    private void deleteFromDefaultFolder(FavoriteData favorite) {
        List<FavoriteData> favorites = databaseHelper.getALLFavorites();
        for (FavoriteData fav : favorites) {
            if (fav.getFolderName().equals("默认收藏夹") &&
                    fav.getDescription().equals(favorite.getDescription()) &&
                    fav.getImageResourceId() == favorite.getImageResourceId() &&
                    fav.getVideoImageResourceId() == favorite.getVideoImageResourceId() &&
                    fav.getVideoListImageResourceId() == favorite.getVideoListImageResourceId()) {
                databaseHelper.deleteFavorite(fav.getId());
                break;
            }
        }
    }

    private void showCustomSnackbar(Data data, FavoriteData favorite) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_SHORT);

        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.TRANSPARENT);

        ViewGroup.LayoutParams params = snackbarView.getLayoutParams();
        if (params instanceof CoordinatorLayout.LayoutParams) {
            CoordinatorLayout.LayoutParams clParams = (CoordinatorLayout.LayoutParams) params;
            clParams.setMargins(8, 0, 8, 0);
            snackbarView.setLayoutParams(clParams);
        }

        View customToastView = getLayoutInflater().inflate(R.layout.snack_bar_custom, null);
        customToastView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        TextView toastText = customToastView.findViewById(R.id.toast_text);
        toastText.setText("已加入\"默认收藏夹\"");

        Button changeFolderButton = customToastView.findViewById(R.id.change_folder_button);
        changeFolderButton.setOnClickListener(v -> {
            onChangeFolderRequest(data, favorite);
        });

        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbarView;
        snackbarLayout.removeAllViews();
        snackbarLayout.addView(customToastView);

        snackbar.show();
    }
    private void showFavoriteDialog(Data data) {
        FavoriteDialogFragment dialog = new FavoriteDialogFragment(data);
        dialog.show(getSupportFragmentManager(), "FavoriteBottomSheet");
    }
}


