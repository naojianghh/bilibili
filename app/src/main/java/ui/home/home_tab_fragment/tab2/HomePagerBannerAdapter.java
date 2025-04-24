package ui.home.home_tab_fragment.tab2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.naojianghh.bilibili3.R;

import java.util.List;

public class HomePagerBannerAdapter extends PagerAdapter {
    private Context context;
    private List<Integer> imageList;

    public HomePagerBannerAdapter(Context context, List<Integer> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_banner,container,false);
        ImageView imageView = view.findViewById(R.id.item_banner_iv);
        int realPosition = position % imageList.size();
        imageView.setImageResource(imageList.get(realPosition));
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
