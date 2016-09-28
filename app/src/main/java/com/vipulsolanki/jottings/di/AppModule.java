package com.vipulsolanki.jottings.di;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vipulsolanki.jottings.App;
import com.vipulsolanki.jottings.api.JottingService;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class AppModule {


    // Right now, all dependencies are provided from this module.
    // It should be refactored to several modules to group them.
    @Provides
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create();
    }

    @Provides
    @Singleton
    public JottingService provideJottingService(Retrofit retrofit) {
        return retrofit.create(JottingService.class);
    }

    @Provides
    public OkHttpClient provideOkhttpClient() {
        return new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
    }

    /**
    /* Provides Retrofit with baseurl already configured.
     */
    @Provides
    public Retrofit provideRetrofit(OkHttpClient okHttpClient, Gson gson) {
        //TODO for real-app, change mock-server base url with actual one.
        return new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .baseUrl(App.getApp().getMockBaseURL())
                    .client(okHttpClient)
                    .build();
    }
}
