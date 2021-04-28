package com.waro.hero.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.waro.hero.R;
import com.waro.hero.activities.HomeActivity;
import com.waro.hero.activities.LoginActivity;
import com.waro.hero.adapters.OrderHistoryListAdapter;
import com.waro.hero.databinding.FragmentHistoryBinding;
import com.waro.hero.helper.UserSessionManager;
import com.waro.hero.helper.Util;
import com.waro.hero.model.OrderHistoryResponse;
import com.waro.hero.viewmodels.OrderHistoryViewModel;

import java.util.ArrayList;
import java.util.HashMap;


public class HistoryFragment extends Fragment {

    OrderHistoryViewModel orderHistoryViewModel;
    FragmentHistoryBinding binding;
    OrderHistoryListAdapter adapter;
    String customerId;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        orderHistoryViewModel = new ViewModelProvider(this).get(OrderHistoryViewModel.class);
        binding = FragmentHistoryBinding.inflate(inflater, container, false);

        binding.actionLayout.textLocation.setText("Order History");
        binding.actionLayout.textLocation.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        binding.actionLayout.cartMenu.setOnClickListener(v -> {

            UserSessionManager userSessionManager = new UserSessionManager(getContext());
            userSessionManager.clearSession();

            Intent intent=new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        orderHistoryViewModel.initOrderHistory(customerId, requireActivity());

        // Alert toast msg
        orderHistoryViewModel.getToastObserver().observe(getViewLifecycleOwner(), message -> {

            if (message.equalsIgnoreCase(getResources().getString(R.string.no_connection))) {
                binding.noInternet.noInternet.setVisibility(View.VISIBLE);
            } else {
                Util.snackBar(requireView().getRootView(), message, Color.RED);
            }


        });


        // progress bar
        orderHistoryViewModel.getProgressbarObservable().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.noInternet.noInternet.setVisibility(View.GONE);

            } else {
                binding.progressBar.setVisibility(View.GONE);
                //binding.noInternet.noInternet.setVisibility(View.GONE);

            }
        });

        // get home data
        orderHistoryViewModel.getRepository().observe(getViewLifecycleOwner(), homeResponse -> {

            if (homeResponse.getStatus().equalsIgnoreCase("true")) {
                ArrayList<OrderHistoryResponse.OrdersBean> catDetailsBeanList = homeResponse.getOrders();
                adapter = new OrderHistoryListAdapter(catDetailsBeanList, getActivity());
                binding.recyclerHistory.setAdapter(adapter);
                binding.progressBar.setVisibility(View.GONE);
                binding.noInternet.noInternet.setVisibility(View.GONE);
                binding.errorLayout.txtError.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            } else {
                binding.errorLayout.txtError.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
                binding.recyclerHistory.setVisibility(View.GONE);
            }


        });


        return binding.getRoot();
    }
}