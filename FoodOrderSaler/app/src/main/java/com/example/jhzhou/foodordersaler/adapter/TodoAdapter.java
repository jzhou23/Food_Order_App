package com.example.jhzhou.foodordersaler.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jhzhou.foodordersaler.R;
import com.example.jhzhou.foodordersaler.entity.Bag;
import com.example.jhzhou.foodordersaler.entity.Customer;
import com.example.jhzhou.foodordersaler.entity.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jhzhou on 6/5/17.
 */

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder>{


    private static final String LOG_TAG = "TodoAdapter";

    public interface OnClickListener {
        void onClick(Bag bag, Customer customer, int position);
    }


    private Context mContext;
    private List<Bag> mBagList;
    private List<String> mKeyList;
    private List<Customer> mCustomerList;
    private OnClickListener mOnClickListener;

    public TodoAdapter(Context context, OnClickListener onClickListener) {
        mContext = context;
        mBagList = new ArrayList<>();
        mKeyList = new ArrayList<>();
        mCustomerList = new ArrayList<>();
        mOnClickListener = onClickListener;
    }

    public void addKey(String key) {
        mKeyList.add(key);
    }

    public String getKey(int position) {
        return mKeyList.get(position);
    }

    public void updateData(Bag bag, Customer customer, String key) {
        mBagList.add(bag);
        mCustomerList.add(customer);
        mKeyList.add(key);
        notifyDataSetChanged();
    }

    public void removePosition(int position) {
        mKeyList.remove(position);
        mBagList.remove(position);
        mCustomerList.remove(position);
        notifyDataSetChanged();
    }

    public Bag getBag(int position) {
        return mBagList.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_todo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Order order = mBagList.get(position).getOrderList().get(0);
        Glide.with(mContext).load(Uri.parse(order.getFood().getImage())).into(holder.foodImage);

        Customer customer = mCustomerList.get(position);
        holder.customerName.setText(customer.getUserName());

        String image = customer.getImage();
        if (image != null && !image.equals("")) {
            Glide.with(mContext).load(Uri.parse(image)).into(holder.customerImage);
        }
    }

    @Override
    public int getItemCount() {
        return mBagList == null ? 0 : mBagList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.head_portrait_todo) CircleImageView customerImage;
        @BindView(R.id.customer_name_todo) TextView customerName;
        @BindView(R.id.time_left_todo) TextView timeLeft;
        @BindView(R.id.food_todo) ImageView foodImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Bag bag = mBagList.get(position);
            Customer customer = mCustomerList.get(position);
            mOnClickListener.onClick(bag, customer, position);
        }
    }
}
