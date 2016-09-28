package com.vipulsolanki.jottings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vipulsolanki.jottings.helper.ItemTouchHelperAdapter;
import com.vipulsolanki.jottings.model.Jotting;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;


public class JottingsListAdapter extends RecyclerView.Adapter<JottingsListAdapter.ViewHolder>
    implements ItemTouchHelperAdapter {

    private Realm realm;
    private RealmResults<Jotting> realmResults;
    private Activity activity;
    private Callback callback;

    public JottingsListAdapter(Activity context, Callback callback) {
        this.activity = context;
        this.callback = callback;

        realm = Realm.getDefaultInstance();
        queryData();
    }

    private void queryData() {
        String[] sortFields = {"isFinished", "dateCreated"};
        Sort[] sortOrders = {Sort.ASCENDING, Sort.DESCENDING};
        realmResults = realm.where(Jotting.class).findAll().sort(sortFields, sortOrders);
    }

    public void requery() {
        queryData();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_jotting, null);
        return new ViewHolder(itemView, activity);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.apply(realmResults.get(position));
    }

    @Override
    public int getItemCount() {
        if (realmResults == null) {
            return 0;
        }
        return realmResults.size();
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
        final boolean isFinished = direction == ItemTouchHelper.START ? false : true;
        final ViewHolder vh = (ViewHolder) viewHolder;

        Jotting jotting = vh.getTaggedJotting();

        int beforePosition = viewHolder.getAdapterPosition();

        realm.beginTransaction();
        jotting.setFinished(isFinished);
        realm.copyToRealmOrUpdate(jotting);
        realm.commitTransaction();

        queryData();

        int afterPosition = realmResults.indexOf(jotting);

        notifyItemRemoved(beforePosition);
        notifyItemInserted(afterPosition);
    }

    @Override
    public void onSelectedChanged(boolean isSwipe) {
        if (callback != null) {
            callback.onSwipeStateChanged(isSwipe);
        }
    }

    public interface Callback {
        void onSwipeStateChanged(boolean isSwiping);
    }

    static public class ViewHolder extends RecyclerView.ViewHolder {

        public static final int CODE_LAUNCH_DETAIL = 1001;

        private final Activity activity;

        @BindView(R.id.header) TextView header;
        @BindView(R.id.body) TextView body;
        @BindView(R.id.content_parent) ViewGroup contentParent;

        public ViewHolder(View itemView, Activity activity) {
            super(itemView);
            this.activity = activity;
            ButterKnife.bind(this, itemView);
        }

        public void apply(final Jotting jotting) {
            boolean isFinished = jotting.isFinished();
            setTagJotting(jotting);
            header.setText(jotting.getHeader());
            header.setEnabled(!isFinished);

            if (isFinished) {
                header.setPaintFlags(header.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                header.setPaintFlags(header.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }

            body.setText(jotting.getBody());
            body.setEnabled(!jotting.isFinished());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = JottingDetailActivity.getIntent(itemView.getContext(),
                            jotting.getId());
                    activity.startActivityForResult(intent, CODE_LAUNCH_DETAIL);
                }
            });
        }

        private void setTagJotting(@NonNull Jotting jotting) {
            itemView.setTag(R.integer.tag_key_jotting, jotting);
        }
        public Jotting getTaggedJotting() {
            if (itemView == null) return null;
            return (Jotting) itemView.getTag(R.integer.tag_key_jotting);
        }
    }
}
