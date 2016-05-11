package it.polito.group2.restaurantowner.user.order;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.Meal;

public class MealFragment extends ListFragment {

    //parameter's names
    public static final String RESTAURANT_ID = "restaurant_id";
    public static final String MENUCATEGORY_ID = "menucategory_id";

    //paramter's values
    private String restaurant_id;
    private String menucategory_id;

    private ArrayList<Meal> listMeal;
    private OnMealSelectedListener mCallback;

    public MealFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            restaurant_id = getArguments().getString(RESTAURANT_ID);
            menucategory_id = getArguments().getString(MENUCATEGORY_ID);
        }
        listMeal = getMealList(restaurant_id, menucategory_id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_meal, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MealArrayAdapter adapter = new MealArrayAdapter(getActivity(),
                android.R.layout.simple_list_item_1, getItemList(listMeal));
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Send the MealID to the host activity
        mCallback.onMealSelected(restaurant_id,
                menucategory_id,
                listMeal.get(position).getMealId());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMealSelectedListener) {
            mCallback = (OnMealSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMealSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public interface OnMealSelectedListener {
        public void onMealSelected(String restaurantID, String menuCategoryID, String mealID);
    }

    /////////////////////////////////////////////////////////////////////////////////////
    // GETTING DATAS
    private ArrayList<String> getItemList(ArrayList<Meal> objList) {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < objList.size(); ++i) {
            list.add(objList.get(i).getMeal_name());
        }
        return list;
    }

    private ArrayList<Meal> getMealList(String restaurant_id, String menucategory_id) {
        ArrayList<Meal> mealList = new ArrayList<Meal>();

        for(int i=0; i<10; i++) {
            Meal m = new Meal();
            m.setMeal_name("meal "+i);
            m.setMealId("mealID"+i);
            mealList.add(m);
        }

        return mealList;
    }

    /////////////////////////////////////////////////////////////////////////////////


    /*



    // TODO: Rename and change types and number of parameters
    public static MealFragment newInstance(String param1, String param2) {
        MealFragment fragment = new MealFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    */
}
