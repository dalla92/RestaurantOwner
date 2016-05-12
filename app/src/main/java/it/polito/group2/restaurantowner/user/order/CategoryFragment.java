package it.polito.group2.restaurantowner.user.order;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.MenuCategory;

public class CategoryFragment extends ListFragment {

    private static final String RESTAURANT = "restaurantID";
    private String restaurantID;

    private ArrayList<CategoryModel> modelList;
    private OnCategorySelectedListener mCallback;

    public CategoryFragment() {}

    public static CategoryFragment newInstance(String resID) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString(RESTAURANT, resID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            restaurantID = getArguments().getString(RESTAURANT);
        }
        modelList = getModel(restaurantID);
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getActivity().
                    getResources().getString(R.string.order_category_title));
        } catch (Exception e) {
            Log.d("FILIPPO", e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_fragment_category, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CategoryAdapter adapter = new CategoryAdapter(getActivity(), modelList);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mCallback.onCategorySelected(modelList.get(position).getCategory());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCategorySelectedListener) {
            mCallback = (OnCategorySelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCategorySelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public interface OnCategorySelectedListener {
        public void onCategorySelected(MenuCategory category);
    }

    private ArrayList<CategoryModel> getModel(String restaurantID) {
        ArrayList<CategoryModel> list = new ArrayList<CategoryModel>();
        ArrayList<MenuCategory> restaurantCategories = getMenuCategoryList(restaurantID);
        for(MenuCategory cat : restaurantCategories) {
            CategoryModel model = new CategoryModel(cat.getCategoryID(), cat.getName(), cat);
            list.add(model);
        }
        return list;
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //GENERATE DATAS
    //TODO change this method to get data from firebase
    private ArrayList<MenuCategory> getMenuCategoryList(String resID) {
        ArrayList<MenuCategory> menuCatList = new ArrayList<MenuCategory>();
        for(int i=0; i<10; i++) {
            MenuCategory mc = new MenuCategory("catID"+i, "Category "+i, "RestID0");
            mc.setRestaurantID(resID);
            menuCatList.add(mc);
        }
        return menuCatList;
    }

}
