package it.polito.group2.restaurantowner.user.restaurant_page;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;

/**
 * Created by Alessio on 12/05/2016.
 */
public class FavouriteAdapter extends ArrayAdapter<Restaurant> {

    private ArrayList<Restaurant> bookmarked_restaurants;
    private Context context;

    public FavouriteAdapter(Context context, int resource, ArrayList<Restaurant> bookmarked_restaurants) {
        super(context, resource, bookmarked_restaurants);
        this.bookmarked_restaurants = bookmarked_restaurants;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.favourite_layout, null);
        }

        Restaurant p = bookmarked_restaurants.get(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.restaurant_name);
            ImageView tt2 = (ImageView) v.findViewById(R.id.restaurant_image);
            if (tt1 != null && p.getRestaurant_name()!=null) {
                tt1.setText(p.getRestaurant_name());
            }
            if (tt2 != null && p.getRestaurant_photo_firebase_URL()!=null) {
                Glide.with(context)
                        .load(p.getRestaurant_photo_firebase_URL()) //"http://nuuneoi.com/uploads/source/playstore/cover.jpg"
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(tt2);
            }
        }

        return v;
    }

}