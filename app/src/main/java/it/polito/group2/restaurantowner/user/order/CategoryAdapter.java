package it.polito.group2.restaurantowner.user.order;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.polito.group2.restaurantowner.R;

/**
 * Created by Filippo on 10/05/2016.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final ArrayList<CategoryModel> modelList;
    private final Context context;

    public CategoryAdapter(Context context, ArrayList<CategoryModel> list) {
        this.context = context;
        this.modelList = list;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryName;

        public CategoryViewHolder(View view){
            super(view);
            categoryName = (TextView) itemView.findViewById(R.id.category_name);
        }
    }

    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_fragment_category_item, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CategoryAdapter.CategoryViewHolder holder, int position) {
        holder.categoryName.setText(modelList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }
}