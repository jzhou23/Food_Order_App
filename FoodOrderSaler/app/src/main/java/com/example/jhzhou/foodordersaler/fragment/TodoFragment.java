package com.example.jhzhou.foodordersaler.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jhzhou.foodordersaler.DetailsActivity;
import com.example.jhzhou.foodordersaler.R;
import com.example.jhzhou.foodordersaler.adapter.TodoAdapter;
import com.example.jhzhou.foodordersaler.entity.Bag;
import com.example.jhzhou.foodordersaler.entity.Customer;
import com.example.jhzhou.foodordersaler.entity.Staff;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * Created by jhzhou on 6/5/17.
 */

public class TodoFragment extends Fragment implements TodoAdapter.OnClickListener {

    public static final String LOG_TAG = TodoFragment.class.getSimpleName();

    public static final int REQUEST_DETAILS = 1;

    @BindView(R.id.recycler_view_todo) RecyclerView recyclerView;
    @BindView(R.id.empty_todo) TextView emptyView;

    private FirebaseDatabase mFirebaseDatabase;
//    private List<Bag> mBagList;
    private TodoAdapter mAdapter;
    private String mUUid;

    public static TodoFragment getInstance(String uuid) {
        TodoFragment fragment = new TodoFragment();
        Bundle args = new Bundle();
        args.putString("staffUUid", uuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUUid = getArguments().getString("staffUUid");
        Log.v(LOG_TAG, "mUUid " + mUUid);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);
        ButterKnife.bind(this, view);

        mAdapter = new TodoAdapter(getActivity(), this);
        recyclerView.setAdapter(mAdapter);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = mFirebaseDatabase.getReference().child("orders");

        ChildEventListener mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Bag bag = dataSnapshot.getValue(Bag.class);
                Log.v(LOG_TAG, "key " + dataSnapshot.getKey());
                Log.v(LOG_TAG, "notes " + bag.getNotes());
                Log.v(LOG_TAG, "orderList size " + bag.getOrderList().size());
                Log.v(LOG_TAG, "amount " + bag.getAmount());
                Log.v(LOG_TAG, "customer's uuid " + bag.getCustomerUUid());
                String uuid = bag.getCustomerUUid();

                dataSnapshot.getRef().getParent().child("users").orderByChild("uuid").equalTo(uuid).

                Customer customer = getCustomerInfo(bag.getCustomerUUid());
                String key = dataSnapshot.getKey();

                mAdapter.updateData(bag, customer, key);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        reference.addChildEventListener(mChildEventListener);
    }

    @Override
    public void onClick(Bag bag, Customer customer, int position) {
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra(DetailsActivity.BAG, bag);
        intent.putExtra(DetailsActivity.CUSTOMER, customer);
        intent.putExtra(DetailsActivity.POSITION, position);
        startActivityForResult(intent, REQUEST_DETAILS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_DETAILS) {
                int position = data.getIntExtra(DetailsActivity.POSITION, 0);
                String key = mAdapter.getKey(position);
                Bag bag = mAdapter.getBag(position);

                addOrderToHistory(bag);

                DatabaseReference reference = mFirebaseDatabase.getReference().child("orders").child(key);
                reference.removeValue();

                mAdapter.removePosition(position);
            }
        }
    }

    public void addOrderToHistory(final Bag bag) {
        DatabaseReference reference = mFirebaseDatabase.getReference().child("staffs");
        reference.orderByChild("uuid").equalTo(mUUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    GenericTypeIndicator<List<Bag>> t = new GenericTypeIndicator<List<Bag>>() {
                    };
                    List<Bag> orderList = shot.child("history").getValue(t);

                    if (orderList == null || orderList.size() == 0) {
                        orderList = new ArrayList<Bag>();
                    }

                    orderList.add(bag);
                    shot.child("history").getRef().setValue(orderList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public Customer getCustomerInfo(String uuid) {
        Log.v(LOG_TAG, "getCustomerInfo(String uuid) uuid: " + uuid);
        final List<Customer> customers = new ArrayList<>();

        DatabaseReference reference = mFirebaseDatabase.getReference().child("users");
        reference.orderByChild("uuid").equalTo(uuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    Customer customer = shot.getValue(Customer.class);
                    if (customer == null) {
                        Log.v(LOG_TAG, "customer is null");
                    } else {
                        Log.v(LOG_TAG, "customer.getUUid()" + customer.getUUid());
                        Log.v(LOG_TAG, "customer.getUsername()" + customer.getUserName());
                    }

                    customers.add(customer);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (customers.size() == 0) {
            Log.v(LOG_TAG, "return null");
            return null;
        } else {
            Log.v(LOG_TAG, "return customers.get(0)");
            return customers.get(0);
        }
    }
}
