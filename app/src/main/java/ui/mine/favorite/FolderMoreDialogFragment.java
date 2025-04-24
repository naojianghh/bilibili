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

public class FolderMoreDialogFragment extends BottomSheetDialogFragment {

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
        View view = inflater.inflate(R.layout.bottom_sheet_layout_favorite_folder_more,container,false);
        Button buttonCencel = view.findViewById(R.id.folder_cancel);
        buttonCencel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button buttonDelete = view.findViewById(R.id.folder_delete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.onDeleteButtonClick();
                }
            }
        });

        return  view;
    }
}
