package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.search.SearchListAdapter;

public class ScheduleWithCheck{
    private boolean isChecked;
    private boolean isVisible;
    private SearchListAdapter.ViewHolder viewHolder;
    private Schedule schedule;

    public ScheduleWithCheck(Schedule schedule) {
        this.schedule = schedule;
        this.isChecked = false;
        this.viewHolder = null;
    }

    public boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public SearchListAdapter.ViewHolder getViewHolder() {
        return viewHolder;
    }

    public void setViewHolder(SearchListAdapter.ViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
