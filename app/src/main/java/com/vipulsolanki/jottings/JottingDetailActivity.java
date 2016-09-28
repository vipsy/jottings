package com.vipulsolanki.jottings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.vipulsolanki.jottings.model.Jotting;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.Component;
import io.realm.Realm;
import io.realm.RealmResults;

public class JottingDetailActivity extends BaseActivity {

    public static final String EXTRA_JOTTING_ID = "EXTRA_JOTTING_ID";

    @BindView(R.id.toolbar) protected Toolbar toolbar;
    @BindView(R.id.fab) protected FloatingActionButton fab;

    @BindView(R.id.header) protected TextView header;
    @BindView(R.id.body) protected TextView body;

    private long jottingId;
    private Realm realm;
    private Jotting jotting;

    //Activity launch codes
    private static final int CODE_LAUNCH_EDIT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jotting_detail);
        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();
        if (savedInstanceState != null) {
            jottingId = savedInstanceState.getLong(EXTRA_JOTTING_ID);
        } else {
            jottingId = getIntent().getExtras().getLong(EXTRA_JOTTING_ID);
        }

        jotting = Jotting.find(realm, jottingId);
        apply(jotting);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void apply(Jotting jotting) {
        header.setText(jotting.getHeader());
        body.setText(jotting.getBody());
    }

    @OnClick(R.id.btn_delete)
    protected void onDelete(View view) {
        makeSnackbar(view, "Deleted. Undo if  you want")
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        if (event != DISMISS_EVENT_ACTION) {
                            realm.beginTransaction();
                            jotting.deleteFromRealm();
                            realm.commitTransaction();
                            jotting = null;
                            finish();
                        } else {

                        }
                    }
                }).show();
    }

    @OnClick(R.id.fab)
    protected void onFabClicked(View view) {
        Intent launchIntent = CreateEditActivity.getEditIntent(this, jottingId);
        startActivityForResult(launchIntent, CODE_LAUNCH_EDIT);
    }

    public static Intent getIntent(Context context, long id) {
        Intent intent = new Intent(context, JottingDetailActivity.class);
        intent.putExtra(EXTRA_JOTTING_ID, id);
        return intent;
    }
}
