package ui.mine.favorite;


import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.naojianghh.bilibili3.R;


import java.util.ArrayList;
import java.util.List;

import Data.Data;
import Data.FavoriteDatabaseHelper;
import Data.FavoriteData;
import base.BaseActivity;

public class FolderActivity extends BaseActivity {

    private Button buttonReturn;
    private TextView textViewFolderName;
    private RecyclerView recyclerView;
    private FavoriteDatabaseHelper databaseHelper;
    private List<FavoriteData> favorites;
    private List<FavoriteData> currentFavorites;

    @Override
    protected void initViews() {
        databaseHelper = new FavoriteDatabaseHelper(this);
        favorites = databaseHelper.getALLFavorites();
        buttonReturn = findViewById(R.id.folder_return);
        textViewFolderName = findViewById(R.id.folder_name);
        recyclerView = findViewById(R.id.folder_rv);

        String folderName = getIntent().getStringExtra("folderName");
        if (folderName != null){
            currentFavorites = new ArrayList<FavoriteData>();
            for (FavoriteData favoriteData : favorites){
                if (favoriteData.getFolderName().equals(folderName)){
                    currentFavorites.add(favoriteData);
                }
            }
        }



        FavoritesAdapter adapter = new FavoritesAdapter(currentFavorites,FolderActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        textViewFolderName.setText(folderName);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_folder;
    }
}
