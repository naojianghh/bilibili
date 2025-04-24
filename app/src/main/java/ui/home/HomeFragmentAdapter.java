package ui.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

import Bean.Bean;
import ui.home.home_tab_fragment.Tab1Fragment;
import ui.home.home_tab_fragment.tab2.Tab2Fragment;
import ui.home.home_tab_fragment.Tab3Fragment;
import ui.home.home_tab_fragment.Tab4Fragment;
import ui.home.home_tab_fragment.Tab5Fragment;
import ui.home.home_tab_fragment.Tab6Fragment;

public class HomeFragmentAdapter extends FragmentPagerAdapter {

    private List<Bean> mBean;

    public HomeFragmentAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public void setmBean(List<Bean> bean){
        mBean = bean;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new Tab1Fragment();
        } else if (position == 1) {
            return new Tab2Fragment();
        } else if (position == 2) {
            return new Tab3Fragment();
        } else if (position == 3) {
            return new Tab4Fragment();
        } else if (position == 4) {
            return new Tab5Fragment();
        } else if (position == 5) {
            return new Tab6Fragment();
        }
        return new Tab2Fragment();
    }

    @Override
    public int getCount() {
        return mBean == null ? 0 : mBean.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mBean.get(position).getName();
    }
}
