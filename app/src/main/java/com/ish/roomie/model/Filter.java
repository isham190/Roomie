package com.ish.roomie.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Filter implements Parcelable {
    private String dateFilter;
    private String timeFilter;
    private String capacityFilter;

    public Filter() {
    }

    public String getDateFilter() {
        return dateFilter;
    }

    public void setDateFilter(String dateFilter) {
        this.dateFilter = dateFilter;
    }

    public String getTimeFilter() {
        return timeFilter;
    }

    public void setTimeFilter(String timeFilter) {
        this.timeFilter = timeFilter;
    }

    public String getCapacityFilter() {
        return capacityFilter;
    }

    public void setCapacityFilter(String capacityFilter) {
        this.capacityFilter = capacityFilter;
    }

    public void clearFilters() {
        dateFilter = null;
        timeFilter = null;
        capacityFilter = null;

    }

    public static final Parcelable.Creator<Filter> CREATOR
            = new Creator<Filter>() {
        @Override
        public Filter createFromParcel(Parcel source) {
            return new Filter(source);
        }

        @Override
        public Filter[] newArray(int size) {
            return new Filter[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dateFilter);
        dest.writeString(timeFilter);
        dest.writeString(capacityFilter);
    }

    public Filter(Parcel source) {
        dateFilter = source.readString();
        timeFilter = source.readString();
        capacityFilter = source.readString();
    }
}
