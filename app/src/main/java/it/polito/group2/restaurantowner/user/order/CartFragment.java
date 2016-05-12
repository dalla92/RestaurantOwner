package it.polito.group2.restaurantowner.user.order;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
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
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getActivity().
                    getResources().getString(R.string.order_cart_title));
        } catch (Exception e) {
            Log.d("FILIPPO", e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.order_fragment_cart, container, false);
        if(order.getNote() != null && order.getNote() != "") {
            EditText note = (EditText) view.findViewById(R.id.ordernote);
            note.setText(order.getNote());
        }

        Button confirm_btn = (Button) view.findViewById(R.id.confirm_order);
        Button continue_btn = (Button) view.findViewById(R.id.continue_order);

        continue_btn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                EditText note = (EditText) view.findViewById(R.id.ordernote);
                order.setNote(note.getText().toString());
                mCallback.onContinueOrderClicked(order);
            }
        });

        confirm_btn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                new AlertDialog.Builder(getContext())
                        .setTitle(getContext().getResources().getString(R.string.order_confirm_title))
                        .setMessage(getContext().getResources().getString(R.string.order_confirm_message))
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                EditText note = (EditText) view.findViewById(R.id.ordernote);
                                order.setNote(note.getText().toString());
                                mCallback.onConfirmOrderClicked(order);
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CartMealAdapter adapter = new CartMealAdapter(getActivity(), modelList);
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
        public void onConfirmOrderClicked(Order order);
        public void onContinueOrderClicked(Order order);
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
