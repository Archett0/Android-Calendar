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

    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        RemoteViewsService.RemoteViewsFactory rc = new ListRemoteViewsFactory(this, intent);
        return rc;
    }
}
