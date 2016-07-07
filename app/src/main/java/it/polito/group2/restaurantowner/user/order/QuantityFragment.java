package it.polito.group2.restaurantowner.user.order;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import it.polito.group2.restaurantowner.R;

public class QuantityFragment extends Fragment {

    private OnActionListener mCallback;
    private int quantity = 1;

    public QuantityFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle(getActivity().getResources().getString(R.string.user_order_info_title));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_order_fragment_quantity, container, false);
        NumberPicker qty = (NumberPicker)view.findViewById(R.id.meal_quantity);
        qty.setMinValue(1);
        qty.setMaxValue(30);
        qty.setWrapSelectorWheel(false);
        qty.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                quantity = newVal;
            }
        });
        Button addToCart = (Button) view.findViewById(R.id.add_cart);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onQuantitySelected(quantity);
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnActionListener) {
            mCallback = (OnActionListener) context;
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

    public interface OnActionListener {
        void onQuantitySelected(Integer quantity);
        void onCartClicked();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.user_order_fragment_quantity_menu, menu);
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

}
