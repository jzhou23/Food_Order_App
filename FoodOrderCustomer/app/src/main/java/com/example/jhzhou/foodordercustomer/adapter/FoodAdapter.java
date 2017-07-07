package com.example.jhzhou.foodordercustomer.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jhzhou.foodordercustomer.R;
import com.example.jhzhou.foodordercustomer.entity.Food;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jhzhou on 6/4/17.
 */

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder>{

    private List<Food> mFoodList;
    private Context mContext;
    private ClickListener mClickListener;

    public interface ClickListener {
        void onClick(Food food);
    }

    public FoodAdapter(Context context, List<Food> foodList, ClickListener clickListener) {
        mContext = context;
        mFoodList = foodList;
        mClickListener = clickListener;
    }

    public void setAdapter(List<Food> foodList) {
        mFoodList = foodList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Food food = mFoodList.get(position);
        if (food.getImage() != null) {
            Uri imageUri = Uri.parse(food.getImage());
            Glide.with(mContext).load(imageUri).into(holder.image);
            holder.image.setContentDescription(food.getName() + " image");
        }
        holder.name.setText(food.getName());
        holder.price.setText(mContext.getString(R.string.dollar_sign) + food.getPrice());
    }

    @Override
    public int getItemCount() {
        return mFoodList == null ? 0 : mFoodList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.image_food_list_item) ImageView image;
        @BindView(R.id.name_food_list_item) TextView name;
        @BindView(R.id.price_food_list_item) TextView price;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Food food = mFoodList.get(position);
            mClickListener.onClick(mFoodList.get(position));
        }
    }
}
