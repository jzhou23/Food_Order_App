package com.example.jhzhou.foodordercustomer.widget;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

/**
 * Created by jhzhou on 6/15/17.
 */

public class OrderWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.v("RemoteViewsFactory", "onGetViewFactory");
        return new OrderWidgetDataProvider(this.getApplicationContext());
    }
}
