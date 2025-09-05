package ui.home.home_tab_fragment.tab2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.naojianghh.bilibili3.R;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Data.Data;
import logic.network.VideoData;
import ui.detailed_video.VideoActivity;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.CommonViewHolder> {

    private List<Data> dataList;
    public static final int VIEW_TYPE_BANNER = 0;
    public static final int VIEW_TYPE_NORMAL = 1;
    public static final int VIEW_TYPE_VIDEO = 2;
    public static final int VIEW_TYPE_LOADING = 3;
    private Context context;
    private List<VideoData> videoList;
    private boolean isLoading;

    public HomeRecyclerViewAdapter(List<Data> dataList, List<VideoData> videoList,Context context) {
        this.dataList = dataList;
        this.context = context;
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public CommonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CommonViewHolder result;
        if (viewType == VIEW_TYPE_NORMAL) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recyclerview_item, parent, false);
            result = new NormalViewHolder(view);
        }
        else if (viewType == VIEW_TYPE_BANNER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recyclerview_banner,parent,false);
            result = new BannerViewHolder(view);
        }
        else if (viewType == VIEW_TYPE_VIDEO){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recyclerview_video,parent,false);
            result = new VideoViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recyclerview_loading,parent,false);
            result = new LoadingViewHolder(view);
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
        else if (holder instanceof VideoViewHolder) {
            int videoIndex = position - 1 - dataList.size();
            VideoData videoData = videoList.get(videoIndex);

            VideoViewHolder videoViewHolder = (VideoViewHolder) holder;

            videoViewHolder.title.setText(videoData.getTitle());
            videoViewHolder.upName.setText(videoData.getUpData().getName());
            videoViewHolder.likes.setText(
                    videoData.getIsLikeCount() > 10000
                            ? String.format("%.1f万", videoData.getIsLikeCount() / 10000.0)
                            : String.valueOf(videoData.getIsLikeCount())
            );
            videoViewHolder.collects.setText( videoData.getIsCollectCount() > 10000
                    ? String.format("%.1f万", videoData.getIsCollectCount() / 10000.0)
                    : String.valueOf(videoData.getIsCollectCount()));



            String originalUrl = videoData.getThumbPhoto();

            String uniqueUrl = originalUrl.replace("https://picsum.photos/", "https://picsum.photos/id/" + ((videoIndex * 7 + 1)%1000) + "/");
            Log.d("zxyvideo", uniqueUrl);



            Glide.with(holder.itemView.getContext())
                    .load(uniqueUrl)
                    .placeholder(R.drawable.video_loading)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(videoViewHolder.thumbnail);


//            String avatarOriginal = videoData.getUpData().getAvator(); // 基础地址：https://picsum.photos/70
//            String avatarUniqueUrl = avatarOriginal.replace("https://picsum.photos/", "https://picsum.photos/id/" + ((videoIndex * 11 + 3)%1000) + "/");
//            Glide.with(holder.itemView.getContext())
//                    .load(avatarUniqueUrl)
//                    .circleCrop()
//                    .into(videoViewHolder.avatar);
            int minutes = new Random().nextInt(60);
            int seconds = new Random().nextInt(60);

            String duration = String.format("%d:%02d", minutes, seconds);
            videoViewHolder.duration.setText(duration);
            videoViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,VideoActivity.class);
                    intent.putExtra("video_data", videoData);
                    context.startActivity(intent);
                }
            });

        }
    }


    @Override
    public int getItemCount() {
        return 1 + dataList.size() + videoList.size() + (isLoading? 1 : 0);
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

    public class VideoViewHolder extends CommonViewHolder{
        TextView title, upName, likes, collects , duration;
        ImageView thumbnail, avatar;
        public VideoViewHolder(@NonNull View view) {
            super(view);
            title = itemView.findViewById(R.id.title);
            upName = itemView.findViewById(R.id.up_name);
            likes = itemView.findViewById(R.id.likes);
            collects = itemView.findViewById(R.id.collects);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            avatar = itemView.findViewById(R.id.avatar);
            duration = itemView.findViewById(R.id.duration);
        }
    }

    public class LoadingViewHolder extends CommonViewHolder{

        public LoadingViewHolder(@NonNull View view) {
            super(view);
        }
    }

    public class CommonViewHolder extends RecyclerView.ViewHolder {
        public CommonViewHolder(View view) {
            super(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_BANNER;
        }
        else if (position <= dataList.size()) {
            return VIEW_TYPE_NORMAL;
        }
        else if (position <= dataList.size() + videoList.size()) {
            return VIEW_TYPE_VIDEO;
        }
        else {
            return VIEW_TYPE_LOADING;
        }
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        notifyDataSetChanged();
    }
}
