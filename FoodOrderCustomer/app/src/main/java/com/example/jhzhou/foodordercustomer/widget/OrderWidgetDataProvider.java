package com.example.jhzhou.foodordercustomer.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.jhzhou.foodordercustomer.DetailsActivity;
import com.example.jhzhou.foodordercustomer.R;
import com.example.jhzhou.foodordercustomer.entity.Food;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhzhou on 6/15/17.
 */

public class OrderWidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private static final String LOG_TAG = OrderWidgetDataProvider.class.getSimpleName();

    private FirebaseDatabase mFirebaseDatabase;

    private Context mContext;
    private List<Food> mFoodList;


    public OrderWidgetDataProvider(Context context) {
        mContext = context;
    }

    private static final String TAG_LOG = OrderWidgetDataProvider.class.getSimpleName();

    @Override
    public void onCreate() {
        Log.v(LOG_TAG, "onCreate");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        initialData();
    }

    @Override
    public void onDataSetChanged() {

    }

    public void initialData() {
        final long identityToken = Binder.clearCallingIdentity();

        mFoodList = new ArrayList<>();

        DatabaseReference reference = mFirebaseDatabase.getReference().child("foods");
        ChildEventListener mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Food food = dataSnapshot.getValue(Food.class);
                if (food.getIsSpecial()) {
                    mFoodList.add(food);

                    update();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };

        reference.addChildEventListener(mChildEventListener);

        Binder.restoreCallingIdentity(identityToken);
    }

    public void update() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(mContext, getClass()));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
        Log.v(LOG_TAG, "food list size: " + mFoodList.size());
    }

    @Override
    public void onDestroy() {
        mFoodList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mFoodList == null ? 0 : mFoodList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position >= mFoodList.size()) {
            return null;
        }

        Food food = mFoodList.get(position);

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.list_item_widget);

        views.setTextViewText(R.id.food_name, food.getName());
        views.setTextViewText(R.id.food_percent, food.getPercentOff() + "% Off");

        final Intent fillIntent = new Intent();
        fillIntent.putExtra(DetailsActivity.FOOD, food);
        views.setOnClickFillInIntent(R.id.list_view_item, fillIntent);
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
