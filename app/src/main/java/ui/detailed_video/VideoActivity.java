package ui.detailed_video;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import logic.network.VideoData;
import ui.MainActivity;

public class VideoActivity extends BaseActivity implements RecommendVideoAdapter.FavoriteActionListener {
    // 控件声明
    private ImageView favoriteButton;
    private FavoriteDatabaseHelper databaseHelper;
    private List<Data> dataList = Data.getDataList();
    private Button buttonReturn;
    private Button buttonGotoHome;
    private VideoView videoView;
    private ConstraintLayout topController;
    private LinearLayout bottomController;
    private Handler handler = new Handler();
    private boolean isControlVisible = false;
    private SeekBar seekBar;
    private ImageView btnPlayPause;
    private ImageView btnFullScreen;
    private TextView videoTime;
    private int totalDuration; // 视频总时长（毫秒）
    private boolean isProgressUpdating = false; // 进度更新任务标记

    private boolean isDragging = false; // 是否正在拖动进度条
    private boolean wasPlayingBeforeDrag = false; // 拖动前的播放状态

    // 进度更新和控制栏隐藏的Runnable
    private Runnable progressRunnable;
    private Runnable controlHideRunnable;

    private ActivityResultLauncher<Intent> fullScreenResultLauncher;


    @Override
    protected void initViews() {
        // 1. 初始化全屏返回结果监听（必须在initAllViews之前初始化）
        initFullScreenResultLauncher();

        // 2. 初始化所有控件
        initAllViews();

        // 3. 初始化视频播放核心逻辑
        initVideoPlayLogic();

        // 4. 初始化控制栏交互（显示/隐藏、刷新隐藏时间）
        initControlBarLogic();

        // 5. 初始化推荐视频列表
        initRecommendVideoList();

        // 6. 初始化收藏相关逻辑（Fragment结果监听）
        initFavoriteFragmentListener();
    }

