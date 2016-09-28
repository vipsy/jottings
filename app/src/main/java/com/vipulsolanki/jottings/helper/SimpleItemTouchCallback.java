package com.vipulsolanki.jottings.helper;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.vipulsolanki.jottings.JottingsListAdapter;
import com.vipulsolanki.jottings.R;

public class SimpleItemTouchCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter adapter;
    private final Bitmap bitmapDone;
    private final Bitmap bitmapUndo;
    private final Paint paint;

    public SimpleItemTouchCallback(ItemTouchHelperAdapter adapter, Bitmap bitmapDone, Bitmap bitmapUndo, Paint paint) {
        this.adapter = adapter;
        this.bitmapDone = bitmapDone;
        this.bitmapUndo = bitmapUndo;
        this.paint = paint;

    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final int dragFlags = 0;
        JottingsListAdapter.ViewHolder vh = (JottingsListAdapter.ViewHolder) viewHolder;

        boolean isFinished = vh.getTaggedJotting().isFinished();
        final int swipeFlags = isFinished ?
                ItemTouchHelper.START :
                ItemTouchHelper.END;

        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Fade out the view when it is swiped out of parent's view bounds
            final float alpha = 1.0f - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);

            final float left;

            Bitmap iconBitmap;

            if (dX > 0) {
                left = viewHolder.itemView.getLeft() + viewHolder.itemView.getPaddingLeft()
                        + dX - bitmapDone.getWidth();
                iconBitmap = bitmapDone;
            } else {
                left = viewHolder.itemView.getRight() + viewHolder.itemView.getPaddingRight() + dX;
                iconBitmap = bitmapUndo;
            }

            final float top = viewHolder.itemView.getTop() + viewHolder.itemView.getPaddingTop()
                    + (viewHolder.itemView.getHeight() - bitmapDone.getHeight()) / 2;

            c.save();
            c.translate(left, top);
            c.drawBitmap(iconBitmap, 0, 0, paint);
            c.restore();

        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        adapter.onSelectedChanged(actionState == ItemTouchHelper.ACTION_STATE_SWIPE);

    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        viewHolder.itemView.setAlpha(1.0f);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.onSwiped(viewHolder, direction);
    }
}
