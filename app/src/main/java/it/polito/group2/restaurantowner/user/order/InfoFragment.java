package it.polito.group2.restaurantowner.user.order;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;

public class InfoFragment extends Fragment {
    //parameter's names
    public static final String RESTAURANT_ID = "restaurant_id";
    public static final String MENUCATEGORY_ID = "menucategory_id";
    public static final String MEAL_ID = "meal_id";
    public static final String LIST_ADDITION_ID = "list_addition_id";

    //paramter's values
    private String restaurant_id;
    private String menucategory_id;
    private String meal_id;
    private ArrayList<String> list_addition_id;

    private OnAddClickedListener mCallback;

    public InfoFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            restaurant_id = getArguments().getString(RESTAURANT_ID);
            menucategory_id = getArguments().getString(MENUCATEGORY_ID);
            meal_id = getArguments().getString(MEAL_ID);
            list_addition_id = getArguments().getStringArrayList(LIST_ADDITION_ID);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_info, container, false);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddClickedListener) {
            mCallback = (OnAddClickedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAddClickedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public interface OnAddClickedListener {
        public void onAddClicked(String restaurantID, String menuCategoryID,
                                  String mealID, ArrayList<String> listAdditionID);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_order_addition, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_add){
            // Send the Additions to the host activity
            mCallback.onAddClicked(
                    restaurant_id,
                    menucategory_id,
                    meal_id,
                    list_addition_id);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    public static InfoFragment newInstance(String param1, String param2) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    */
}
