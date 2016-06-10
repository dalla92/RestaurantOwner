package it.polito.group2.restaurantowner.user.search;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.maps.model.LatLng;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import it.polito.group2.restaurantowner.R;

/**
 * Created by TheChuck on 09/06/2016.
 */
public class PlaceSuggestionAdapter extends RecyclerView.Adapter<PlaceSuggestionAdapter.PlaceSearchViewHolder> {

    private Activity activity;
    private List<AutocompletePrediction> prediction_list;

    public PlaceSuggestionAdapter(List<AutocompletePrediction> prediction_list, Activity activity) {
        this.prediction_list = prediction_list;
        this.activity = activity;
    }

    public class PlaceSearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView searchSuggestion;

        public PlaceSearchViewHolder(View view){
            super(view);
            searchSuggestion = (TextView) itemView.findViewById(R.id.search_suggestion);
            searchSuggestion.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String addressText = prediction_list.get(this.getLayoutPosition()).getFullText(null).toString();
            try {
                Address address = new Geocoder(activity, Locale.ITALY).getFromLocationName(addressText, 1).get(0);
                LatLng coordinates = new LatLng(address.getLatitude(), address.getLongitude());
                Intent intent = new Intent();
                intent.putExtra("coordinates", coordinates);
                intent.putExtra("searchedText", addressText);
                activity.setResult(Activity.RESULT_OK, intent);
                activity.finish();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /*Intent intent = new Intent(activity, UserRestaurantList.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("restaurant_list", restaurantIDs);
            activity.startActivity(intent);*/
        }
    }

    @Override
    public PlaceSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_suggestion_item, parent, false);
        return new PlaceSearchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PlaceSearchViewHolder holder, int position) {
        holder.searchSuggestion.setText(prediction_list.get(position).getFullText(null));
    }

    private String getFormattedAddress(AutocompletePrediction prediction) {
        String secondaryText = prediction.getSecondaryText(null).toString();
        int firstComma = secondaryText.indexOf(',');
        int lastComma =secondaryText.lastIndexOf(',');

        String address = prediction.getPrimaryText(null).toString().trim();
        String city = secondaryText.substring(0, firstComma);
        String country = secondaryText.substring(lastComma + 1).trim();
        Log.d("prova", address);
        Log.d("prova", city);
        Log.d("prova", country);
        Log.d("prova", "--------------------------------------");
        return address + ", " + city + ", " + country;
    }

    @Override
    public int getItemCount() {
        return prediction_list.size();
    }

    public void setData(List<AutocompletePrediction> prediction_list){
        this.prediction_list = prediction_list;
        notifyDataSetChanged();
    }
}
