package com.vipulsolanki.jottings.helper;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDividerDrawable;
    private int mDividerSize;
    private static final int VERTICAL_SPACING = 32;

    public DividerItemDecoration(Drawable dividerDrawable) {
        mDividerDrawable = dividerDrawable;
        mDividerSize = dividerDrawable.getIntrinsicHeight();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (shouldDrawDividerForItemView(view, parent)) {
            outRect.set(0, 0, 0, mDividerSize + VERTICAL_SPACING);
            return;
        }

        super.getItemOffsets(outRect, view, parent, state);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDividerDrawable.getIntrinsicHeight();
            mDividerDrawable.setBounds(left, top, right, bottom);
            mDividerDrawable.draw(c);
        }

    }

    protected boolean isLastItemView(View view, RecyclerView recyclerView) {
        int position = recyclerView.getChildAdapterPosition(view);
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        return adapter != null && position + 1 >= adapter.getItemCount();
    }

    protected boolean shouldDrawDividerForItemView(View view, RecyclerView recyclerView) {
        return view.getVisibility() == View.VISIBLE && !isLastItemView(view, recyclerView);
    }
}
