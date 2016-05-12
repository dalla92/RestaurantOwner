package it.polito.group2.restaurantowner.user.order;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.Meal;
import it.polito.group2.restaurantowner.data.Order;
import it.polito.group2.restaurantowner.data.OrderMeal;
import it.polito.group2.restaurantowner.data.OrderMealAddition;

public class CartFragment extends ListFragment {

    public static final String ORDER = "order";
    private Order order;

    private ArrayList<MealModel> modelList;
    private OnActionListener mCallback;

    public CartFragment() {}

    public static CartFragment newInstance(Order order) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        args.putSerializable(ORDER, order);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            order = (Order)getArguments().getSerializable(ORDER);
        }
        modelList = getModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_cart, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //TODO implementa il modello
        MealAdapter adapter = new MealAdapter(getActivity(), modelList);
        setListAdapter(adapter);
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
        public void onConfirmOrderClicked();
        public void onContinueOrderClicked();
    }

    private ArrayList<MealModel> getModel() {
        ArrayList<MealModel> list = new ArrayList<MealModel>();
        for(OrderMeal m : order.getMealList()) {
            MealModel model = new MealModel(m.getMeal().getMealId(), m.getMeal().getMeal_name(), m.getMeal());
            for(OrderMealAddition a : m.getAdditionList()) {
                AdditionModel am = new AdditionModel(a.getAddition().getAddition_id(),
                        a.getAddition().getName(), true, a.getAddition());
                model.getAdditionModel().add(am);
            }
            list.add(model);
        }
        return list;
    }
}
