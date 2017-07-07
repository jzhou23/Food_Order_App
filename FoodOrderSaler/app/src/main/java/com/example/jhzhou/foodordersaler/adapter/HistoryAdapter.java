package com.example.jhzhou.foodordersaler.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jhzhou.foodordersaler.R;
import com.example.jhzhou.foodordersaler.entity.Bag;
import com.example.jhzhou.foodordersaler.entity.Customer;
import com.example.jhzhou.foodordersaler.util.DateUtil;
import com.example.jhzhou.foodordersaler.util.NumUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jhzhou on 6/14/17.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>{

    private Context mContext;
    private List<Bag> mBagList;
    private List<Customer> mCustomerList;
    private ClickListener mClickListener;

    public interface ClickListener {
        void onClick(Bag bag);
    }

    public HistoryAdapter(Context context, List<Bag> bagList, ClickListener clickListener) {
        mContext = context;
        mBagList = bagList;
        mCustomerList = new ArrayList<>();
        mClickListener = clickListener;
    }

    public void setList(List<Bag> bagList, List<Customer> customerList) {
        mBagList = bagList;
        mCustomerList = customerList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bag bag = mBagList.get(position);
        Customer customer = mCustomerList.get(position);

        String imageString = customer.getImage();
        if (imageString != null && !imageString.equals("")) {
            Glide.with(mContext).load(Uri.parse(imageString)).into(holder.customerImage);
        }

        holder.customerName.setText(customer.getUserName());
        holder.placedTime.setText(DateUtil.getDate(bag.getTime()));
        holder.amount.setText(NumUtil.addDollarSign(bag.getAmount()));
    }

    @Override
    public int getItemCount() {
        return mBagList == null ? 0 : mBagList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.head_portrait_history) CircleImageView customerImage;
        @BindView(R.id.customer_name_history) TextView customerName;
        @BindView(R.id.placed_time_history) TextView placedTime;
        @BindView(R.id.price_history) TextView amount;
        @BindView(R.id.comments_history) TextView comments;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mClickListener.onClick(mBagList.get(position));
        }
    }

}
