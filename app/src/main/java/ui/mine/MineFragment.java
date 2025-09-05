package ui.mine;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import base.BaseFragment;
import ui.mine.favorite.MyFavoritesActivity;

import com.naojianghh.bilibili3.R;

public class MineFragment extends BaseFragment {

    private Button myFavorite;

    @Override
    protected void initViews() {

        RecyclerView recyclerView = contentView.findViewById(R.id.mine_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        MineRecyclerViewAdapter mineRecyclerViewAdapter = new MineRecyclerViewAdapter(requireContext());
        recyclerView.setAdapter(mineRecyclerViewAdapter);


    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }
}
