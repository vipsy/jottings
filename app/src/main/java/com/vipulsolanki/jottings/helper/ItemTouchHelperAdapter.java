package com.vipulsolanki.jottings.helper;

import android.support.v7.widget.RecyclerView;

public interface ItemTouchHelperAdapter {

    void onSwiped(RecyclerView.ViewHolder viewHolder, int direction);
    void onSelectedChanged(boolean isSwipe);
}
