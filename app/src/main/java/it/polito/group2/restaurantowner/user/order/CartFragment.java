package it.polito.group2.restaurantowner.user.order;

import android.app.Dialog;
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
import android.widget.LinearLayout;
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

        //TODO aggiustare la lista annidata
        //TODO fare spuntare fidelity point presi oppure lo sconto fidelity
        //TODO stampa lista vuota se non ci sono articoli

        TextView orderPrice = (TextView) view.findViewById(R.id.order_price);
        orderPrice.setText(formatEuro(this.order.getOrder_price()));

        Button confirm_btn = (Button) view.findViewById(R.id.confirm_order);
        Button continue_btn = (Button) view.findViewById(R.id.continue_order);
        Button cancel_btn = (Button) view.findViewById(R.id.cancel_order);
        LinearLayout order_detail = (LinearLayout) view.findViewById(R.id.order_detail);
        TextView empty_msg = (TextView) view.findViewById(R.id.order_empty);

        continue_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mCallback.onOrderContinue(order);
            }
        });

        if(order.getOrder_meals().size() == 0) {
            confirm_btn.setVisibility(View.GONE);
            order_detail.setVisibility(View.GONE);
            empty_msg.setVisibility(View.VISIBLE);
        } else {
            order_detail.setVisibility(View.VISIBLE);
            confirm_btn.setVisibility(View.VISIBLE);
            empty_msg.setVisibility(View.GONE);
            confirm_btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.user_order_fragment_cart_dialog_confirm);
                    dialog.setTitle(getResources().getString(R.string.user_order_fragment_cart_alert_orderconfirm_title));
                    final EditText note = (EditText) dialog.findViewById(R.id.ordernote);
                    Button confirm = (Button) dialog.findViewById(R.id.confirm);
                    Button cancel = (Button) dialog.findViewById(R.id.cancel);
                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            order.setOrder_notes(note.getText().toString());
                            order.calendarToOrderDate(Calendar.getInstance());
                            mCallback.onOrderConfirm(order);
                            dialog.dismiss();
                        }
                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
        }

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle(getContext().getResources().getString(R.string.user_order_fragment_cart_alert_ordercancel_title))
                        .setMessage(getContext().getResources().getString(R.string.user_order_fragment_cart_alert_ordercancel_message))
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mCallback.onOrderDelete();
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

    public void onMealDeleted(final String mealID) {
        new AlertDialog.Builder(getContext())
                .setTitle(getContext().getResources().getString(R.string.user_order_fragment_cart_alert_mealdel_title))
                .setMessage(getContext().getResources().getString(R.string.user_order_fragment_cart_alert_mealdel_message))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        order.getOrder_meals().remove(mealID);
                        mCallback.onOrderChange(order);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void onAdditionDeleted(final String mealID, final String addID) {
        new AlertDialog.Builder(getContext())
                .setTitle(getContext().getResources().getString(R.string.user_order_fragment_cart_alert_adddel_title))
                .setMessage(getContext().getResources().getString(R.string.user_order_fragment_cart_alert_adddel_message))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        order.getOrder_meals().get(mealID).getMeal_additions().remove(addID);
                        mCallback.onOrderChange(order);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void onQuantityChanged(String mealID, int quantity) {
        this.order.getOrder_meals().get(mealID).setMeal_quantity(quantity);
        mCallback.onOrderChange(this.order);
    }

    public interface OnActionListener {
        void onOrderConfirm(Order order);
        void onOrderContinue(Order order);
        void onOrderDelete();
        void onOrderChange(Order order);
    }

    private void setCartList(View view) {
        final RecyclerView mList = (RecyclerView) view.findViewById(R.id.order_meal_list);
        assert mList != null;
        mList.setLayoutManager(new LinearLayoutManager(getContext()));
        mList.setNestedScrollingEnabled(false);
        CartMealAdapter adapter = new CartMealAdapter(getContext(), this.order.allMeals(), order.getOffer_applied(), CartFragment.this);
        mList.setAdapter(adapter);
    }

    private String formatEuro(double number) {
        return "Euro "+String.format("%2.2f", number);
    }
}
