package utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtils {
    private static final String SP_NAME = "login_state";
    private static final String KEY_TOKEN = "token";

    // 保存Token
    public static void saveToken(Context context, String token) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_TOKEN, token).apply();
    }

    // 获取Token
    public static String getToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(KEY_TOKEN, null);
    }

    // 清除Token（退出登录）
    public static void clearToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().remove(KEY_TOKEN).apply();
    }
}