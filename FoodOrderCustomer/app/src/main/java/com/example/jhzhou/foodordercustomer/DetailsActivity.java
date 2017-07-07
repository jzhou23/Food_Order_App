package com.example.jhzhou.foodordercustomer;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.example.jhzhou.foodordercustomer.entity.Food;
import com.example.jhzhou.foodordercustomer.entity.Order;
import com.example.jhzhou.foodordercustomer.util.NumUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {

    @BindView(R.id.coordinator_layout_details) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.appbar_details) AppBarLayout appBarLayout;
    @BindView(R.id.collapsing_toolbar_details) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.image_food_details) ImageView foodImage;
    @BindView(R.id.toolbar_details) Toolbar toolbar;
    @BindView(R.id.name_food_details) TextView foodName;
    @BindView(R.id.price_food_details) TextView foodPrice;
    @BindView(R.id.spicy_food_details) TextView foodSpicy;
    @BindView(R.id.rating_food_details) RatingBar foodRating;
    @BindView(R.id.description_food_details) TextView foodDescription;
    @BindView(R.id.reduce_button) ImageButton reduceButton;
    @BindView(R.id.num_order) TextView foodNum;
    @BindView(R.id.increase_button) ImageButton increaseButton;
    @BindView(R.id.done_button) Button doneButton;
    @BindView(R.id.linear_layout_ingreadients) LinearLayout linearLayout;

    public static final String FOOD = "food";
    public static final String ORDER = "order";

    public static final int DEFAULT_NUM = 1;

    private int mNumber;
    private Food mFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        Intent intent = getIntent();
        if (intent != null) {
            mFood = intent.getParcelableExtra(FOOD);
            initalUI();
        }

//        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            boolean isShow = false;
//            int scrollRange = -1;
//
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                if (scrollRange == -1) {
//                    scrollRange = appBarLayout.getTotalScrollRange();
//                }
//                if (scrollRange + verticalOffset == 0) {
//                    collapsingToolbarLayout.setTitle(mFood.getName());
//                    isShow = true;
//                } else if (isShow) {
//                    collapsingToolbarLayout.setTitle("");
//                    isShow = false;
//                }
//            }
//        });
    }

    public void initalUI() {
        Glide.with(this).load(Uri.parse(mFood.getImage())).into(foodImage);

        foodName.setText(mFood.getName());
        foodPrice.setText(NumUtil.addDollarSign(mFood.getPrice()));

        int spicy = mFood.getSpicy();
        if (spicy == 0) {
            foodSpicy.setVisibility(View.GONE);
        } else {
            foodSpicy.setText(String.valueOf(spicy));
        }

        foodRating.setRating((float) mFood.getRating());
        foodDescription.setText(mFood.getDescription());

        Map<String, Double> map = mFood.getIngredients();
        final List<CheckBox> checkBoxes = new ArrayList<>();
        if (map != null && map.size() != 0) {
            linearLayout.setVisibility(View.VISIBLE);
            for (Map.Entry<String, Double> entry : map.entrySet()) {
                RelativeLayout layout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.ingredient, linearLayout, false);
                linearLayout.addView(layout);

                CheckBox ingredientName = (CheckBox) layout.findViewById(R.id.checkbox_ingredient);
                checkBoxes.add(ingredientName);
                TextView addtionPrice = (TextView) layout.findViewById(R.id.price_addition_ingredient);

                ingredientName.setText(entry.getKey());
                addtionPrice.setText(NumUtil.addPlusDollarSign(entry.getValue()));
            }
        } else {
            linearLayout.setVisibility(View.INVISIBLE);
        }

        reduceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNumber < 2) {
                    makeSnackBar();
                } else {
                    mNumber--;
                    foodNum.setText(String.valueOf(mNumber));
                }
            }
        });

        mNumber = DEFAULT_NUM;
        foodNum.setText(String.valueOf(mNumber));

        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNumber++;
                foodNum.setText(String.valueOf(mNumber));
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                int nums = Integer.parseInt(foodNum.getText().toString());

                List<String> addtionList = getAdditionList(checkBoxes);

                Order order = new Order(mFood, nums, addtionList);
                intent.putExtra(ORDER, order);

                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return true;
    }

    public void makeSnackBar() {
        Snackbar.make(coordinatorLayout, R.string.nums_notice, Snackbar.LENGTH_SHORT).show();
    }

    public List<String> getAdditionList(List<CheckBox> checkBoxes) {
        List<String> addtionList = new ArrayList<>();
        for (CheckBox checkBox: checkBoxes) {
            if (checkBox.isChecked()) {
                addtionList.add(checkBox.getText().toString());
            }
        }
        return addtionList;
    }
}
