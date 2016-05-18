package it.polito.group2.restaurantowner.owner;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.firebase.client.Firebase;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.firebasedata.Meal;

/**
 * Created by Daniele on 09/04/2016.
 */

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private boolean todo = false;
    private String url = "";
    private final ItemTouchHelperAdapter mAdapter;
    private ArrayList<Meal> meals = new ArrayList<Meal>();

    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter, boolean todo, ArrayList<Meal> meals, String url) {
        this.todo = todo;
        this.url = url;
        this.meals = meals;
        mAdapter = adapter;
    }

    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
        if(this.todo == true) {
            String meal_key = meals.get(viewHolder.getAdapterPosition()).getMeal_id();
            Firebase ref = new Firebase(this.url + meal_key);
            //delete
            ref.setValue(null);
        }
    }

}
