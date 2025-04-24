package ui.home.home_tab_fragment.tab2;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.naojianghh.bilibili3.R;
import java.util.List;
import Data.Data;
import base.BaseFragment;

public class Tab2Fragment extends BaseFragment {

    @Override
    protected void initViews() {
        RecyclerView recyclerView = contentView.findViewById(R.id.rv1);
        List<Data> dataList = Data.getDataList();


        HomeRecyclerViewAdapter adapter = new HomeRecyclerViewAdapter(dataList,requireContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0){
                    return 2;
                }
                else return 1;
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


    }

    @Override
    protected int getLayoutId() {
        return R.layout.tab2_fragment;
    }
}
