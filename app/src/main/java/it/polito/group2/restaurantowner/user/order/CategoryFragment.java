package it.polito.group2.restaurantowner.user.order;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.MealAddition;
import it.polito.group2.restaurantowner.data.MenuCategory;

public class CategoryFragment extends Fragment {

    private static final String RESTAURANT = "restaurantID";
    private String restaurantID;

    private ArrayList<CategoryModel> modelList;
    private OnActionListener mCallback;

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
        setHasOptionsMenu(true);
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
        setCategoryList();
    }

    //@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mCallback.onCategorySelected(modelList.get(position).getCategory());
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
        public void onCategorySelected(MenuCategory category);
        public void onCartClicked();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.order_fragment_category_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.goto_cart){
            mCallback.onCartClicked();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setCategoryList() {
        modelList = getModel(restaurantID);
        final RecyclerView categoryList = (RecyclerView) getView().findViewById(R.id.category_list);
        assert categoryList != null;
        categoryList.setLayoutManager(new LinearLayoutManager(getContext()));
        categoryList.setNestedScrollingEnabled(false);
        CategoryAdapter adapter = new CategoryAdapter(getContext(), modelList);
        categoryList.setAdapter(adapter);
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
