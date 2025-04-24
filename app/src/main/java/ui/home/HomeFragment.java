package ui.home;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.naojianghh.bilibili3.R;
import java.util.ArrayList;
import java.util.List;
import Data.Data;
import base.BaseFragment;
import Bean.Bean;
import ui.search.SearchActivity;


public class HomeFragment extends BaseFragment {

    private List<Data> dataList;
    private HomeFragmentAdapter homeFragmentAdapter;
    private List<Bean> beans = new ArrayList<>();

    @SuppressLint("ResourceAsColor")
    @Override
    protected void initViews() {
        Bean bean1 = new Bean("直播");
        beans.add(bean1);
        Bean bean2 = new Bean("推荐");
        beans.add(bean2);
        Bean bean3 = new Bean("热门");
        beans.add(bean3);
        Bean bean4 = new Bean("动画");
        beans.add(bean4);
        Bean bean5 = new Bean("影视");
        beans.add(bean5);
        Bean bean6 = new Bean("新征程");
        beans.add(bean6);
        TabLayout tabLayout = contentView.findViewById(R.id.tab_layout);
        TabLayout tabLayout_fixed = contentView.findViewById(R.id.tab_layout_fixed);

        tabLayout_fixed.addTab(tabLayout_fixed.newTab().setIcon(R.drawable.more));

        ViewPager viewPager = contentView.findViewById(R.id.vp);

        homeFragmentAdapter = new HomeFragmentAdapter(getChildFragmentManager());
        homeFragmentAdapter.setmBean(beans);
        viewPager.setAdapter(homeFragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        ImageView imageViewSearch = contentView.findViewById(R.id.top_search);
        imageViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }
}