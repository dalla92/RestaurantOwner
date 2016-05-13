package it.polito.group2.restaurantowner.user.order;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
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
import it.polito.group2.restaurantowner.data.Meal;

public class MealFragment extends ListFragment {

    public static final String CATEGORY = "categoryID";
    private String categoryID;

    private ArrayList<MealModel> modelList;
    private OnActionListener mCallback;

    public MealFragment() {}

    public static MealFragment newInstance(String catID) {
        MealFragment fragment = new MealFragment();
        Bundle args = new Bundle();
        args.putString(CATEGORY, catID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryID = getArguments().getString(CATEGORY);
        }
        modelList = getModel(categoryID);
        setHasOptionsMenu(true);
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getActivity().
                    getResources().getString(R.string.order_meal_title));
        } catch (Exception e) {
            Log.d("FILIPPO", e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_fragment_meal, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MealAdapter adapter = new MealAdapter(getActivity(), modelList);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mCallback.onMealSelected(modelList.get(position).getMeal());
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
        public void onMealSelected(Meal meal);
        public void onCartClicked();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.order_fragment_meal_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.goto_cart){
            mCallback.onCartClicked();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ArrayList<MealModel> getModel(String categoryID) {
        ArrayList<MealModel> list = new ArrayList<MealModel>();
        ArrayList<Meal> categoryMeals = getMealList(categoryID);
        for(Meal m : categoryMeals) {
            MealModel model = new MealModel(m.getMealId(), m.getMeal_name(), m);
            list.add(model);
        }
        return list;
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //GENERATE DATAS
    //TODO change this method to get data from firebase
    private ArrayList<Meal> getMealList(String categoryID) {
        ArrayList<Meal> mealList = new ArrayList<Meal>();
        for(int i=0; i<10; i++) {
            Meal m = new Meal();
            m.setRestaurantId("ResID0");
            m.setCategory(categoryID);
            m.setMeal_name("meal "+i);
            m.setMealId("mealID"+i);
            mealList.add(m);
        }
        return mealList;
    }

}
