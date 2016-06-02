package it.polito.group2.restaurantowner.user.order;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.MealAddition;

public class AdditionFragment extends ListFragment {

    public static final String LIST = "additionList";
    private ArrayList<MealAddition> additionList;
    private ArrayList<MealAddition> selectedAddition;
    private OnActionListener mCallback;

    public AdditionFragment() {}

    public static AdditionFragment newInstance(ArrayList<MealAddition> list) {
        AdditionFragment fragment = new AdditionFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(LIST, list);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            additionList = getArguments().getParcelableArrayList(LIST);
        }
        setHasOptionsMenu(true);
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getActivity().
                    getResources().getString(R.string.order_addition_title));
        } catch (Exception e) {
            Log.d("FILIPPO", e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_order_fragment_addition, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setAdditionList();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if(selectedAddition.indexOf(additionList.get(position)) == -1) {
            selectedAddition.add(additionList.get(position));
        } else {
            selectedAddition.remove(additionList.get(position));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnActionListener) {
            mCallback = (OnActionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnActionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public interface OnActionListener {
        public void onAdditionsSelected(ArrayList<MealAddition> selectedAdditions);
        public void onCartClicked();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.user_order_fragment_addition_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_next){
            // Send the Additions to the host activity
            mCallback.onAdditionsSelected(selectedAddition);
            return true;
        }

        if(id == R.id.goto_cart){
            mCallback.onCartClicked();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setAdditionList() {
        final RecyclerView list = (RecyclerView) getView().findViewById(R.id.addition_list);
        assert list != null;
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        list.setNestedScrollingEnabled(false);
        AdditionAdapter adapter = new AdditionAdapter(getContext(), additionList);
        list.setAdapter(adapter);
    }

}
