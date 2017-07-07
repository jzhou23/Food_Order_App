package com.example.jhzhou.foodordersaler.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jhzhou.foodordersaler.R;
import com.example.jhzhou.foodordersaler.entity.Order;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jhzhou on 6/5/17.
 */

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.ViewHolder>{

    private List<Order> mOrderList;
    private Context mContext;
    private List<CheckBox> mCheckBoxList;

    public DetailsAdapter(Context context, List<Order> orderList) {
        mContext = context;
        mOrderList = orderList;
        mCheckBoxList = new ArrayList<>();
    }

    public boolean checkBoxList() {
        for (int i = 0; i < mCheckBoxList.size(); i++) {
            if (!mCheckBoxList.get(i).isChecked()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Order order = mOrderList.get(position);
        holder.checkBox.setText(order.getFood().getName());
        holder.nums.setText(String.valueOf(order.getNums()));

        List<String> orderList = order.getAdditionList();
        if (orderList != null) {
            for (int i = 0; i < orderList.size(); i++) {
                holder.addtions.append(orderList.get(i));
                if (i != orderList.size() - 1) {
                    holder.addtions.append("\n");
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mOrderList == null ? 0 : mOrderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.checkbox_details) CheckBox checkBox;
        @BindView(R.id.numbers_details) TextView nums;
        @BindView(R.id.additional_info_details) TextView addtions;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mCheckBoxList.add(checkBox);
        }
    }
}
