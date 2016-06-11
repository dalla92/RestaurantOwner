package it.polito.group2.restaurantowner.user.order;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.Meal;
import it.polito.group2.restaurantowner.firebasedata.Offer;
import it.polito.group2.restaurantowner.firebasedata.Order;

public class CartFragment extends Fragment {

    public static final String ORDER = "order";
    private Order order;

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
        getActivity().setTitle(getActivity().getResources().getString(R.string.user_order_cart_title));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.user_order_fragment_cart, container, false);

        TextView orderPrice = (TextView) view.findViewById(R.id.order_price);
        orderPrice.setText(formatEuro(this.order.getOrder_price()));

        if(order.getOrder_notes() != null && order.getOrder_notes() != "") {
            EditText note = (EditText) view.findViewById(R.id.ordernote);
            note.setText(order.getOrder_notes());
        }

        Button confirm_btn = (Button) view.findViewById(R.id.confirm_order);
        Button continue_btn = (Button) view.findViewById(R.id.continue_order);
        Button cancel_btn = (Button) view.findViewById(R.id.cancel_order);

        continue_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText note = (EditText) view.findViewById(R.id.ordernote);
                order.setOrder_notes(note.getText().toString());
                mCallback.onContinueOrderClicked(order);
            }
        });

        if(order.getOrder_meals().size() == 0) {
            confirm_btn.setVisibility(View.GONE);
        } else {
            confirm_btn.setVisibility(View.VISIBLE);
            confirm_btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(getContext().getResources().getString(R.string.user_order_confirm_title))
                            .setMessage(getContext().getResources().getString(R.string.user_order_confirm_message))
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    EditText note = (EditText) view.findViewById(R.id.ordernote);
                                    order.setOrder_notes(note.getText().toString());
                                    order.calendarToOrderDate(Calendar.getInstance());
                                    mCallback.onConfirmOrderClicked(order);
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
            });
        }

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle(getContext().getResources().getString(R.string.user_order_cancel_title))
                        .setMessage(getContext().getResources().getString(R.string.user_order_cancel_message))
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mCallback.onCancelOrderClicked();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setCartList(getView());
    }

    /*
    @Override
    public void onListItemClick(ListView l, View v, final int position, long id) {
        new AlertDialog.Builder(getContext())
                .setTitle(getContext().getResources().getString(R.string.user_order_cart_deltitle))
                .setMessage(getContext().getResources().getString(R.string.user_order_cart_delmessage))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mCallback.onMealDeleted(order, order.getOrder_meals().get(position));
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }
    */

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
        void onConfirmOrderClicked(Order order);
        void onContinueOrderClicked(Order order);
        void onCancelOrderClicked();
        void onMealDeleted(Order order, Meal meal);
    }

    private void setCartList(View view) {
        final RecyclerView list = (RecyclerView) view.findViewById(R.id.order_meal_list);
        assert list != null;
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        list.setNestedScrollingEnabled(false);
        CartMealAdapter adapter = new CartMealAdapter(getContext(), this.order.allMeals(), order.getOffer_applied());
        list.setAdapter(adapter);
    }

    private String formatEuro(double number) {
        return "Euro "+String.format("%10.2f", number);
    }
}
