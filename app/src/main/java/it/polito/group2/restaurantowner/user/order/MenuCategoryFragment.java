package it.polito.group2.restaurantowner.user.order;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.MenuCategory;

public class MenuCategoryFragment extends ListFragment {

    //parameter's names
    private static final String RESTAURANT_ID = "restaurant_id";

    //paramter's values
    private String restaurant_id;

    private ArrayList<MenuCategory> listMenuCategory;
    private OnMenuCategorySelectedListener mCallback;

    public MenuCategoryFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            restaurant_id = getArguments().getString(RESTAURANT_ID);
        }
        listMenuCategory = getMenuCategoryList(restaurant_id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_menucategory, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MenuCategoryArrayAdapter adapter = new MenuCategoryArrayAdapter(getActivity(),
                android.R.layout.simple_list_item_1, getItemList(listMenuCategory));
        setListAdapter(adapter);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Send the MenuCategoryID to the host activity
        mCallback.onMenuCategorySelected(restaurant_id, listMenuCategory.get(position).getCategoryID());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMenuCategorySelectedListener) {
            mCallback = (OnMenuCategorySelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMenuCategorySelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public interface OnMenuCategorySelectedListener {
        public void onMenuCategorySelected(String restaurantID, String menuCategoryID);
    }

    /////////////////////////////////////////////////////////////////////////////////////
    // GETTING DATAS
    private ArrayList<String> getItemList(ArrayList<MenuCategory> objList) {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < objList.size(); ++i) {
            list.add(objList.get(i).getName());
        }
        return list;
    }

    private ArrayList<MenuCategory> getMenuCategoryList(String restaurant_id) {
        ArrayList<MenuCategory> menuCatList = new ArrayList<MenuCategory>();

        for(int i=0; i<10; i++) {
            MenuCategory mc = new MenuCategory("id"+i, "category "+i, "rest0");
            menuCatList.add(mc);
        }

        return menuCatList;
    }

    /////////////////////////////////////////////////////////////////////////////////

//


/*
    public static MenuCategoryFragment newInstance(String res_id) {
        MenuCategoryFragment fragment = new MenuCategoryFragment();
        Bundle args = new Bundle();
        args.putString(RESTAURANT_ID, res_id);
        fragment.setArguments(args);
        return fragment;
    }
*/

}
