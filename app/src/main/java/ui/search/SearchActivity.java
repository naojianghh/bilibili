package ui.search;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.naojianghh.bilibili3.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import Data.Data;
import base.BaseActivity;

public class SearchActivity extends BaseActivity {
    private List<String> searchResultList;
    private List<Data> dataList;
    private SearchResultAdapter adapter;
    private SearchView searchView;
    private RecyclerView searchResultRecyclerView;
    private List<Data> relatedDataList = new ArrayList<>();

    @Override
    protected void initViews() {
        searchResultList = new ArrayList<>();
        searchView = findViewById(R.id.search_sv);

        initData();

        ImageView imageViewReturnButton = findViewById(R.id.search_return);
        imageViewReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        ImageView imageViewSearchEnter = findViewById(R.id.search_enter);
        imageViewSearchEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence query = searchView.getQuery();
                String result = query.toString();
                Intent intent = new Intent(SearchActivity.this,SearchVideoListActivity.class);
                intent.putExtra("relatedData",(Serializable) relatedDataList);
                intent.putExtra("keyWord",result);
                startActivity(intent);
            }
        });

        searchResultRecyclerView = findViewById(R.id.search_rv);


        adapter = new SearchResultAdapter(this,searchResultList);

        searchResultRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchResultRecyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new SearchResultAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String result) {
                searchView.setQuery(result,true);
                Intent intent = new Intent(SearchActivity.this,SearchVideoListActivity.class);
                intent.putExtra("relatedData",(Serializable) relatedDataList);
                intent.putExtra("keyWord",result);
                startActivity(intent);
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                adapter.setKeyword(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                adapter.setKeyword(newText);
                return true;
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    private void initData() {



        searchView.setQueryHint("搜索");
        searchView.setBackgroundResource(R.drawable.search);

        dataList = Data.getDataList();

        searchResultList.add(dataList.get(0).getDescription());
        searchResultList.add(dataList.get(1).getDescription());
        searchResultList.add(dataList.get(2).getDescription());
        searchResultList.add(dataList.get(3).getDescription());
        searchResultList.add(dataList.get(4).getDescription());
        searchResultList.add(dataList.get(5).getDescription());

    }

    private void performSearch(String query) {
        searchResultList.clear();
        relatedDataList.clear();
        for (Data data : dataList) {
            if (data.getDescription().contains(query)) {
                searchResultList.add(data.getDescription());
                relatedDataList.add(data);
            }
        }
        adapter.notifyDataSetChanged();
    }

}
