package ui.mine;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import base.BaseFragment;
import ui.mine.favorite.MyFavoritesActivity;

import com.naojianghh.bilibili3.R;

public class MineFragment extends BaseFragment {

    private Button myFavorite;

    @Override
    protected void initViews() {
        myFavorite = contentView.findViewById(R.id.mine_favorite);
        myFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), MyFavoritesActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }
}
