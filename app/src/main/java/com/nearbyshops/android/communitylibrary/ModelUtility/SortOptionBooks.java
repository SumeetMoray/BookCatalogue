package com.nearbyshops.android.communitylibrary.ModelUtility;

/**
 * Created by sumeet on 17/8/16.
 */

public class SortOptionBooks {

    private int sort_by;
    private boolean whether_descending;

    public int getSort_by() {
        return sort_by;
    }

    public void setSort_by(int sort_by) {
        this.sort_by = sort_by;
    }

    public boolean isWhether_descending() {
        return whether_descending;
    }

    public void setWhether_descending(boolean whether_descending) {
        this.whether_descending = whether_descending;
    }
}
