package com.example.jhzhou.foodordercustomer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jhzhou.foodordercustomer.R;
import com.example.jhzhou.foodordercustomer.adapter.HistoryAdapter;
import com.example.jhzhou.foodordercustomer.entity.Bag;
import com.example.jhzhou.foodordercustomer.entity.Customer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jhzhou on 6/12/17.
 */

public class HistoryFragment extends Fragment implements HistoryAdapter.ClickListener{

    @BindView(R.id.recycler_view_history) RecyclerView historyRecyclerView;
    @BindView(R.id.empty_history) TextView emptyView;

    private List<Bag> mBagList;
    private HistoryAdapter mAdapter;

    public static HistoryFragment getInstance(ArrayList<Bag> bagList) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("bagList", bagList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBagList = getArguments().getParcelableArrayList("bagList");
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
        mAdapter = new HistoryAdapter(getActivity(), mBagList, this);
        historyRecyclerView.setAdapter(mAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        historyRecyclerView.setLayoutManager(layoutManager);

        historyRecyclerView.setHasFixedSize(true);

        if (mBagList == null || mBagList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            historyRecyclerView.setVisibility(View.INVISIBLE);
        } else {
            emptyView.setVisibility(View.INVISIBLE);
            historyRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(Bag bag) {
        Toast.makeText(getContext(), R.string.clickable_notice, Toast.LENGTH_SHORT).show();
    }
}
