package com.ordermanagementapp.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ordermanagementapp.model.OrderModel;
import com.ordermanagementapp.interfaces.OrderUpdateInterface;
import com.ordermanagementapp.R;

import java.util.ArrayList;
import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.MyViewHolder>{

    private Context context;

    List<OrderModel> orderData;
    OrderUpdateInterface orderUpdateInterface;

    public OrderListAdapter(Context context, ArrayList<OrderModel> orderData, OrderUpdateInterface orderUpdateInterface) {
        this.context = context;
        this.orderData = orderData;
        this.orderUpdateInterface = orderUpdateInterface;
        this.setHasStableIds(true);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final List<OrderModel> orderDataList = orderData;

        if (orderDataList.get(position).getOrderNumber() != null && !orderDataList.get(position).getOrderNumber().isEmpty()
                && !orderDataList.get(position).getOrderNumber().equals("")){
            holder.tvOrderNo.setText(orderDataList.get(position).getOrderNumber());
        }

        if (orderDataList.get(position).getOrderDueDate() != null && !orderDataList.get(position).getOrderDueDate().isEmpty()
                && !orderDataList.get(position).getOrderDueDate().equals("")){
            holder.tvDueDate.setText(orderDataList.get(position).getOrderDueDate());
        }

        if (orderDataList.get(position).getCustomerName() != null && !orderDataList.get(position).getCustomerName().isEmpty()
                && !orderDataList.get(position).getCustomerName().equals("")){
            holder.tvName.setText(orderDataList.get(position).getCustomerName());
        }

        if (orderDataList.get(position).getCustomerPhone() != null && !orderDataList.get(position).getCustomerPhone().isEmpty()
                && !orderDataList.get(position).getCustomerPhone().equals("")){
            holder.tvPhone.setText(orderDataList.get(position).getCustomerPhone());
        }

        if (orderDataList.get(position).getCustomerAddress() != null && !orderDataList.get(position).getCustomerAddress().isEmpty()
                && !orderDataList.get(position).getCustomerAddress().equals("")){
            holder.tvAddress.setText(orderDataList.get(position).getCustomerAddress());
        }

        if (orderDataList.get(position).getLocation() != null && !orderDataList.get(position).getLocation().isEmpty()
                && !orderDataList.get(position).getLocation().equals("")){
            holder.tvLocation.setText(orderDataList.get(position).getLocation());
        }

        if (orderDataList.get(position).getOrderTotal() != null && !orderDataList.get(position).getOrderTotal().isEmpty()
                && !orderDataList.get(position).getOrderTotal().equals("")){
            holder.tvTotal.setText(orderDataList.get(position).getOrderTotal());
        }

        holder.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderUpdateInterface.edit(orderDataList.get(position).getOrderNumber(), orderDataList.get(position).getOrderDueDate(),
                        orderDataList.get(position).getCustomerName(), orderDataList.get(position).getCustomerPhone(),
                        orderDataList.get(position).getCustomerAddress(),
                        orderDataList.get(position).getOrderTotal(), orderDataList.get(position).getLocation());
            }
        });

        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderUpdateInterface.delete(orderDataList.get(position).getOrderNumber());
            }
        });
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvOrderNo, tvDueDate, tvName, tvPhone, tvAddress, tvLocation, tvTotal, tvEdit, tvDelete;

        MyViewHolder(View itemView) {
            super(itemView);

            tvOrderNo     = itemView.findViewById(R.id.tvOrderNo);
            tvDueDate     = itemView.findViewById(R.id.tvDueDate);
            tvName        = itemView.findViewById(R.id.tvName);
            tvPhone       = itemView.findViewById(R.id.tvPhone);
            tvAddress     = itemView.findViewById(R.id.tvAddress);
            tvLocation    = itemView.findViewById(R.id.tvLocation);
            tvTotal       = itemView.findViewById(R.id.tvTotal);
            tvEdit        = itemView.findViewById(R.id.tvEdit);
            tvDelete      = itemView.findViewById(R.id.tvDelete);

        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        //return store_datas.size();
        return orderData.size();
    }


    public void updateList(List<OrderModel> list) {
        orderData = list;
        notifyDataSetChanged();
    }
}
