package edu.zjut.androiddeveloper_ailaiziciqi.calendarview;

import android.content.Context;
import android.graphics.Canvas;


public class DefaultYearView extends YearView {

    private int mTextPadding;

    public DefaultYearView(Context context) {
        super(context);
        mTextPadding = edu.zjut.androiddeveloper_ailaiziciqi.calendarview.CalendarUtil.dipToPx(context, 3);
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    @Override
    protected void onDrawMonth(Canvas canvas, int year, int month, int x, int y, int width, int height) {

        String text = getContext()
                .getResources()
                .getStringArray(R.array.month_string_array)[month - 1];

        canvas.drawText(text,
                x + mItemWidth / 2 - mTextPadding,
                y + mMonthTextBaseLine,
                mMonthTextPaint);
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    @Override
    protected void onDrawWeek(Canvas canvas, int week, int x, int y, int width, int height) {
        String text = getContext().getResources().getStringArray(R.array.year_view_week_string_array)[week];
        canvas.drawText(text,
                x + width / 2,
                y + mWeekTextBaseLine,
                mWeekTextPaint);
    }


    @Override
    protected boolean onDrawSelected(Canvas canvas, edu.zjut.androiddeveloper_ailaiziciqi.calendarview.Calendar calendar, int x, int y, boolean hasScheme) {
        return false;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, edu.zjut.androiddeveloper_ailaiziciqi.calendarview.Calendar calendar, int x, int y) {

    }

    @Override
    protected void onDrawText(Canvas canvas, edu.zjut.androiddeveloper_ailaiziciqi.calendarview.Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        float baselineY = mTextBaseLine + y;
        int cx = x + mItemWidth / 2;

        if (isSelected) {
            canvas.drawText(String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    hasScheme ? mSchemeTextPaint : mSelectTextPaint);
        } else if (hasScheme) {
            canvas.drawText(String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mSchemeTextPaint : mOtherMonthTextPaint);

        } else {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mCurMonthTextPaint : mOtherMonthTextPaint);
        }
    }
}
