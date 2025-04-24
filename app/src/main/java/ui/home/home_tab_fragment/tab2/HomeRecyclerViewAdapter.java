package ui.home.home_tab_fragment.tab2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.naojianghh.bilibili3.R;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import Data.Data;
import ui.detailed_video.VideoActivity;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.CommonViewHolder> {

    private List<Data> dataList;
    private static final int VIEW_TYPE_BANNER = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private Context context;

    public HomeRecyclerViewAdapter(List<Data> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public CommonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CommonViewHolder result;
        if (viewType == VIEW_TYPE_NORMAL) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recyclerview_item, parent, false);
            result = new NormalViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recyclerview_banner,parent,false);
            result = new BannerViewHolder(view);
        }
        return result;
    }

    @Override
    public void onBindViewHolder(@NonNull CommonViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof BannerViewHolder){
            BannerViewHolder bannerViewHolder = (BannerViewHolder) holder;
            List<Integer> imageList = new ArrayList<>();
            imageList.add(R.drawable.data_banner1);
            imageList.add(R.drawable.data_banner2);
            imageList.add(R.drawable.data_banner3);
            HomePagerBannerAdapter adapter = new HomePagerBannerAdapter(holder.itemView.getContext(), imageList);
            bannerViewHolder.viewPager.setAdapter(adapter);
            bannerViewHolder.viewPager.setCurrentItem(Integer.MAX_VALUE / 2);
            final android.os.Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    int currentItem = bannerViewHolder.viewPager.getCurrentItem();
                    bannerViewHolder.viewPager.setCurrentItem(currentItem + 1);
                    handler.postDelayed(this,3000);
                }
            };
            handler.postDelayed(runnable,3000);
            bannerViewHolder.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == ViewPager.SCROLL_STATE_IDLE){
                        handler.removeCallbacks(runnable);
                        handler.postDelayed(runnable,3000);
                    }
                    else {
                        handler.removeCallbacks(runnable);
                    }
                }
            });
        }
        else if (holder instanceof NormalViewHolder){
            NormalViewHolder normalViewHolder = (NormalViewHolder) holder;
            normalViewHolder.imageView.setImageResource(dataList.get(position - 1).getImageResourceId());
            normalViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,VideoActivity.class);
                    intent.putExtra("videoId",position - 1);
                    context.startActivity(intent);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return dataList.size() + 1;
    }

    public class NormalViewHolder extends CommonViewHolder {
        TextView textView;
        ImageView imageView;

        public NormalViewHolder(@NonNull View view) {
            super(view);
            imageView = view.findViewById(R.id.home_item_image);
        }
    }

    public class BannerViewHolder extends CommonViewHolder{
        ViewPager viewPager;
        public BannerViewHolder(@NonNull View view) {
            super(view);
            viewPager = view.findViewById(R.id.vp_banner);
        }
    }

    public class CommonViewHolder extends RecyclerView.ViewHolder {
        public CommonViewHolder(View view) {
            super(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return VIEW_TYPE_BANNER;
        }
        return VIEW_TYPE_NORMAL;
    }
}
