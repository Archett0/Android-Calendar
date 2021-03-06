package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DesktopWidget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.R;

public class ListWidgetService extends RemoteViewsService {
    private ListRemoteViewsFactory listRemoteViewsFactory;

    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this, intent);
    }
}
