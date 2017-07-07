package com.example.jhzhou.foodordercustomer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.jhzhou.foodordercustomer.entity.Bag;
import com.example.jhzhou.foodordercustomer.entity.Order;

import org.w3c.dom.Text;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jhzhou on 6/5/17.
 */

public class BagActivity extends AppCompatActivity{

    @BindView(R.id.coordinator_layout_bag) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.linear_layout_bag) LinearLayout linearLayout;
    @BindView(R.id.spinner_bag) Spinner spinner;
    @BindView(R.id.note_bag) TextView notesTextView;
    @BindView(R.id.submit_order_bag) Button button;
    @BindView(R.id.toolbar_bag) Toolbar toolbar;
    @BindView(R.id.empty_bag) TextView emptyView;

    public static final String LOG_TAG = BagActivity.class.getSimpleName();
    public static final String BAG = "bag";
    private Bag mBag;
    private boolean[] visited;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bag);
        ButterKnife.bind(this);

        // set up toolbar
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // get data from MainActivity
        Intent intent = getIntent();
        if (intent != null) {
            mBag = intent.getParcelableExtra(BAG);
            initailUI(mBag);
        }
    }

    public void initailUI(Bag bag) {
        if (bag == null) {
            return;
        }

        List<Order> listOrder = bag.getOrderList();
        Collections.sort(listOrder, new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                if (o1 == null) {
                    return 1;
                } else if (o2 == null) {
                    return -1;
                }

                return o1.getFood().getCategory() - o2.getFood().getCategory();
            }
        });

        visited = new boolean[3];
        Log.d(LOG_TAG, listOrder.size() + "");

        // check order is empty
        if (listOrder == null || listOrder.size() == 0) {
            linearLayout.setVisibility(View.INVISIBLE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.INVISIBLE);
        }

        for (Order o: listOrder) {
            int category = o.getFood().getCategory();
            Log.v(LOG_TAG, "category " + category);

            if (!visited[category - 1]) {
                // add title into linearlayout
                visited[category - 1] = true;
                LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.list_item_bag_title, linearLayout, false);
                linearLayout.addView(layout);

                TextView textView = (TextView) layout.findViewById(R.id.order_title_text_view);
                String title = null;
                switch (category) {
                    case MainActivity.CATEGORY_APPETIZER:
                        title = "appetizer";
                        break;
                    case MainActivity.CATEGORY_MEAL:
                        title = "meal";
                        break;
                    case MainActivity.CATEGORY_DRINKS:
                        title = "drinks";
                        break;
                }
                textView.setText(String.valueOf(title));
            }

            RelativeLayout layout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.list_item_bag, linearLayout, false);
            linearLayout.addView(layout);

            TextView orderName = (TextView) layout.findViewById(R.id.order_name_bag);
            TextView orderNums = (TextView) layout.findViewById(R.id.order_nums_bag);
            TextView orderAddition = (TextView) layout.findViewById(R.id.order_additon_bag);

            orderName.setText(o.getFood().getName());
            orderNums.setText(getString(R.string.num_sign) + String.valueOf(o.getNums()));
            List<String> addtionList = o.getAdditionList();
            if (addtionList != null && addtionList.size() != 0) {
                orderAddition.setVisibility(View.VISIBLE);
                for (int i = 0; i < addtionList.size(); i++) {
                    orderAddition.append("add " + addtionList.get(i));
                    if (i != addtionList.size() - 1) {
                        orderAddition.append("\n");
                    }
                }
            }
        }

        // set up button action
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stay = spinner.getSelectedItem().toString();
                String[] array = getResources().getStringArray(R.array.choices_array);
                boolean isStay = stay.equals(array[0]);

                String notes = notesTextView.getText().toString();

                mBag.setIsStay(isStay);
                mBag.setNotes(notes);

                List<Order> orderList = mBag.getOrderList();
                if (orderList == null || orderList.size() == 0) {
                    Snackbar.make(coordinatorLayout, R.string.empty_bag, Snackbar.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra(BAG, mBag);
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
}
