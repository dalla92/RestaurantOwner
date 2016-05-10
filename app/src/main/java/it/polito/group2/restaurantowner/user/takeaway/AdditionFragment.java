package it.polito.group2.restaurantowner.user.takeaway;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.Addition;
import it.polito.group2.restaurantowner.data.Meal;

public class AdditionFragment extends ListFragment {

    //parameter's names
    public static final String RESTAURANT_ID = "restaurant_id";
    public static final String MENUCATEGORY_ID = "menucategory_id";
    public static final String MEAL_ID = "meal_id";

    //paramter's values
    private String restaurant_id;
    private String menucategory_id;
    private String meal_id;

    private ArrayList<Addition> listAddition;

    public AdditionFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            restaurant_id = getArguments().getString(RESTAURANT_ID);
            menucategory_id = getArguments().getString(MENUCATEGORY_ID);
            meal_id = getArguments().getString(MEAL_ID);
        }
        listAddition = getAdditionList(restaurant_id, menucategory_id, meal_id);
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

        AdditionArrayAdapter adapter = new AdditionArrayAdapter(getActivity(), getModel(listAddition));
        setListAdapter(adapter);

    }

    /*
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) l.getItemAtPosition(position);
        //listMeal.get(position).getCategoryID()
    }
    */

    private ArrayList<AdditionModel> getModel(ArrayList<Addition> objList) {
        ArrayList<AdditionModel> list = new ArrayList<AdditionModel>();
        for (int i = 0; i < objList.size(); ++i) {
            list.add(new AdditionModel(objList.get(i).getName()));
        }
        return list;
    }

    private ArrayList<Addition> getAdditionList(String restaurant_id, String menucategory_id, String meal_id) {
        ArrayList<Addition> additionList = new ArrayList<Addition>();

        for(int i=0; i<10; i++) {
            Addition a = new Addition();
            a.setName("Addition " + i);
            a.setAddition_id("addID" + i);
            additionList.add(a);
        }

        return additionList;
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
            //TODO farlo
            Toast.makeText(getContext(), "fatto", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /////////////////////////////////////////////////////////////////////////////////////

    /*

    private OnFragmentInteractionListener mListener;

    public AdditionFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static AdditionFragment newInstance(String param1, String param2) {
        AdditionFragment fragment = new AdditionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_takeaway_addition, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    */
}
