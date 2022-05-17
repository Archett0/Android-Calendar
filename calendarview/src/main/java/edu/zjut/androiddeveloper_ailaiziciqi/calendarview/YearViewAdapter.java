package edu.zjut.androiddeveloper_ailaiziciqi.calendarview;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Constructor;

final class YearViewAdapter extends edu.zjut.androiddeveloper_ailaiziciqi.calendarview.BaseRecyclerAdapter<Month> {
    private edu.zjut.androiddeveloper_ailaiziciqi.calendarview.CalendarViewDelegate mDelegate;
    private int mItemWidth, mItemHeight;

    YearViewAdapter(Context context) {
        super(context);
    }

    final void setup(edu.zjut.androiddeveloper_ailaiziciqi.calendarview.CalendarViewDelegate delegate) {
        this.mDelegate = delegate;
    }


    final void setYearViewSize(int width, int height) {
        this.mItemWidth = width;
        this.mItemHeight = height;
    }

    @Override
    RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        edu.zjut.androiddeveloper_ailaiziciqi.calendarview.YearView yearView;
        if (TextUtils.isEmpty(mDelegate.getYearViewClassPath())) {
            yearView = new edu.zjut.androiddeveloper_ailaiziciqi.calendarview.DefaultYearView(mContext);
        } else {
            try {
                Constructor constructor = mDelegate.getYearViewClass().getConstructor(Context.class);
                yearView = (edu.zjut.androiddeveloper_ailaiziciqi.calendarview.YearView) constructor.newInstance(mContext);
            } catch (Exception e) {
                e.printStackTrace();
                yearView = new edu.zjut.androiddeveloper_ailaiziciqi.calendarview.DefaultYearView(mContext);
            }
        }
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.MATCH_PARENT);
        yearView.setLayoutParams(params);
        return new YearViewHolder(yearView, mDelegate);
    }

    @Override
    void onBindViewHolder(RecyclerView.ViewHolder holder, Month item, int position) {
        YearViewHolder h = (YearViewHolder) holder;
        edu.zjut.androiddeveloper_ailaiziciqi.calendarview.YearView view = h.mYearView;
        view.init(item.getYear(), item.getMonth());
        view.measureSize(mItemWidth, mItemHeight);
    }

    private static class YearViewHolder extends RecyclerView.ViewHolder {
        edu.zjut.androiddeveloper_ailaiziciqi.calendarview.YearView mYearView;
        YearViewHolder(View itemView, edu.zjut.androiddeveloper_ailaiziciqi.calendarview.CalendarViewDelegate delegate) {
            super(itemView);
            mYearView = (edu.zjut.androiddeveloper_ailaiziciqi.calendarview.YearView) itemView;
            mYearView.setup(delegate);
        }
    }
}
