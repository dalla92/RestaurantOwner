package it.polito.group2.restaurantowner.user.order;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.util.Calendar;

import it.polito.group2.restaurantowner.R;

public class InfoFragment extends Fragment {

    private OnActionListener mCallback;

    public InfoFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getActivity().
                    getResources().getString(R.string.order_info_title));
        } catch (Exception e) {
            Log.d("FILIPPO", e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_fragment_info, container, false);
        NumberPicker qty = (NumberPicker)view.findViewById(R.id.meal_quantity);
        qty.setMinValue(1);
        qty.setWrapSelectorWheel(false);
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
        public void onAddClicked(Integer quantity, String note);
        public void onCartClicked();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.order_fragment_info_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_add){

            NumberPicker qty = (NumberPicker)getView().findViewById(R.id.meal_quantity);
            EditText note = (EditText)getView().findViewById(R.id.meal_note);
            mCallback.onAddClicked(qty.getValue(), note.getText().toString());
            return true;
        }
        if(id == R.id.goto_cart){
            mCallback.onCartClicked();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
