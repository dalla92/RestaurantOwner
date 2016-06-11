package it.polito.group2.restaurantowner.user.order;

import android.content.Context;
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
import it.polito.group2.restaurantowner.firebasedata.Offer;

public class CategoryFragment extends Fragment {

    private static final String LIST = "categoryList";
    private static final String OFFER = "offer";
    private ArrayList<String> categoryList;
    private Offer offer;

    private OnActionListener mCallback;

    public CategoryFragment() {}

    public static CategoryFragment newInstance(ArrayList<String> catList, Offer offer) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(LIST, catList);
        args.putSerializable(OFFER, offer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryList = getArguments().getStringArrayList(LIST);
            offer = (Offer) getArguments().getSerializable(OFFER);
        }
        setHasOptionsMenu(true);
        getActivity().setTitle(getActivity().getResources().getString(R.string.user_order_category_title));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_order_fragment_category, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setCategoryList(getView());
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

    public void onCategorySelected(String category) {
        mCallback.onCategorySelected(category);
    }

    public interface OnActionListener {
        void onCategorySelected(String categoryName);
        void onCartClicked();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.user_order_fragment_category_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.goto_cart){
            mCallback.onCartClicked();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setCategoryList(View view) {
        final RecyclerView catList = (RecyclerView) view.findViewById(R.id.category_list);
        assert catList != null;
        catList.setLayoutManager(new LinearLayoutManager(getContext()));
        catList.setNestedScrollingEnabled(false);
        CategoryAdapter adapter = new CategoryAdapter(categoryList, offer, CategoryFragment.this);
        catList.setAdapter(adapter);
    }

}
