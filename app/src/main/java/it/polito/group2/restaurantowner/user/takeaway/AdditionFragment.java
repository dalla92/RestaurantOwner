package it.polito.group2.restaurantowner.user.takeaway;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
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

    //parameter's names
    public static final String RESTAURANT_ID = "restaurant_id";
    public static final String MENUCATEGORY_ID = "menucategory_id";
    public static final String MEAL_ID = "meal_id";

    //paramter's values
    private String restaurant_id;
    private String menucategory_id;
    private String meal_id;

    private ArrayList<MealAddition> listMealAddition;
    private ArrayList<AdditionModel> listModel;
    private OnNextClickedListener mCallback;

    public AdditionFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            restaurant_id = getArguments().getString(RESTAURANT_ID);
            menucategory_id = getArguments().getString(MENUCATEGORY_ID);
            meal_id = getArguments().getString(MEAL_ID);
        }
        listMealAddition = getAdditionList(restaurant_id, menucategory_id, meal_id);
        listModel = getModel(listMealAddition);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_takeaway_addition, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AdditionArrayAdapter adapter = new AdditionArrayAdapter(getActivity(), listModel);
        setListAdapter(adapter);
    }

    /*
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) l.getItemAtPosition(position);
        //listMeal.get(position).getCategoryID()
    }
    */

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNextClickedListener) {
            mCallback = (OnNextClickedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNextSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public interface OnNextClickedListener {
        public void onNextClicked(String restaurantID, String menuCategoryID,
                                  String mealID, ArrayList<String> listAdditionID);
    }

    private ArrayList<AdditionModel> getModel(ArrayList<MealAddition> objList) {
        ArrayList<AdditionModel> list = new ArrayList<AdditionModel>();
        for (int i = 0; i < objList.size(); ++i) {
            AdditionModel am = new AdditionModel(objList.get(i).getName(), objList.get(i).getAddition_id());
            list.add(am);
        }
        return list;
    }

    private ArrayList<MealAddition> getAdditionList(String restaurant_id, String menucategory_id, String meal_id) {
        ArrayList<MealAddition> mealAdditionList = new ArrayList<MealAddition>();

        for(int i=0; i<10; i++) {
            MealAddition a = new MealAddition();
            a.setName("MealAddition " + i);
            a.setAddition_id("addID" + i);
            mealAdditionList.add(a);
        }

        return mealAdditionList;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_takeaway_addition, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_next){
            // Send the Additions to the host activity
            ArrayList<String> additionList = new ArrayList<String>();
            for(AdditionModel am : listModel) {
                if(am.isSelected()) {
                    additionList.add(am.getAdditionID());
                }
            }
            mCallback.onNextClicked(
                    restaurant_id,
                    menucategory_id,
                    meal_id,
                    additionList);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /////////////////////////////////////////////////////////////////////////////////////

    /*
    public static AdditionFragment newInstance(String param1, String param2) {
        AdditionFragment fragment = new AdditionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    */
}
