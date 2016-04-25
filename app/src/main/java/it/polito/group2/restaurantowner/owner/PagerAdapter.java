package it.polito.group2.restaurantowner.owner;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private long date;
    private TableFragment table_fragment;
    private TakeAwayFragment takeaway_fragment;
    private String restaurantId;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, long date, String restaurantId) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.date = date;
        this.restaurantId = restaurantId;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putLong("date", date);
        bundle.putString("id", restaurantId);

        switch (position) {
            case 0:
                table_fragment = new TableFragment();
                table_fragment.setArguments(bundle);
                return table_fragment;
            case 1:
                takeaway_fragment = new TakeAwayFragment();
                takeaway_fragment.setArguments(bundle);
                return takeaway_fragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    public TableFragment getTable_fragment() {
        return table_fragment;
    }

    public TakeAwayFragment getTakeaway_fragment() {
        return takeaway_fragment;
    }
}