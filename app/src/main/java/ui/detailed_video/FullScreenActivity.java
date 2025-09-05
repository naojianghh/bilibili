package ui.detailed_video;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.naojianghh.bilibili3.R;

public class FullScreenActivity extends AppCompatActivity {

    private VideoView videoView;
    private ImageView playOrPause, fullScreenReturn;
    private TextView videoTime;
    private LinearLayout topController, bottomController;
    private SeekBar seekBar;
    private Handler handler = new Handler();
    private boolean isControlVisible = true;
    private int totalDuration;
    private boolean isProgressUpdating = false;
    private boolean isDragging = false;
    private boolean wasPlayingBeforeDrag = false;
    private Runnable progressRunnable;
    private Runnable controlHideRunnable;
    private int currentPosition;
    private boolean isPlaying;
    private String videoPath;

    // 新增变量：记录拖动目标位置，解决seek延迟问题
    private int targetSeekPosition = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_full_screen);

        getVideoStateFromIntent();
        hideSystemUI();
        initView();
        initRunnables();
        initVideoPlayLogic();
        initControlBarLogic();

        showControls();
        startControlHideTimer();
    }

    private void getVideoStateFromIntent() {
        Intent intent = getIntent();
        currentPosition = intent.getIntExtra("current_position", 0);
        isPlaying = intent.getBooleanExtra("is_playing", false);
        videoPath = intent.getStringExtra("video_path");
    }

    private void initView() {
        videoView = findViewById(R.id.videoView);
        playOrPause = findViewById(R.id.play_or_pause);
        topController = findViewById(R.id.top_controller);
        bottomController = findViewById(R.id.bottom_controller);
        seekBar = findViewById(R.id.seek_bar);
        fullScreenReturn = findViewById(R.id.full_screen_return);
        videoTime = findViewById(R.id.video_time);
    }

    private void initRunnables() {
        // 进度更新Runnable：增加对seek完成状态的判断
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isFinishing() && !isDragging) {
                    int currentPos = videoView.getCurrentPosition();

                    // 如果正在seek操作，检查是否已接近目标位置
                    if (targetSeekPosition != -1) {
                        // 当实际位置与目标位置差距小于500ms时，视为seek完成
                        if (Math.abs(currentPos - targetSeekPosition) < 500) {
                            targetSeekPosition = -1; // 重置目标位置
                        }
                    }

                    seekBar.setProgress(currentPos);
                    videoTime.setText(convertTime(currentPos));
                    handler.postDelayed(this, 1000);
                }
            }
        };

        controlHideRunnable = new Runnable() {
            @Override
            public void run() {
                if (isControlVisible && !isFinishing() && !isDragging && videoView.isPlaying()) {
                    hideControls();
                }
            }
        };
    }

    private void initVideoPlayLogic() {
        if (videoPath != null && !videoPath.isEmpty()) {
            videoView.setVideoPath(videoPath);
        } else {
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video);
            videoView.setVideoURI(videoUri);
        }
        videoView.setMediaController(null);
        videoView.requestFocus();

        // 视频准备完成监听
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // 增加对视频跳转的监听，解决seek延迟问题
                mp.setOnSeekCompleteListener(mp1 -> {
                    // seek完成后更新UI
                    int newPos = videoView.getCurrentPosition();
                    seekBar.setProgress(newPos);
                    videoTime.setText(convertTime(newPos));

                    // 如果是拖动后的seek操作，完成后恢复状态
                    if (targetSeekPosition != -1) {
                        targetSeekPosition = -1; // 重置目标位置

                        if (wasPlayingBeforeDrag) {
                            videoView.start();
                            startProgressUpdate();
                        }
                    }
                });

                totalDuration = mp.getDuration();
                seekBar.setMax(totalDuration);
                videoView.seekTo(currentPosition);

                if (isPlaying) {
                    videoView.start();
                    playOrPause.setImageResource(R.drawable.video_controller_bottom_left_pause);
                    startProgressUpdate();
                    startControlHideTimer();
                } else {
                    playOrPause.setImageResource(R.drawable.video_controller_bottom_left_play);
                    showControls();
                }
                videoTime.setText(convertTime(currentPosition));
            }
        });

        // 播放完成监听
        videoView.setOnCompletionListener(mp -> {
            seekBar.setProgress(0);
            videoTime.setText(convertTime(0));
            playOrPause.setImageResource(R.drawable.video_controller_bottom_left_play);
            stopProgressUpdate();
            showControls();
        });

        // SeekBar拖动监听：优化时间更新逻辑
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && isDragging) {
                    // 拖动时先显示目标进度，不立即更新到视频
                    videoTime.setText(convertTime(progress));
                    targetSeekPosition = progress; // 记录目标位置
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isDragging = true;
                wasPlayingBeforeDrag = videoView.isPlaying();
                videoView.pause();
                stopProgressUpdate();
                showControls();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isDragging = false;

                // 只有当目标位置有效时才执行seek操作
                if (targetSeekPosition != -1) {
                    videoView.seekTo(targetSeekPosition);
                    // 暂时不更新UI，等待seek完成后由OnSeekCompleteListener更新
                }

                if (wasPlayingBeforeDrag) {
                    playOrPause.setImageResource(R.drawable.video_controller_bottom_left_pause);
                    startControlHideTimer();
                } else {
                    playOrPause.setImageResource(R.drawable.video_controller_bottom_left_play);
                }
            }
        });
    }

    private void initControlBarLogic() {
        videoView.setOnTouchListener(new View.OnTouchListener() {
            private long lastTouchTime = 0;
            private static final long DOUBLE_TAP_THRESHOLD = 300;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastTouchTime < DOUBLE_TAP_THRESHOLD) {
                        lastTouchTime = 0;
                        return true;
                    }
                    lastTouchTime = currentTime;

                    toggleControlVisibility();
                    return true;
                }
                return false;
            }
        });

        playOrPause.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                playOrPause.setImageResource(R.drawable.video_controller_bottom_left_play);
                showControls();
            } else {
                videoView.start();
                playOrPause.setImageResource(R.drawable.video_controller_bottom_left_pause);
                if (!isProgressUpdating) startProgressUpdate();
                startControlHideTimer();
            }
        });

        fullScreenReturn.setOnClickListener(v -> {
            returnToVideoActivity();
        });

        topController.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                startControlHideTimer();
            }
            return false;
        });
        bottomController.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                startControlHideTimer();
            }
            return false;
        });
    }

    private String convertTime(int currentTimeMs) {
        if (totalDuration <= 0) return "00:00/00:00";

        int currentSec = currentTimeMs / 1000;
        int currentMin = currentSec / 60;
        currentSec = currentSec % 60;

        int totalSec = totalDuration / 1000;
        int totalMin = totalSec / 60;
        totalSec = totalSec % 60;

        return String.format("%02d:%02d/%02d:%02d", currentMin, currentSec, totalMin, totalSec);
    }

    private void toggleControlVisibility() {
        if (isControlVisible) {
            hideControls();
        } else {
            showControls();
            startControlHideTimer();
        }
    }

    private void showControls() {
        if (!isControlVisible) {
            topController.setVisibility(View.VISIBLE);
            bottomController.setVisibility(View.VISIBLE);
            isControlVisible = true;
        }
    }

    private void hideControls() {
        if (isControlVisible) {
            topController.setVisibility(View.GONE);
            bottomController.setVisibility(View.GONE);
            isControlVisible = false;
        }
    }

    private void startControlHideTimer() {
        handler.removeCallbacks(controlHideRunnable);
        handler.postDelayed(controlHideRunnable, 3000);
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

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideSystemUI();
    }

    private void returnToVideoActivity() {
        Intent intent = new Intent();
        intent.putExtra("current_position", videoView.getCurrentPosition());
        intent.putExtra("is_playing", videoView.isPlaying());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        returnToVideoActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (videoView != null) videoView.stopPlayback();
    }
}
