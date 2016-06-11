package it.polito.group2.restaurantowner.user.order;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.OnBackUtil;
import it.polito.group2.restaurantowner.firebasedata.Meal;
import it.polito.group2.restaurantowner.firebasedata.Offer;

public class MealFragment extends Fragment {

    public static final String LIST = "mealList";
    private static final String OFFER = "offer";
    private ArrayList<Meal> mealList;
    private Offer offer;

    private OnActionListener mCallback;

    public MealFragment() {}

    public static MealFragment newInstance(ArrayList<Meal> mList, Offer offer) {
        MealFragment fragment = new MealFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(LIST, mList);
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
        getActivity().setTitle(getActivity().getResources().getString(R.string.user_order_meal_title));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_order_fragment_meal, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setMealList(getView());
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

    public void onMealSelected(Meal meal) {
        mCallback.onMealSelected(meal);
    }

    public interface OnActionListener {
        void onMealSelected(Meal meal);
        void onCartClicked();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.user_order_fragment_meal_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.goto_cart){
            new AlertDialog.Builder(getContext())
                    .setTitle(getResources().getString(R.string.user_order_alert_cart_title))
                    .setMessage(getResources().getString(R.string.user_order_alert_cart_message))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            mCallback.onCartClicked();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setMealList(View view) {
        final RecyclerView mList = (RecyclerView) view.findViewById(R.id.meal_list);
        assert mList != null;
        mList.setLayoutManager(new LinearLayoutManager(getContext()));
        mList.setNestedScrollingEnabled(false);
        MealAdapter adapter = new MealAdapter(mealList, offer, MealFragment.this);
        mList.setAdapter(adapter);
    }

}
