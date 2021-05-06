package com.waro.hero.network;

import com.waro.hero.model.OrderHistoryResponse;
import com.google.gson.JsonObject;
import com.waro.hero.model.OrderStatusResponse;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {


    @Headers("Content-Type: application/json")
    @POST("all-orders")
    Call<OrderHistoryResponse> getOrderHistoryList();

    @Headers("Content-Type: application/json")
    @POST("update-order-status")
    Call<OrderStatusResponse> getOrderUpdateStatus(@Body JsonObject jsonObject);


}
