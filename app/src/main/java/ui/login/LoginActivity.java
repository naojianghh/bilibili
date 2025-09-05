package ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.naojianghh.bilibili3.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ui.MainActivity;
import utils.SpUtils;
import utils.Url;

import android.util.Log;

public class LoginActivity extends AppCompatActivity {

    private final static int IS_AGREE = 1;
    private final static int IS_NOT_AGREE = 0;
    private int state;

    private static final String LOGIN_URL = "http://" + Url.host + "/m1/6993757-6712033-default/user/log/psw";
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        state = IS_NOT_AGREE;

        EditText user = findViewById(R.id.user);
        EditText password = findViewById(R.id.password);
        ImageView loginImage = findViewById(R.id.login_background);
        Button login = findViewById(R.id.login);
        Button agree = findViewById(R.id.isAgree);
        Button returnButton = findViewById(R.id.login_return);

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = (state == IS_NOT_AGREE) ? IS_AGREE : IS_NOT_AGREE;
                ChangeBackground(loginImage);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = user.getText().toString().trim();
                String pwd = password.getText().toString().trim();


                if (username.isEmpty() || pwd.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (state == IS_NOT_AGREE) {
                    Toast.makeText(LoginActivity.this, "请先同意协议", Toast.LENGTH_SHORT).show();
                    return;
                }

                loginRequest(username, pwd);
            }
        });



    }

    private void loginRequest(String username, String password) {
        // 请求体（JSON 格式，和 Postman 里一致）
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType,
                "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"
        );

        // 构建请求
        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(body)
                .build();

        // 异步请求
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                // 请求失败，切回主线程提示
                runOnUiThread(() ->
                        Toast.makeText(LoginActivity.this, "网络请求失败：" + e.getMessage(), Toast.LENGTH_LONG).show()
                );
                Log.d("zxy",e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() ->
                            Toast.makeText(LoginActivity.this, "请求失败，状态码：" + response.code(), Toast.LENGTH_LONG).show()
                    );
                    return;
                }

                // 解析响应 JSON
                String responseData = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(responseData);
                    int code = jsonObject.optInt("code");
                    String msg = jsonObject.optString("msg");

                    if (code == 200) {
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                            // 保存Token到本地
                            String token = jsonObject.optString("data");
                            SpUtils.saveToken(LoginActivity.this, token);

                            // 跳转到MainActivity（无需再通过Intent传递token）
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // 关闭登录页，避免返回后重新显示
                        });
                    } else {
                        // 登录失败逻辑
                        runOnUiThread(() ->
                                Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_LONG).show()
                        );
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() ->
                            Toast.makeText(LoginActivity.this, "响应解析失败：" + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
                }
            }
        });
    }

    private void ChangeBackground(ImageView loginImage){
        if (state == IS_AGREE){
            loginImage.setImageResource(R.drawable.login_yes);
        } else {
            loginImage.setImageResource(R.drawable.login_no);
        }
    }

}