package it.polito.group2.restaurantowner.owner;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import it.polito.group2.restaurantowner.owner.reservations.TableFragment;
import it.polito.group2.restaurantowner.owner.reservations.OrderFragment;

public class FragmentPageAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private long date;
    private TableFragment table_fragment;
    private OrderFragment takeaway_fragment;
    private String restaurantId;

    public FragmentPageAdapter(FragmentManager fm, int NumOfTabs, long date, String restaurantId) {
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
                takeaway_fragment = new OrderFragment();
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

    public OrderFragment getTakeaway_fragment() {
        return takeaway_fragment;
    }
}