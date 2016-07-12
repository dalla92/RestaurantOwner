package it.polito.group2.restaurantowner.owner.offer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.Meal;
import it.polito.group2.restaurantowner.firebasedata.Offer;

public class MealFragment extends Fragment {

    public static final String LIST = "mealList";
    public static final String OFFER = "offer";
    private ArrayList<Meal> mealList;
    private Offer offer;
    private OnActionListener mCallback;

    public MealFragment() {}

    public static MealFragment newInstance(ArrayList<Meal> list, Offer offer) {
        MealFragment fragment = new MealFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(LIST, list);
        args.putSerializable(OFFER, offer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mealList = getArguments().getParcelableArrayList(LIST);
            offer = (Offer) getArguments().getSerializable(OFFER);
        }
        setHasOptionsMenu(true);
        Log.d("FILIPPO", "P4");
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getActivity().
                    getResources().getString(R.string.owner_offer_fragment_meal_title));
        } catch (Exception e) {
            Log.d("FILIPPO", e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.owner_offer_fragment_meal, container, false);
        Log.d("FILIPPO", "P5");
        setMealList(view);
        Log.d("FILIPPO", "P6");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d("FILIPPO", "P7");
        super.onActivityCreated(savedInstanceState);
        Log.d("FILIPPO", "P8");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("FILIPPO", "P2");
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
        public void onSaveListClicked(Offer offer);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.owner_offer_fragment_meal_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_save){
            Log.d("FILIPPO", "P1");
            mCallback.onSaveListClicked(offer);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setMealList(View view) {
        TextView empty = (TextView) view.findViewById(R.id.empty_lable);
        if(mealList.size() > 0) {
            empty.setVisibility(View.GONE);
            final RecyclerView list = (RecyclerView) view.findViewById(R.id.meal_list);
            assert list != null;
            list.setLayoutManager(new LinearLayoutManager(getContext()));
            list.setNestedScrollingEnabled(false);
            MealAdapter adapter = new MealAdapter(getContext(), mealList, offer);
            list.setAdapter(adapter);
        } else {
            empty.setVisibility(View.VISIBLE);
        }
    }
}
