package ui.mine.favorite;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.naojianghh.bilibili3.R;

public class VideoMoreDialogFragment extends BottomSheetDialogFragment {

    public interface OnDeleteButtonClickListener {
        void onDeleteButtonClick();
    }

    private OnDeleteButtonClickListener listener;

    public void setListener(OnDeleteButtonClickListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout_favorite_video_more,container,false);
        Button deleteButton = view.findViewById(R.id.favorites_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.onDeleteButtonClick();
                }
            }
        });

        Button cancelButton = view.findViewById(R.id.favorites_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }
}
