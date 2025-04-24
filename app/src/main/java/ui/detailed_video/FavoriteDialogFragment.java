package ui.detailed_video;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.naojianghh.bilibili3.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Data.Data;
import Data.FavoriteData;
import Data.FavoriteDatabaseHelper;
import ui.mine.favorite.CreateFolderActivity;

public class FavoriteDialogFragment extends BottomSheetDialogFragment  {
    private EditText folderNameEditText;
    private Button createFolderButton;
    private RecyclerView folderListView;
    private Button saveFavoriteButton;
    private FavoriteDatabaseHelper databaseHelper;
    private List<String> folderNames = new ArrayList<>();
    private String selectedFolderName;
    private FavoriteData favorite;
    private Map<String, Integer> folderDataCountMap = new HashMap<>();
    private static final int NEW_FOLDER_NAME = 1;

    public FavoriteDialogFragment(Data data){
        favorite = new FavoriteData(data.getDescription(),data.getImageResourceId(),data.getVideoImageResourceId(),data.getVideoListImageResourceId());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState  ) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout_folder_add,container,false);
        createFolderButton = view.findViewById(R.id.create_folder_button);
        folderListView = view.findViewById(R.id.folder_list_view);
        saveFavoriteButton = view.findViewById(R.id.save_favorite_button);
        databaseHelper = new FavoriteDatabaseHelper(requireContext());

        folderNames = databaseHelper.getAllFolderNames();
        List<FavoriteData> favorites = databaseHelper.getALLFavorites();
        for (FavoriteData favorite : favorites) {
            String folderName = favorite.getFolderName();
            if(!folderNames.contains(folderName)) {
                folderNames.add(folderName);
            }
            folderDataCountMap.put(folderName, folderDataCountMap.getOrDefault(folderName, 0) + 1);
        }

        createFolderButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(),CreateFolderActivity.class);
            startActivityForResult(intent,NEW_FOLDER_NAME);
        });

        FoldersAdapter adapter = new FoldersAdapter(folderDataCountMap,folderNames);
        adapter.setFolderListView(folderListView);
        adapter.setListener(new FoldersAdapter.OnSelectClickListener() {
            @Override
            public void onSelectClick(String folderName) {
                selectedFolderName = folderName;
            }
        });
        folderListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        folderListView.setAdapter(adapter);


        saveFavoriteButton.setOnClickListener(v -> {
            if (selectedFolderName != null) {
                favorite.setFolderName(selectedFolderName);
                databaseHelper.addFavorite(favorite);

                Bundle resultBundle = new Bundle();
                resultBundle.putString("selected_folder_name",selectedFolderName);
                getParentFragmentManager().setFragmentResult("request_key",resultBundle);

                dismiss();
            }  else {
                CustomToast.showCustomToast(requireContext(),"已取消收藏");
                dismiss();
            }
        });

        View dragHandle = view.findViewById(R.id.drag_handle);
        dragHandle.setOnTouchListener(new SwipeDismissTouchListener(dragHandle,null, new SwipeDismissTouchListener.OnDismissCallback() {
            @Override
            public void onDismiss(View view) {
                dismiss();
            }
        }));

        return view;
    }

    private static class SwipeDismissTouchListener implements View.OnTouchListener {
        private static final int MIN_SWIPE_DISTANCE = 120;
        private static final int MAX_SWIPE_VELOCITY = 200;
        private final View view;
        private final OnDismissCallback callback;
        private float downY;

        public SwipeDismissTouchListener(View view, View clickableView ,OnDismissCallback callback) {
            this.view = view;
            this.callback = callback;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downY = event.getY();
                    return false;
                case MotionEvent.ACTION_UP:
                    float upY = event.getY();
                    float deltaY = downY - upY;
                    if (Math.abs(deltaY) > MIN_SWIPE_DISTANCE && deltaY > 0) {
                        callback.onDismiss(view);
                        return true;
                    }
                    return false;
            }
            return false;
        }
        public interface OnDismissCallback {
            void onDismiss(View view);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_FOLDER_NAME && resultCode == RESULT_OK && data != null){
            String newFolderName = data.getStringExtra("folderName");
            folderNames.add(newFolderName);
            folderDataCountMap.put(newFolderName,0);
            FoldersAdapter adapter = (FoldersAdapter) folderListView.getAdapter();
            if (adapter != null){
                adapter.notifyDataSetChanged();
            }
        }
    }
}
