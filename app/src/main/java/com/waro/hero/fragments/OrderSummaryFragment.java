package com.waro.hero.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.google.gson.JsonObject;
import com.waro.hero.R;
import com.waro.hero.adapters.OrderItemListAdapter;
import com.waro.hero.databinding.FragmentOrderSummaryBinding;
import com.waro.hero.model.OrderHistoryResponse;
import com.waro.hero.model.OrderStatusResponse;
import com.waro.hero.network.ApiInterface;
import com.waro.hero.network.NoConnectivityException;
import com.waro.hero.viewmodels.OrderHistoryViewModel;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.waro.hero.network.RetrofitService.IMAGE_HOME_URL;
import static com.waro.hero.network.RetrofitService.createService;


public class OrderSummaryFragment extends Fragment {

    private static final String TAG = "OrderSummaryFragment";
    OrderHistoryViewModel orderHistoryViewModel;
    FragmentOrderSummaryBinding binding;
    OrderItemListAdapter adapter;
    int position;
    List<OrderHistoryResponse.OrdersBean> ordersBeanList;
    List<OrderHistoryResponse.OrdersBean.OrderItemBean> orderItemBeanList;

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        orderHistoryViewModel = new ViewModelProvider(this).get(OrderHistoryViewModel.class);
        binding = FragmentOrderSummaryBinding.inflate(inflater, container, false);

        binding.actionLayout.txtActionBarTitle.setText("Order Summary");
        binding.actionLayout.badgeCart.setVisibility(View.GONE);
        binding.actionLayout.txtActionBarTitle.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());


        Bundle bundle = getArguments();
        if (bundle != null) {
            ordersBeanList = bundle.getParcelableArrayList("shopListArray");
            orderItemBeanList = bundle.getParcelableArrayList("orderListArray");
            position = bundle.getInt("position");
        }

       /* for (OrderHistoryResponse.OrdersBean ordersBean : ordersBeanList){
            orderItemBeanList = ordersBean.getOrderItem();
        }*/
        adapter = new OrderItemListAdapter(orderItemBeanList, position, getActivity());
        binding.recyclerHistory.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        binding.txtRestuarntName.setText(ordersBeanList.get(position).getShop().getShopname());
        binding.txtRestuarntLocation.setText(ordersBeanList.get(position).getShop().getAddress());
        Glide.with(requireActivity())
                .load(IMAGE_HOME_URL + ordersBeanList.get(position).getShop().getImage())
                .error(R.drawable.placeholder)
                .into(binding.imgShop);

        binding.txtOrderStatus.setText("This Order was " + ordersBeanList.get(position).getStatus());

        double itemTotal = Double.parseDouble(ordersBeanList.get(position).getTotalAmt()) - (Double.parseDouble(ordersBeanList.get(position).getDeliveryCharges()) - Double.parseDouble(ordersBeanList.get(position).getDiscountAmt()));
        binding.txtItemTotalPrice.setText("\u20b9" + String.format("%.2f", itemTotal));
        binding.txtDeliveryPrice.setText("\u20b9" + ordersBeanList.get(position).getDeliveryCharges());

        if (ordersBeanList.get(position).getDiscountAmt() != null) {
            binding.txtDiscountPrice.setText("-" + "\u20b9" + ordersBeanList.get(position).getDiscountAmt());
        } else {
            binding.txtDiscountPrice.setText("\u20b9" + "0.00");
        }

        if (!ordersBeanList.get(position).getCouponId().equalsIgnoreCase("0")) {
            binding.txtCouponCode.setText(ordersBeanList.get(position).getCoupon().getCouponCode());
        } else {
            binding.txtCouponCode.setText("No Coupon Applied");
        }

        binding.txtGrandTotalAmount.setText("\u20b9" + ordersBeanList.get(position).getTotalAmt());
        binding.txtOrderNumber.setText("" + ordersBeanList.get(position).getId());
        binding.txtOrderMode.setText("Cash on Delivery");

        Log.d(TAG, "onCreateView: " + ordersBeanList.get(position).getAddress());
        if (ordersBeanList.get(position).getAddress() != null) {
            binding.txtOrderPhone.setText("" + ordersBeanList.get(position).getAddress().getPhone());
            binding.txtOrderAddress.setText(ordersBeanList.get(position).getAddress().getAddr1() + "," +
                    ordersBeanList.get(position).getAddress().getAddr2() + "," +
                    ordersBeanList.get(position).getAddress().getLandmark() + "," +
                    ordersBeanList.get(position).getAddress().getPincode());
        } else {
            binding.txtOrderPhone.setText("----------------");
            binding.txtOrderAddress.setText("---------------");
        }


        if (ordersBeanList.get(position).getStatus().equalsIgnoreCase("delivered")
                || ordersBeanList.get(position).getStatus().equalsIgnoreCase("cancelled")){

            binding.btnStatus.setVisibility(View.GONE);

        }else {
            binding.btnStatus.setVisibility(View.VISIBLE);
        }


        binding.btnStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getSelectedItem().toString();
                if (!item.equalsIgnoreCase("Update Status ?")){
                    statusUpdate(item);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return binding.getRoot();
    }

    private void statusUpdate(String status) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("order_id", ordersBeanList.get(position).getId());
        jsonObject.addProperty("status", status);

        Log.d(TAG, "statusUpdate: "+jsonObject);
        ApiInterface newsApiInterface = createService(ApiInterface.class, requireActivity());
        newsApiInterface.getOrderUpdateStatus(jsonObject).enqueue(new Callback<OrderStatusResponse>() {
            @Override
            public void onResponse(@NotNull Call<OrderStatusResponse> call, @NotNull Response<OrderStatusResponse> response) {
                if (response.isSuccessful()) {

                    OrderStatusResponse orderStatusResponse = response.body();
                    assert orderStatusResponse != null;
                    if (orderStatusResponse.getStatus().equalsIgnoreCase("true")) {

                        NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.navigation_history, true).build();
                        Navigation.findNavController(binding.getRoot()).navigate(R.id.navigation_history, null, navOptions);

                        Toast.makeText(requireActivity(), "Status was Updated Successfully...!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<OrderStatusResponse> call, @NotNull Throwable t) {
                if (t instanceof NoConnectivityException) {
                    // show No Connectivity message to user or do whatever you want.
                    //toastMessageObserver.setValue(t.getMessage());
                    // Whenever you want to show toast use setValue.

                }
                // homeData.setValue(null);
                //progressbarObservable.setValue(false);
            }
        });

    }

}