package com.vipulsolanki.jottings.di;

import android.app.Activity;

import com.vipulsolanki.jottings.JottingDetailActivity;
import com.vipulsolanki.jottings.JottingsListActivity;
import com.vipulsolanki.jottings.api.JottingService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component( modules = {AppModule.class})
public interface AppComponent {

    JottingService provideJottingService();

    void inject(JottingsListActivity activity);
    void inject(JottingDetailActivity activity);
}
