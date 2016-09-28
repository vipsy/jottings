package com.vipulsolanki.jottings;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.vipulsolanki.jottings.api.JottingService;
import com.vipulsolanki.jottings.helper.DividerItemDecoration;
import com.vipulsolanki.jottings.helper.SimpleItemTouchCallback;
import com.vipulsolanki.jottings.model.Jotting;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.Lazy;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JottingsListActivity extends BaseActivity {

    private static final String TAG = "JottingsListActivity";
    private static final int CODE_LAUNCH_CREATE = 1000;

    @BindView(R.id.toolbar)             protected Toolbar toolbar;
    @BindView(R.id.fab)                 protected FloatingActionButton fab;
    @BindView(R.id.recycler_view)       protected RecyclerView recyclerView;
    @BindView(R.id.swipe_to_refresh)    protected SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.message)             protected TextView message;

    @Inject Lazy<JottingService> lazyJottingService;

    private JottingsListAdapter jottingsListAdapter;

    ItemTouchHelper itemTouchHelper;
    private Bitmap bitmapDone;
    private Bitmap bitmapUndo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jottings_list);
        ButterKnife.bind(this);
        App.getApp().getAppComponent().inject(this);

        setSupportActionBar(toolbar);

        jottingsListAdapter = new JottingsListAdapter(this, new JottingsListAdapter.Callback() {
            @Override
            public void onSwipeStateChanged(boolean isSwiping) {
                swipeRefreshLayout.setEnabled(!isSwiping);
            }
        });

        recyclerView.setAdapter(jottingsListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider_light)));

        bitmapDone = BitmapFactory.decodeResource(getResources(), R.drawable.ic_check_black_48dp);
        bitmapUndo = BitmapFactory.decodeResource(getResources(), R.drawable.ic_arrow_back_black_48dp);

        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.primary),
                PorterDuff.Mode.SRC_IN));

        ItemTouchHelper.Callback callback = new SimpleItemTouchCallback(jottingsListAdapter,
                bitmapDone,
                bitmapUndo,
                paint);

        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchJottingsAsync();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        if (jottingsListAdapter.getItemCount() > 0) {
            message.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        if (bitmapDone != null) {
            bitmapDone.recycle();
        }
        super.onDestroy();
    }

    private void fetchJottingsAsync() {
        JottingService jottingService = lazyJottingService.get();
        Call<List<Jotting>> callGetAll = jottingService.getAllJottings();
        callGetAll.enqueue(new Callback<List<Jotting>>() {
            @Override
            public void onResponse(Call<List<Jotting>> call, Response<List<Jotting>> response) {
                Log.d(TAG, "code= "+ response.code());

                final List<Jotting> jottingList = response.body();
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealmOrUpdate(jottingList);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        jottingsListAdapter.notifyDataSetChanged();
                        if (jottingList.size() > 0) {
                            message.setVisibility(View.GONE);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Jotting>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.fab)
    protected void onFabClicked(View view) {
        Intent launchIntent = CreateEditActivity.getCreateIntent(this);
        startActivityForResult(launchIntent, CODE_LAUNCH_CREATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case JottingsListAdapter.ViewHolder.CODE_LAUNCH_DETAIL :
                jottingsListAdapter.requery();
                break;

            default:
                break;
        }
    }
}
