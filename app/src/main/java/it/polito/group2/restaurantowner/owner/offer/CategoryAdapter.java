package it.polito.group2.restaurantowner.owner.offer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.Offer;

/**
 * Created by Filippo on 01/06/2016.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final ArrayList<String> categoryList;
    private final Context context;
    private final Offer offer;

    public CategoryAdapter(Context context, ArrayList<String> list, Offer offer) {
        this.context = context;
        this.categoryList = list;
        this.offer = offer;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public CheckBox checkbox;

        public CategoryViewHolder(View view) {
            super(view);
            name = (TextView) itemView.findViewById(R.id.category_name);
            checkbox = (CheckBox) itemView.findViewById(R.id.check);
        }
    }

    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.owner_offer_fragment_category_item, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CategoryAdapter.CategoryViewHolder holder, final int position) {
        holder.name.setText(categoryList.get(position));
        holder.checkbox.setChecked(offer.isInCategoryList(categoryList.get(position)));
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    offer.addCategoryInOffer(categoryList.get(position));
                } else {
                    offer.delCategoryInOffer(categoryList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

}