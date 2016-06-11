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
import it.polito.group2.restaurantowner.firebasedata.MealAddition;

public class AdditionFragment extends Fragment {

    public static final String LIST = "additionList";
    private ArrayList<MealAddition> additionList;
    private ArrayList<MealAddition> selectedList = new ArrayList<>();

    private OnActionListener mCallback;

    public AdditionFragment() {}

    public static AdditionFragment newInstance(ArrayList<MealAddition> list) {
        AdditionFragment fragment = new AdditionFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(LIST, list);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            additionList = getArguments().getParcelableArrayList(LIST);
        }
        setHasOptionsMenu(true);
        getActivity().setTitle(getActivity().getResources().getString(R.string.user_order_addition_title));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_order_fragment_addition, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setAdditionList(getView());
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

    public boolean onAdditionSelected(MealAddition addition) {
        if(selectedList.size() > 0) {
            for(int i = 0; i<selectedList.size(); i++) {
                if(selectedList.get(i).getMeal_addition_id().equals(addition.getMeal_addition_id())) {
                    selectedList.remove(i);
                    return false;
                }
            }
        }

        selectedList.add(addition);
        return true;
    }

    public interface OnActionListener {
        void onAdditionsSelected(ArrayList<MealAddition> selectedAdditions);
        void onCartClicked();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.user_order_fragment_addition_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_next){
            mCallback.onAdditionsSelected(selectedList);
            return true;
        }
        if(id == R.id.goto_cart){
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

    private void setAdditionList(View view) {
        final RecyclerView addList = (RecyclerView) view.findViewById(R.id.addition_list);
        assert addList != null;
        addList.setLayoutManager(new LinearLayoutManager(getContext()));
        addList.setNestedScrollingEnabled(false);
        AdditionAdapter adapter = new AdditionAdapter(additionList, AdditionFragment.this);
        addList.setAdapter(adapter);
    }

}
