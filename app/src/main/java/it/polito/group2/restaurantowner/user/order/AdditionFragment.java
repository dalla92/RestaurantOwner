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

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.MealAddition;

public class AdditionFragment extends ListFragment {

    public static final String MEAL = "mealID";
    private String mealID;

    private ArrayList<AdditionModel> modelList;
    private OnActionListener mCallback;

    public AdditionFragment() {}

    public static AdditionFragment newInstance(String mealID) {
        AdditionFragment fragment = new AdditionFragment();
        Bundle args = new Bundle();
        args.putString(MEAL, mealID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mealID = getArguments().getString(MEAL);
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
        View view = inflater.inflate(R.layout.order_fragment_addition, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setAdditionList();
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
        public void onNextClicked(ArrayList<MealAddition> additionList);
        public void onCartClicked();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.order_fragment_addition_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_next){
            // Send the Additions to the host activity
            ArrayList<MealAddition> additionList = new ArrayList<MealAddition>();
            for(AdditionModel am : modelList) {
                if(am.isSelected()) {
                    additionList.add(am.getAddition());
                }
            }
            mCallback.onNextClicked(additionList);
            return true;
        }

        if(id == R.id.goto_cart){
            mCallback.onCartClicked();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setAdditionList() {
        modelList = getModel(mealID);
        final RecyclerView additionList = (RecyclerView) getView().findViewById(R.id.addition_list);
        assert additionList != null;
        additionList.setLayoutManager(new LinearLayoutManager(getContext()));
        additionList.setNestedScrollingEnabled(false);
        AdditionAdapter adapter = new AdditionAdapter(getContext(), modelList);
        additionList.setAdapter(adapter);
    }

    private ArrayList<AdditionModel> getModel(String mealID) {
        ArrayList<AdditionModel> list = new ArrayList<AdditionModel>();
        ArrayList<MealAddition> mealAdditions = getAdditionList(mealID);
        for(MealAddition ma : mealAdditions) {
            AdditionModel model = new AdditionModel(ma.getAddition_id(), ma.getName(), false, ma);
            list.add(model);
        }
        return list;
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //GENERATE DATAS
    //TODO change this method to get data from firebase
    private ArrayList<MealAddition> getAdditionList(String mealID) {
        ArrayList<MealAddition> mealAdditionList = new ArrayList<MealAddition>();
        for(int i=0; i<10; i++) {
            MealAddition a = new MealAddition();
            a.setmeal_id(mealID);
            a.setName("Addition " + i);
            a.setAddition_id("addID" + i);
            mealAdditionList.add(a);
        }
        return mealAdditionList;
    }
}
