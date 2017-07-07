package com.example.jhzhou.foodordercustomer.widget;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import com.example.jhzhou.foodordercustomer.DetailsActivity;
import com.example.jhzhou.foodordercustomer.MainActivity;
import com.example.jhzhou.foodordercustomer.R;

/**
 * Implementation of App Widget functionality.
 */
public class OrderWidgetProvider extends AppWidgetProvider {

    private static final String TAG_LOG = OrderWidgetProvider.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId: appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.order_app_widget);

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget_title, pendingIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                setRemoteAdapter(context, views);
            } else {
                setRemoteAdapterV11(context, views);
            }

            views.setEmptyView(R.id.widget_list_view, R.id.widget_empty_view);
            Intent clickIntentTemp = new Intent(context, DetailsActivity.class);
            PendingIntent clickPendingIntentTemp = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemp)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_list_view, clickPendingIntentTemp);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public void setRemoteAdapter(Context context, RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_list_view, new Intent(context, OrderWidgetService.class));
    }

    @SuppressWarnings("deprecation")
    public void setRemoteAdapterV11(Context context, RemoteViews views) {
        views.setRemoteAdapter(0, R.id.widget_list_view, new Intent(context, OrderWidgetService.class));
    }
}

