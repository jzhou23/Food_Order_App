package com.example.jhzhou.foodordercustomer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jhzhou.foodordercustomer.R;
import com.example.jhzhou.foodordercustomer.entity.Bag;
import com.example.jhzhou.foodordercustomer.entity.Food;
import com.example.jhzhou.foodordercustomer.util.DateUtil;
import com.example.jhzhou.foodordercustomer.util.NumUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jhzhou on 6/13/17.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>{

    private Context mContext;
    private List<Bag> mBagList;
    private ClickListener mClickListener;

    public interface ClickListener {
        void onClick(Bag bag);
    }

    public HistoryAdapter(Context context, List<Bag> bagList, ClickListener clickListener) {
        mContext = context;
        mBagList = bagList;
        mClickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bag bag = mBagList.get(position);
        holder.timeText.setText(DateUtil.getDate(bag.getTime()));
        holder.priceText.setText(NumUtil.addDollarSign(bag.getAmount()));
    }

    @Override
    public int getItemCount() {
        return mBagList == null ? 0 : mBagList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.list_item_place_time_history) TextView timeText;
        @BindView(R.id.list_item_price_history) TextView priceText;

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
