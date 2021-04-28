package com.waro.hero.network;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.waro.hero.model.OrderHistoryResponse;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.waro.hero.network.RetrofitService.*;

public class Repository {
    private MutableLiveData<String> toastMessageObserver;
    private MutableLiveData<Boolean> progressbarObservable;
    private static Repository repository;

    public static Repository getInstance(Context context) {
        if (repository == null) {
            repository = new Repository(context);

        }
        return repository;
    }

   private ApiInterface newsApiInterface;

    public Repository(Context context) {

        newsApiInterface = createService(ApiInterface.class, context);
        progressbarObservable = new MutableLiveData<>();
        toastMessageObserver = new MutableLiveData<>();
    }

    public MutableLiveData<Boolean> getProgressbarObservable() {
        return progressbarObservable;
    }

    public MutableLiveData<String> getToastObserver() {
        return toastMessageObserver;
    }

    // getting customer data response
    public MutableLiveData<OrderHistoryResponse> getAllOrders() {
        progressbarObservable.setValue(true);
        MutableLiveData<OrderHistoryResponse> homeData = new MutableLiveData<>();
        newsApiInterface.getOrderHistoryList().enqueue(new Callback<OrderHistoryResponse>() {
            @Override
            public void onResponse(@NotNull Call<OrderHistoryResponse> call, @NotNull Response<OrderHistoryResponse> response) {
                if (response.isSuccessful()) {
                    progressbarObservable.setValue(false);
                    homeData.setValue(response.body());
                } else {
                    progressbarObservable.setValue(false);
                    toastMessageObserver.setValue("Something unexpected happened to our request: " + response.message()); // Whenever you want to show toast use setValue.

                }
            }

            @Override
            public void onFailure(@NotNull Call<OrderHistoryResponse> call, @NotNull Throwable t) {
                if (t instanceof NoConnectivityException) {
                    // show No Connectivity message to user or do whatever you want.
                    toastMessageObserver.setValue(t.getMessage());
                    // Whenever you want to show toast use setValue.

                }
                // homeData.setValue(null);
                progressbarObservable.setValue(false);
            }
        });
        return homeData;
    }



}
