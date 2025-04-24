package ui.mine.favorite;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.naojianghh.bilibili3.R;

import Data.FavoriteDatabaseHelper;
import base.BaseActivity;
import ui.detailed_video.FavoriteDialogFragment;

public class CreateFolderActivity extends BaseActivity {
    private Button buttonReturn;
    private Button buttonFinish;
    private EditText editText;
    private FavoriteDatabaseHelper databaseHelper;

    @Override
    protected void initViews() {
        databaseHelper = new FavoriteDatabaseHelper(this);
        buttonReturn = findViewById(R.id.create_folder_return);
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editText = findViewById(R.id.create_folder_new_folder_name);
        buttonFinish = findViewById(R.id.create_folder_finish);
        buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newFolderName = editText.getText().toString().trim();
                if(!newFolderName.isEmpty()){
                    databaseHelper.addFolder(newFolderName);
                    Intent intent = new Intent("MY_BROADCAST_ACTION");
                    intent.putExtra("folderName",newFolderName);
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_createfolder;
    }
}
