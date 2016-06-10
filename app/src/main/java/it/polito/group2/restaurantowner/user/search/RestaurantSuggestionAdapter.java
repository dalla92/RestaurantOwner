package it.polito.group2.restaurantowner.user.search;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.user.restaurant_page.UserRestaurantActivity;

/**
 * Created by TheChuck on 30/05/2016.
 */
public class RestaurantSuggestionAdapter extends RecyclerView.Adapter<RestaurantSuggestionAdapter.RestaurantSearchViewHolder> {

    private ArrayList<String> names;
    private HashMap<String, HashMap<String, Boolean>> namesAndId;
    private Activity activity;

    public RestaurantSuggestionAdapter(HashMap<String, HashMap<String, Boolean>> namesAndId, Activity activity) {
        this.names = new ArrayList<>();
        this.names.addAll(namesAndId.keySet());
        this.namesAndId = namesAndId;
        this.activity = activity;
    }


    public class RestaurantSearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView searchSuggestion;

        public RestaurantSearchViewHolder(View view){
            super(view);
            searchSuggestion = (TextView) itemView.findViewById(R.id.search_suggestion);
            searchSuggestion.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String key = names.get(this.getLayoutPosition());
            HashMap<String, Boolean> mapIds = namesAndId.get(key);

            ArrayList<String> restaurantsIDs = new ArrayList<>();
            restaurantsIDs.addAll(mapIds.keySet());

            if(restaurantsIDs.size() == 1){
                Intent intent = new Intent(activity, UserRestaurantActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("restaurant_id", restaurantsIDs.get(0));
                activity.startActivity(intent);
            }
            else {
                Intent intent = new Intent();
                intent.putExtra("restaurant_list", restaurantsIDs);
                intent.putExtra("searchedText", key);
                activity.setResult(Activity.RESULT_OK, intent);
                activity.finish();
            }


        }
    }

    @Override
    public RestaurantSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_suggestion_item, parent, false);
        return new RestaurantSearchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RestaurantSearchViewHolder holder, int position) {
        holder.searchSuggestion.setText(names.get(position));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public void setData(HashMap<String, HashMap<String, Boolean>> namesAndId){
        this.namesAndId = namesAndId;
        this.names = new ArrayList<>();
        this.names.addAll(namesAndId.keySet());
        notifyDataSetChanged();
    }

}
