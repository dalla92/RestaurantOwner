package it.polito.group2.restaurantowner.user.restaurant_list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;

/**
 * Created by TheChuck on 30/05/2016.
 */
public class RestaurantSearchAdapter extends RecyclerView.Adapter<RestaurantSearchAdapter.RestaurantSearchViewHolder> {

    private ArrayList<String> restaurantNames;

    public RestaurantSearchAdapter(ArrayList<String> restaurantNames) {
        this.restaurantNames = restaurantNames;
    }

    public class RestaurantSearchViewHolder extends RecyclerView.ViewHolder {
        public TextView searchSuggestion;

        public RestaurantSearchViewHolder(View view){
            super(view);
            searchSuggestion = (TextView) itemView.findViewById(R.id.search_suggestion);
        }
    }

    @Override
    public RestaurantSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_suggestion_item, parent, false);
        return new RestaurantSearchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RestaurantSearchViewHolder holder, int position) {
        holder.searchSuggestion.setText(restaurantNames.get(position));
    }

    @Override
    public int getItemCount() {
        return restaurantNames.size();
    }

    public void setData(ArrayList<String> restaurantNames){
        this.restaurantNames = restaurantNames;
        notifyDataSetChanged();
    }

}
