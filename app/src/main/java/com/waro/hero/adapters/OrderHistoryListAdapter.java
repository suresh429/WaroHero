package com.waro.hero.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.waro.hero.R;
import com.waro.hero.databinding.HistoryListItemBinding;
import com.waro.hero.model.OrderHistoryResponse;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static com.waro.hero.network.RetrofitService.IMAGE_HOME_URL;


public class OrderHistoryListAdapter extends RecyclerView.Adapter<OrderHistoryListAdapter.ViewHolder> {

    ArrayList<OrderHistoryResponse.OrdersBean> modelList;
    Context context;
    public OrderHistoryListAdapter(ArrayList<OrderHistoryResponse.OrdersBean> modelList, Context context) {
        this.modelList = modelList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderHistoryListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(HistoryListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrderHistoryListAdapter.ViewHolder holder, int position) {

        holder.rowItemBinding.txtRestuarntName.setText(modelList.get(position).getShop().getShopname());
        holder.rowItemBinding.txtRestuarntLocation.setText(modelList.get(position).getShop().getAddress());


        holder.rowItemBinding.txtAmount.setText("\u20b9"+modelList.get(position).getTotalAmt());
        holder.rowItemBinding.txtDate.setText(modelList.get(position).getDate_time());
        Glide.with(context)
                .load(IMAGE_HOME_URL+modelList.get(position).getShop().getImage())
                .error(R.drawable.placeholder)
                .into(holder.rowItemBinding.imgShop);

        holder.rowItemBinding.getRoot().setOnClickListener(v -> {


            Bundle bundle = new Bundle();

            bundle.putParcelableArrayList("shopListArray", modelList);
            bundle.putParcelableArrayList("orderListArray",  modelList.get(position).getOrderItem());
            bundle.putInt("position", position);

            Navigation.findNavController(v).navigate(R.id.orderSummaryFragment,bundle);


        });

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        HistoryListItemBinding rowItemBinding;

        public ViewHolder(@NonNull HistoryListItemBinding rowItemBinding) {
            super(rowItemBinding.getRoot());
            this.rowItemBinding = rowItemBinding;
        }
    }
}
