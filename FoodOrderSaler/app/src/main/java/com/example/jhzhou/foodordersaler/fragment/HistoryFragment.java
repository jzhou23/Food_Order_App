package com.example.jhzhou.foodordersaler.fragment;

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

import com.example.jhzhou.foodordersaler.R;
import com.example.jhzhou.foodordersaler.adapter.HistoryAdapter;
import com.example.jhzhou.foodordersaler.entity.Bag;
import com.example.jhzhou.foodordersaler.entity.Customer;
import com.example.jhzhou.foodordersaler.entity.Staff;
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

/**
 * Created by jhzhou on 6/5/17.
 */

public class HistoryFragment extends Fragment implements HistoryAdapter.ClickListener{

    public static final String LOG_TAG = HistoryFragment.class.getSimpleName();

    @BindView(R.id.recycler_view_history) RecyclerView recyclerView;
    @BindView(R.id.empty_history) TextView emptyView;

    private String uuid;
    private HistoryAdapter mAdapter;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReference;
    private List<Bag> mBagList;
    private List<Customer> mCustomerList;

    public static HistoryFragment getInstance(String uuid) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putString("staffUUid", uuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uuid = getArguments().getString("staffUUid");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBagList = new ArrayList<>();
        mCustomerList = new ArrayList<>();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mReference = mFirebaseDatabase.getReference().child("staffs");
        mReference.orderByChild("uuid").equalTo(uuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot shot: dataSnapshot.getChildren()) {
                    GenericTypeIndicator<List<Bag>> t = new GenericTypeIndicator<List<Bag>>() {};
                    mBagList = shot.child("history").getValue(t);

                    for (int i = 0; i < mBagList.size(); i++) {
                        addCustomerInfo(mBagList.get(i).getCustomerUUid());
                    }

                    mAdapter.setList(mBagList, mCustomerList);
                    checkEmpty();
                }

                Log.v(LOG_TAG, "bagList size " + mBagList.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mAdapter = new HistoryAdapter(getActivity(), mBagList, this);
        recyclerView.setAdapter(mAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);

        checkEmpty();
    }

    @Override
    public void onClick(Bag bag) {
        // show something
    }

    public void checkEmpty() {
        if (mBagList == null || mBagList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        } else {
            emptyView.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    public void addCustomerInfo(String uuid) {
        DatabaseReference reference = mFirebaseDatabase.getReference().child("users");
        reference.orderByChild("uuid").equalTo(uuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    Customer customer = shot.getValue(Customer.class);
                    mCustomerList.add(customer);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
