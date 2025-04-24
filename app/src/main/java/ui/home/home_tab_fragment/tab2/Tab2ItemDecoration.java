package ui.home.home_tab_fragment.tab2;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Tab2ItemDecoration extends RecyclerView.ItemDecoration {
    private int spacing;

    public Tab2ItemDecoration(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        GridLayoutManager gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();
        int spanCount = gridLayoutManager.getSpanCount();

        if (position != 0) {
            if (position % spanCount != 0) {
                outRect.left = spacing;
            } else {
                outRect.left = spacing / 2;
            }
            if ((position + 1) % spanCount != 0) {
                outRect.right = spacing;
            } else {
                outRect.right = spacing / 2;
            }
        }
    }
}
