package com.example.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {
    String BASE_URL="https://run.mocky.io/v3/";
@GET("e2b4551a-a9be-4350-8538-fbe383fc20a4")
Call<Response>getBase();
}

