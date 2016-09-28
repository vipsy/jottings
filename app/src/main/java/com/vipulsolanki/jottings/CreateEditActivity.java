package com.vipulsolanki.jottings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.vipulsolanki.jottings.model.Jotting;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class CreateEditActivity extends BaseActivity {

    private static final String EXTRA_MODE = "EXTRA_MODE";
    private static final String EXTRA_JOTTING_ID = "EXTRA_JOTTING_ID";

    private Mode createEditMode;
    private long jottingId;
    private Jotting jotting;

    private Realm realm;

    @BindView(R.id.et_header) EditText etHeader;
    @BindView(R.id.et_body) EditText etBody;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.btn_save) Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        realm = Realm.getDefaultInstance();

        Intent intent = getIntent();

        createEditMode = Mode.valueOf(intent.getExtras().getString(EXTRA_MODE));
        if (createEditMode == Mode.EDIT) {
            jottingId = intent.getExtras().getLong(EXTRA_JOTTING_ID);
            jotting = Jotting.find(realm, jottingId);

            etBody.setText(jotting.getBody());
            etHeader.setText(jotting.getHeader());
        } else {
            btnSave.setText("CREATE");
        }
    }

    private enum Mode {
        CREATE, EDIT
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_save)
    protected void onSave(View view) {
        String header = etHeader.getText().toString();
        String body = etBody.getText().toString();

        if (TextUtils.isEmpty(header)) {
            Snackbar.make(view, "Header is empty", Snackbar.LENGTH_SHORT).show();
            return;
        } else if(TextUtils.isEmpty(body)) {
            Snackbar.make(view, "Body is empty", Snackbar.LENGTH_SHORT).show();
            return;
        }

        try {
            realm.beginTransaction();

            if (createEditMode == Mode.CREATE) {
                jotting = Jotting.create(realm);
            }
            jotting.setBody(body);
            jotting.setHeader(header);
        } catch (Exception e) {
            realm.cancelTransaction();
        }
        realm.commitTransaction();

        makeSnackbar(view, "Saved. Peace!!").show();
    }

    public static Intent getCreateIntent(Context context) {
        Intent intent = new Intent(context, CreateEditActivity.class);
        intent.putExtra(EXTRA_MODE, Mode.CREATE.name());

        return intent;
    }

    public static Intent getEditIntent(Context context, long jottingId) {
        Intent intent = new Intent(context, CreateEditActivity.class);
        intent.putExtra(EXTRA_MODE, Mode.EDIT.name());
        intent.putExtra(EXTRA_JOTTING_ID, jottingId);

        return intent;
    }
}
