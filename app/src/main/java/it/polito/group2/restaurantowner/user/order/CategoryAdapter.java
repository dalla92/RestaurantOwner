package it.polito.group2.restaurantowner.user.order;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.Offer;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final ArrayList<String> categoryList;
    private final Offer offer;
    private final CategoryFragment fragment;

    public CategoryAdapter(ArrayList<String> list, Offer offer, CategoryFragment fragment) {
        this.categoryList = list;
        this.offer = offer;
        this.fragment = fragment;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryName;
        public ImageView offerActive;
        public LinearLayout item;

        public CategoryViewHolder(View view){
            super(view);
            categoryName = (TextView) itemView.findViewById(R.id.category_name);
            offerActive = (ImageView) itemView.findViewById(R.id.offer_active);
            item = (LinearLayout) itemView.findViewById(R.id.category_item);
        }
    }

    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_order_fragment_category_item, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CategoryAdapter.CategoryViewHolder holder, final int position) {
        holder.categoryName.setText(categoryList.get(position));
        if(inOffer(categoryList.get(position))) {
            holder.offerActive.setVisibility(View.VISIBLE);
        } else {
            holder.offerActive.setVisibility(View.GONE);
        }
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.onCategorySelected(categoryList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    private boolean inOffer(String category) {
        if(offer != null) {
            return offer.isCategoryInOffer(category);
        }
        return false;
    }

}