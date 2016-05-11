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
import android.widget.EditText;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;

public class InfoFragment extends Fragment {

    private OnAddClickedListener mCallback;

    public InfoFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        public void onAddClicked(Integer quantity, String note);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_order_info, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_add){
            EditText qty = (EditText)getView().findViewById(R.id.meal_quantity);
            EditText note = (EditText)getView().findViewById(R.id.meal_note);
            mCallback.onAddClicked(Integer.parseInt(qty.getText().toString()),note.getText().toString());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
