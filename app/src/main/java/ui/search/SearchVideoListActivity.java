package ui.search;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.naojianghh.bilibili3.R;

import java.util.ArrayList;
import java.util.List;

import Data.Data;
import base.BaseActivity;

public class SearchVideoListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private SearchVideoAdapter adapter;

    @Override
    protected void initViews() {
        recyclerView = findViewById(R.id.searchVideo_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));
        List<Data> relatedDataList = (List<Data>) getIntent().getSerializableExtra("relatedData");
        if (relatedDataList != null) {
            adapter = new SearchVideoAdapter(this, relatedDataList);
            recyclerView.setAdapter(adapter);
        }

        TextView textView = findViewById(R.id.searchVideo_tv);
        String keyWord = getIntent().getStringExtra("keyWord");
        if (keyWord != null){
            textView.setText(keyWord);
        }

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ImageView videoListReturn = findViewById(R.id.searchVideo_return);
        videoListReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_searchvideolist;
    }
}
