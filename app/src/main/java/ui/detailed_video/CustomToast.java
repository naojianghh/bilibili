package ui.detailed_video;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.naojianghh.bilibili3.R;

public class CustomToast {
    public static void showCustomToast(Context context,String text) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customToastView = inflater.inflate(R.layout.toast_custom, null);

        TextView textView = customToastView.findViewById(R.id.real_toast_text);
        textView.setText(text);

        Toast customToast = new Toast(context);
        customToast.setView(customToastView);
        customToast.setDuration(Toast.LENGTH_SHORT);
        customToast.setGravity(Gravity.CENTER, 0, 0);

        customToast.show();
    }
}
