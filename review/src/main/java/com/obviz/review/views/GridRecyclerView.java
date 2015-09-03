package com.obviz.review.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import com.obviz.reviews.R;

/**
 * Created by gaylor on 09/01/2015.
 * Overlay to avoid the bug of the RecyclerView initialization with xml attributes
 */
public class GridRecyclerView extends RecyclerView {

    private int mNumColumns;
    private float mColumnWidth;
    private float mMargin;
    private StaggeredGridLayoutManager mManager;

    public GridRecyclerView(Context context) {
        super(context);

        mNumColumns = 1;
        mColumnWidth = 0;
        mMargin = 10;

        initManager();

        addDecoration();
    }

    public GridRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        getAttributes(context, attrs);

        initManager();

        addDecoration();
    }

    public GridRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        getAttributes(context, attrs);

        initManager();

        addDecoration();
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);

        if (getAdapter().getItemViewType(0) > 0) {

            mManager.setSpanCount(1);
        } else if (mColumnWidth > 0) {

            int spanCount = Math.max(1, getMeasuredWidth() / (int) mColumnWidth);
            mManager.setSpanCount(spanCount);
        }
    }

    public <T extends Adapter & InfiniteScrollable> void setInfiniteAdapter(final T adapter) {

        super.setAdapter(adapter);

        setOnScrollListener(new OnScrollListener() {

            private int currentTotalItems = 0;
            private boolean mLoading = true;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItem = getLayoutManager().getItemCount();
                int visibleItemCount = getChildCount();
                int[] firstItem = ((StaggeredGridLayoutManager) getLayoutManager()).findFirstVisibleItemPositions(null);

                if (totalItem < currentTotalItems) {
                    currentTotalItems = totalItem;
                }

                if (mLoading && totalItem > currentTotalItems) {
                    mLoading = false;
                    currentTotalItems = totalItem;
                }

                if (!mLoading && totalItem - visibleItemCount - 5 <= firstItem[0]) {
                    mLoading = true;
                    adapter.onLoadMore();
                }
            }
        });
    }

    private void getAttributes(Context context, AttributeSet attrs) {
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.GridRecyclerView, 0, 0);
        try {

            // Take an angle between 0 and 360 degrees
            mNumColumns = array.getInt(R.styleable.GridRecyclerView_numColumns, 1);
            mColumnWidth = array.getDimension(R.styleable.GridRecyclerView_columnWidth, 0.0f);
            mMargin = array.getDimension(R.styleable.GridRecyclerView_childMargin, 10.0f);
        } finally {

            array.recycle();
        }
    }

    private void initManager() {
        mManager = new StaggeredGridLayoutManager(mNumColumns, VERTICAL);

        setLayoutManager(mManager);
    }

    private void addDecoration() {
        addItemDecoration(new ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {

                int margin = (int) mMargin;
                outRect.set(margin, margin, margin, margin);
            }
        });
    }
}
