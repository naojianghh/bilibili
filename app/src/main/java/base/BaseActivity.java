package base;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.naojianghh.bilibili3.R;


public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        EdgeToEdge.enable(this);
        View rootView = findViewById(android.R.id.content); // 获取内容根视图
        if (rootView!= null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom); // 设置顶部内边距为状态栏高度
                return insets.consumeSystemWindowInsets();
            });
        } else {
            android.util.Log.e("BaseActivity", "根视图为 null，请检查布局文件是否正确加载");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            WindowCompat.setDecorFitsSystemWindows(window, false);
            // 设置状态栏颜色，这里以蓝色为例，可根据需求更换颜色值
            window.setStatusBarColor(getResources().getColor(R.color.white, null));
        }
        initViews();
    }

    protected abstract void initViews();

    protected abstract int getLayoutId();


}