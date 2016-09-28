package com.vipulsolanki.jottings.api;


import com.vipulsolanki.jottings.model.Jotting;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface JottingService {
    @GET("jottings/list")
    Call<List<Jotting>> getAllJottings();

    @GET("jottings/{id}")
    Call<Jotting> getJotting(@Path("id") String id);
}
