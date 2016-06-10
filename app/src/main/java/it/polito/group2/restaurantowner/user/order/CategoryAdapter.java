package it.polito.group2.restaurantowner.user.order;

import android.content.Context;
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

/**
 * Created by Filippo on 10/05/2016.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final ArrayList<String> categoryList;
    private final Offer offer;
    private final Context context;

    public CategoryAdapter(Context context, ArrayList<String> list, Offer offer) {
        this.context = context;
        this.categoryList = list;
        this.offer = offer;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView categoryName;
        public ImageView offerActive;

        public CategoryViewHolder(View view){
            super(view);
            categoryName = (TextView) itemView.findViewById(R.id.category_name);
            offerActive = (ImageView) itemView.findViewById(R.id.offer_active);
        }

        @Override
        public void onClick(View v) {
            //categoryList.get(this.getLayoutPosition())
        }
    }

    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_order_fragment_category_item, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CategoryAdapter.CategoryViewHolder holder, final int position) {
        holder.categoryName.setText(categoryList.get(position));
        if(inOffer(categoryList.get(position))) {
            holder.offerActive.setVisibility(View.VISIBLE);
        } else {
            holder.offerActive.setVisibility(View.GONE);
        }
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