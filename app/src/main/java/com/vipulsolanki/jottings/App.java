package com.vipulsolanki.jottings;

import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.vipulsolanki.jottings.di.AppComponent;
import com.vipulsolanki.jottings.di.AppModule;
import com.vipulsolanki.jottings.di.DaggerAppComponent;
import com.vipulsolanki.jottings.mock.MockServer;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends Application {

    private static Context sContext;

    private String mockBaseURL;
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule())
                .build();


        bindService(new Intent(this, MockServer.class),
                mMockServerConnection,
                Service.BIND_AUTO_CREATE);

        // Set Default Realm Configuration
        // The Realm file will be located in Context.getFilesDir() with name "default.realm"
        RealmConfiguration config = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(config);
    }

    public static Context getContext() {
        return sContext;
    }

    public static App getApp() {
        return (App) sContext;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public String getMockBaseURL() {
        return mockBaseURL;
    }


    private MockServer.Binder mMockService;
    
    // Service connection to connect to {@link}MockServer running the mock web-server
    private ServiceConnection mMockServerConnection = new ServiceConnection() {
        public static final String TAG = "Mock_ServiceConnection";

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mMockService = (MockServer.Binder) iBinder;
            updateShellUri();
        }

        private void updateShellUri() {
            if (mMockService != null) {
                mockBaseURL = "http://" + mMockService.getLocalAddress() + "/api/v1/";
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "Mock Service disconnected");
        }
    };

}
