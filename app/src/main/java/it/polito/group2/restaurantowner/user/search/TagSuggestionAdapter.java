package it.polito.group2.restaurantowner.user.search;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.user.restaurant_page.UserRestaurantActivity;

/**
 * Created by TheChuck on 06/06/2016.
 */
public class TagSuggestionAdapter extends RecyclerView.Adapter<TagSuggestionAdapter.TagSearchViewHolder> {

    private ArrayList<String> names;
    private HashMap<String, HashMap<String, Boolean>> namesAndId;
    private Activity activity;

    public TagSuggestionAdapter(HashMap<String, HashMap<String, Boolean>> namesAndId, Activity activity) {
        this.names = new ArrayList<>();
        this.names.addAll(namesAndId.keySet());
        this.namesAndId = namesAndId;
        this.activity = activity;
    }


    public class TagSearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView searchSuggestion;

        public TagSearchViewHolder(View view){
            super(view);
            searchSuggestion = (TextView) itemView.findViewById(R.id.search_suggestion);
            searchSuggestion.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String key = names.get(this.getLayoutPosition());
            ArrayList<String> restaurantIDs = new ArrayList<>();
            HashMap<String, Boolean> mapIds = namesAndId.get(key);
            restaurantIDs.addAll(mapIds.keySet());

            for(String s: restaurantIDs)
                Log.d("prova", s);

            //TODO update the list of restaurant in UserRestaurantList
            /*Intent intent = new Intent(activity, UserRestaurantActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("restaurant_id", restaurantId);
            activity.startActivity(intent);*/
        }
    }

    @Override
    public TagSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_suggestion_item, parent, false);
        return new TagSearchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TagSearchViewHolder holder, int position) {
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

