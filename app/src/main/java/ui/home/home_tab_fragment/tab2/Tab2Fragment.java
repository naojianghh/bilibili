package ui.home.home_tab_fragment.tab2;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.naojianghh.bilibili3.R;

import java.util.ArrayList;
import java.util.List;
import Data.Data;
import base.BaseFragment;
import logic.network.ApiClient;
import logic.network.ApiService;
import logic.network.VideoData;
import logic.network.VideoResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ui.MainActivity;

public class Tab2Fragment extends BaseFragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private HomeRecyclerViewAdapter adapter;
    private List<VideoData> videoList = new ArrayList<>();

    private int currentPage = 0;
    private final int pageSize = 8;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private boolean loading = false;
    private boolean videoDataLoaded = false; // 标记视频数据是否已加载

    @Override
    protected void initViews() {
        recyclerView = contentView.findViewById(R.id.rv1);
        List<Data> dataList = Data.getDataList();
        swipeRefreshLayout = contentView.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(this::refreshData);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null) return;

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                // 只有在视频数据未加载时才允许加载更多
                if (!videoDataLoaded && !isLoading && !isLastPage &&
                        (visibleItemCount + firstVisibleItemPosition) >= totalItemCount &&
                        firstVisibleItemPosition >= 0) {
                    if (!loading) {
                        loadMoreData();
                    }
                }
            }
        });

        adapter = new HomeRecyclerViewAdapter(dataList,videoList,requireContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0){
                    return 2;
                }

                int itemType = adapter.getItemViewType(position);
                if (itemType == HomeRecyclerViewAdapter.VIEW_TYPE_LOADING) {
                    return 2;
                }

                return 1;
            }
        });
        int spacingInPixels = 14;
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new Tab2ItemDecoration(spacingInPixels));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });

        // 初始加载数据
        loadInitialData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.tab2_fragment;
    }

    private void loadInitialData() {
        currentPage = 0;
        videoDataLoaded = false;
        isLastPage = false;
        // 初始不加载视频数据，只显示本地数据
    }

    private void refreshData() {
        currentPage = 0;
        videoDataLoaded = false;
        isLastPage = false;
        videoList.clear();
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(requireContext(), "数据已刷新", Toast.LENGTH_SHORT).show();
    }

    private void loadMoreData() {
        // 如果视频数据已经加载过，不再加载
        if (videoDataLoaded) {
            return;
        }

        isLoading = true;
        adapter.setLoading(true);
        fetchData(0, pageSize, false); // page固定为0
    }

    private void fetchData(int page, int pageSize, boolean isRefresh) {
        // 如果视频数据已经加载过，不再请求
        if (videoDataLoaded && !isRefresh) {
            isLoading = false;
            adapter.setLoading(false);
            return;
        }

        // 显示加载中状态至少1.5秒
        adapter.setLoading(true);

        // 添加延迟，让加载中状态显示一段时间
        new Handler().postDelayed(() -> {
            ApiService apiService = ApiClient.getApiService();
            Call<VideoResponse> call = apiService.getVideoData(page, pageSize);

            call.enqueue(new Callback<VideoResponse>() {
                @Override
                public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                    swipeRefreshLayout.setRefreshing(false);

                    if (isLoading) {
                        isLoading = false;
                        adapter.setLoading(false);
                    }

                    if (response.isSuccessful() && response.body()!= null) {
                        VideoResponse videoResponse = response.body();
                        if (videoResponse.getCode() == 200) {
                            List<VideoData> newData = videoResponse.getData();

                            if (isRefresh) {
                                videoList.clear();
                            }
                            Log.d("zxy",newData.toString());
                            if (newData!= null &&!newData.isEmpty()) {
                                videoList.addAll(newData);
                                adapter.notifyDataSetChanged();
                                isLastPage = true; // 设置为最后一页，因为只加载一次
                                videoDataLoaded = true; // 标记视频数据已加载
                            } else {
                                isLastPage = true;
                                videoDataLoaded = true;
                                Toast.makeText(requireContext(), "没有更多数据了", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(requireContext(), videoResponse.getMsg(), Toast.LENGTH_SHORT).show();
                            Log.d("videoResponse",videoResponse.getMsg());
                            isLastPage = true;
                            videoDataLoaded = true;
                        }
                    } else {
                        Toast.makeText(requireContext(), "请求失败", Toast.LENGTH_SHORT).show();
                        isLastPage = true;
                        videoDataLoaded = true;
                    }

                    // 确保加载中状态被移除
                    adapter.setLoading(false);
                }

                @Override
                public void onFailure(Call<VideoResponse> call, Throwable t) {
                    swipeRefreshLayout.setRefreshing(false);
                    if (isLoading) {
                        isLoading = false;
                        adapter.setLoading(false);
                    }
                    Toast.makeText(requireContext(), "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("videoResponse",t.getMessage());
                    isLastPage = true;
                    videoDataLoaded = true;

                    // 确保加载中状态被移除
                    adapter.setLoading(false);
                }
            });
        }, 1500); // 1.5秒延迟
    }
}