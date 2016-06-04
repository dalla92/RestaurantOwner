package it.polito.group2.restaurantowner.owner.offer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;

/**
 * Created by Filippo on 04/06/2016.
 */
public class OfferCategoryAdapter extends RecyclerView.Adapter<OfferCategoryAdapter.OfferCategoryViewHolder> {

    private final ArrayList<String> categoryList;

    public OfferCategoryAdapter(ArrayList<String> list) {
        this.categoryList = list;
    }

    public class OfferCategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView name;

        public OfferCategoryViewHolder(View view) {
            super(view);
            name = (TextView) itemView.findViewById(R.id.category_name);
        }
    }

    @Override
    public OfferCategoryAdapter.OfferCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.owner_offer_fragment_offer_category_item, parent, false);
        return new OfferCategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final OfferCategoryAdapter.OfferCategoryViewHolder holder, int position) {
        holder.name.setText(categoryList.get(position));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

}