    private void initFullScreenResultLauncher() {
        fullScreenResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        // 只有当返回结果成功时才处理（FullScreenActivity中设置了RESULT_OK）
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Intent data = result.getData();
                            // 1. 获取从全屏页面返回的播放进度和状态
                            int returnPosition = data.getIntExtra("current_position", 0);
                            boolean returnIsPlaying = data.getBooleanExtra("is_playing", false);

                            // 2. 恢复视频播放状态（核心逻辑）
                            restoreVideoState(returnPosition, returnIsPlaying);
                        }
                    }
                }
        );
    }


    private void restoreVideoState(int targetPosition, boolean isPlaying) {
        // 确保VideoView已初始化且视频已准备完成
        if (videoView != null && totalDuration > 0) {
            // 1. 停止当前的进度更新任务，避免冲突
            stopProgressUpdate();

            // 2. 跳转到目标进度（全屏时的播放位置）
            videoView.seekTo(targetPosition);

            // 3. 恢复播放状态
            if (isPlaying) {
                videoView.start();
                btnPlayPause.setImageResource(R.drawable.pause); // 注意：与你的资源名保持一致
                startProgressUpdate(); // 恢复进度更新
            } else {
                videoView.pause();
                btnPlayPause.setImageResource(R.drawable.start); // 注意：与你的资源名保持一致
            }

            // 4. 同步更新SeekBar和时间显示，避免UI不一致
            seekBar.setProgress(targetPosition);
            videoTime.setText(convertTime(targetPosition));

            // 5. 显示控制栏，让用户看到状态已恢复
            if (!isControlVisible) {
                toggleControlVisibility();
            }
        }
    }


    private void initAllViews() {
        // 视频相关控件
        videoView = findViewById(R.id.video);
        seekBar = findViewById(R.id.seek_bar);
        videoTime = findViewById(R.id.video_time);
        btnPlayPause = findViewById(R.id.pause_or_start);
        btnFullScreen = findViewById(R.id.full_screen);

        // 控制栏控件
        topController = findViewById(R.id.videoTopController);
        bottomController = findViewById(R.id.videoBottomController);

        // 按钮控件
        buttonReturn = findViewById(R.id.video_return);
        buttonGotoHome = findViewById(R.id.video_goto_home);

        // 初始化Runnable
        initRunnables();
    }


    private void initRunnables() {
        // 进度更新Runnable：用VideoView实际进度更新，不依赖SeekBar
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isFinishing() && !isDragging) {
                    int currentPosition = videoView.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    videoTime.setText(convertTime(currentPosition));
                    handler.postDelayed(this, 1000);
                }
            }
        };

        // 控制栏隐藏Runnable
        controlHideRunnable = new Runnable() {
            @Override
            public void run() {
                if (isControlVisible && !isFinishing() && !isDragging) {
                    topController.setVisibility(View.GONE);
                    bottomController.setVisibility(View.GONE);
                    isControlVisible = false;
                }
            }
        };
    }


    private void initVideoPlayLogic() {
        // 设置视频源
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video);
        videoView.setVideoURI(videoUri);
        videoView.setMediaController(null);
        videoView.requestFocus();
        videoView.start();
        btnPlayPause.setImageResource(R.drawable.pause);

        // 视频准备完成监听：获取总时长
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                totalDuration = mp.getDuration();
                seekBar.setMax(totalDuration);
                startProgressUpdate();
                videoTime.setText(convertTime(0));
            }
        });

        // 视频播放完成监听
        videoView.setOnCompletionListener(mp -> {
            seekBar.setProgress(0);
            videoTime.setText(convertTime(0));
            btnPlayPause.setImageResource(R.drawable.start);
            stopProgressUpdate();
        });

        // 进度条拖动监听
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && isDragging) {
                    videoView.seekTo(progress);
                    // 延迟更新时间，确保跳转完成
                    handler.postDelayed(() -> {
                        int actualPosition = videoView.getCurrentPosition();
                        videoTime.setText(convertTime(actualPosition));
                    }, 50);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isDragging = true;
                wasPlayingBeforeDrag = videoView.isPlaying();
                videoView.pause();
                stopProgressUpdate();
                refreshControlHideTime();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isDragging = false;

                // 关键修改：延迟获取实际位置，确保seekTo完成
                handler.postDelayed(() -> {
                    int finalActualPosition = videoView.getCurrentPosition();
                    seekBar.setProgress(finalActualPosition);
                    videoTime.setText(convertTime(finalActualPosition));
                }, 100); // 100ms延迟，足够视频完成跳转

                if (wasPlayingBeforeDrag) {
                    videoView.start();
                    btnPlayPause.setImageResource(R.drawable.pause);
                } else {
                    btnPlayPause.setImageResource(R.drawable.start);
                }

                startProgressUpdate();
                refreshControlHideTime();
            }
        });
    }


    private void initControlBarLogic() {
        // 点击视频画面切换控制栏显示/隐藏
        videoView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                toggleControlVisibility();
                return true;
            }
            return false;
        });

        // 播放/暂停按钮点击事件
        btnPlayPause.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                btnPlayPause.setImageResource(R.drawable.start);
            } else {
                videoView.start();
                btnPlayPause.setImageResource(R.drawable.pause);
                if (!isProgressUpdating) {
                    startProgressUpdate();
                }
            }
            refreshControlHideTime();
        });

        // 返回按钮点击事件
        buttonReturn.setOnClickListener(v -> finish());

        // 首页按钮点击事件
        buttonGotoHome.setOnClickListener(v -> {
            Intent intent = new Intent(VideoActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        // 关键修改：全屏按钮点击事件（用launcher启动，而非startActivity）
        btnFullScreen.setOnClickListener(v -> {
            Intent intent = new Intent(VideoActivity.this, FullScreenActivity.class);
            // 传递当前播放进度和状态到全屏页面
            intent.putExtra("current_position", videoView.getCurrentPosition());
            intent.putExtra("is_playing", videoView.isPlaying());
            // 用launcher启动，监听返回结果
            fullScreenResultLauncher.launch(intent);
        });
    }
    private void initRecommendVideoList() {
        Intent intent = getIntent();
        if (intent == null) return;

        int videoId = intent.getIntExtra("videoId", -1);
        databaseHelper = new FavoriteDatabaseHelper(this);

        List<Data> recommendVideos = new ArrayList<>();
        List<Integer> selectedVideos = new ArrayList<>();
        if (videoId != -1) {
            selectedVideos.add(videoId);
            ImageView videoCoverIv = findViewById(R.id.video_iv);
            videoCoverIv.setImageResource(dataList.get(videoId).getVideoImageResourceId());
        }

        // 随机选择推荐视频
        Random random = new Random();
        int dataSize = dataList.size();
        while (recommendVideos.size() < 5 && dataSize > 0) {
            int randomIndex = random.nextInt(dataSize);
            if (!selectedVideos.contains(randomIndex)) {
                recommendVideos.add(dataList.get(randomIndex));
                selectedVideos.add(randomIndex);
                dataSize--;
            }
        }

        // 设置RecyclerView
        RecyclerView recyclerViewRecommend = findViewById(R.id.recommend_video_list);
        recyclerViewRecommend.setLayoutManager(new LinearLayoutManager(this));
        if (videoId != -1) {
            RecommendVideoAdapter adapter = new RecommendVideoAdapter(recommendVideos, this, videoId, this);
            recyclerViewRecommend.setAdapter(adapter);
        } else {
            VideoData videoData = (VideoData) intent.getSerializableExtra("video_data");
            VideoAdapter adapter = new VideoAdapter(recommendVideos, this, videoData);
            recyclerViewRecommend.setAdapter(adapter);
        }
    }

    private void initFavoriteFragmentListener() {
        getSupportFragmentManager().setFragmentResultListener("request_key", this, ((requestKey, result) -> {
            String selectedFolderName = result.getString("selected_folder_name");
            if (selectedFolderName != null && !isFinishing()) {
                showCustomModifiedSnackbar(selectedFolderName);
            }
        }));
    }


    private String convertTime(int currentTimeMs) {
        if (totalDuration <= 0) {
            return "00:00/00:00";
        }

        // 当前时间转换
        int currentTotalSec = currentTimeMs / 1000;
        int currentMin = currentTotalSec / 60;
        int currentSec = currentTotalSec % 60;

        // 总时长转换
        int totalSec = totalDuration / 1000;
        int totalMin = totalSec / 60;
        int totalSecRemain = totalSec % 60;

        return String.format("%02d:%02d/%02d:%02d",
                currentMin, currentSec,
                totalMin, totalSecRemain);
    }

    private void toggleControlVisibility() {
        if (isControlVisible) {
            topController.setVisibility(View.GONE);
            bottomController.setVisibility(View.GONE);
            handler.removeCallbacks(controlHideRunnable);
        } else {
            topController.setVisibility(View.VISIBLE);
            bottomController.setVisibility(View.VISIBLE);
            refreshControlHideTime();
        }
        isControlVisible = !isControlVisible;
    }

    private void refreshControlHideTime() {
        if (isControlVisible && !isFinishing() && !isDragging) {
            handler.removeCallbacks(controlHideRunnable);
            handler.postDelayed(controlHideRunnable, 3000);
        }
    }

    private void startProgressUpdate() {
        if (!isProgressUpdating && !isFinishing() && !isDragging) {
            isProgressUpdating = true;
            handler.removeCallbacks(progressRunnable);
            handler.post(progressRunnable);
        }
    }

    private void stopProgressUpdate() {
        if (isProgressUpdating) {
            isProgressUpdating = false;
            handler.removeCallbacks(progressRunnable);
        }
    }
    private void showCustomModifiedSnackbar(String folderName) {
        Snackbar modifiedSnackbar = Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_SHORT);
        if (modifiedSnackbar.getView() == null) return;

        View snackbarView = modifiedSnackbar.getView();
        snackbarView.setBackgroundColor(Color.TRANSPARENT);

        ViewGroup.LayoutParams params = snackbarView.getLayoutParams();
        if (params instanceof CoordinatorLayout.LayoutParams) {
            CoordinatorLayout.LayoutParams clParams = (CoordinatorLayout.LayoutParams) params;
            clParams.setMargins(8, 0, 8, 0);
            snackbarView.setLayoutParams(clParams);
        }

        View customToastView = getLayoutInflater().inflate(R.layout.snack_bar_custom_modified, null);
        customToastView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        TextView toastText = customToastView.findViewById(R.id.toast_modified_text);
        toastText.setText("已加入\"" + folderName + "\"");

        Button goToLookBtn = customToastView.findViewById(R.id.toast_modified_look);
        goToLookBtn.setOnClickListener(v -> modifiedSnackbar.dismiss());

        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbarView;
        snackbarLayout.removeAllViews();
        snackbarLayout.addView(customToastView);

        modifiedSnackbar.show();
    }

    private void showCustomSnackbar(Data data, FavoriteData favorite) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_SHORT);
        if (snackbar.getView() == null) return;

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

        Button changeFolderBtn = customToastView.findViewById(R.id.change_folder_button);
        changeFolderBtn.setOnClickListener(v -> {
            snackbar.dismiss();
            onChangeFolderRequest(data, favorite);
        });

        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbarView;
        snackbarLayout.removeAllViews();
        snackbarLayout.addView(customToastView);

        snackbar.show();
    }



    /**
     * 显示收藏文件夹选择对话框
     */
    private void showFavoriteDialog(Data data) {
        FavoriteDialogFragment dialog = new FavoriteDialogFragment(data);
        dialog.show(getSupportFragmentManager(), "FavoriteBottomSheet");
    }

    /**
     * 处理添加到默认收藏夹的逻辑
     */
    @Override
    public void onAddToDefaultFolder(Data data) {
        String defaultFolderName = "默认收藏夹";
        FavoriteData favorite = createFavoriteData(data, defaultFolderName);
        databaseHelper.addFavorite(favorite);
        showCustomSnackbar(data, favorite);
    }

    /**
     * 处理更改收藏文件夹的逻辑
     */
    @Override
    public void onChangeFolderRequest(Data data, FavoriteData favorite) {
        deleteFromDefaultFolder(favorite);
        showFavoriteDialog(data);
    }

    /**
     * 创建收藏数据
     */
    private FavoriteData createFavoriteData(Data data, String folderName) {
        FavoriteData favorite = new FavoriteData(
                data.getDescription(),
                data.getImageResourceId(),
                data.getVideoImageResourceId(),
                data.getVideoListImageResourceId(),
                data.getId()
        );
        favorite.setFolderName(folderName);
        return favorite;
    }

    /**
     * 从默认收藏夹中删除
     */
    private void deleteFromDefaultFolder(FavoriteData favorite) {
        List<FavoriteData> favorites = databaseHelper.getALLFavorites();
        for (FavoriteData fav : favorites) {
            if (fav.getFolderName().equals("默认收藏夹") &&
                    fav.getOriginalId() == favorite.getOriginalId()) {
                databaseHelper.deleteFavorite(fav.getId());
                break;
            }
        }
    }

    /**
     * 获取布局ID
     */
    @Override
    protected int getLayoutId() {
        return R.layout.activity_video;
    }

    /**
     * 生命周期：销毁时释放资源
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 停止所有Handler任务
        handler.removeCallbacksAndMessages(null);
        // 释放VideoView资源
        if (videoView != null) {
            videoView.stopPlayback();
        }
        // 关闭数据库连接
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
