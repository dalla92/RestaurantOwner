package it.polito.group2.restaurantowner.user.my_favourites;

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
import it.polito.group2.restaurantowner.firebasedata.RestaurantPreview;

/**
 * Created by Alessio on 12/05/2016.
 */
public class FavouriteAdapter extends ArrayAdapter<RestaurantPreview> {

    private ArrayList<RestaurantPreview> bookmarked_restaurants;
    private Context context;

    public FavouriteAdapter(Context context, int resource, ArrayList<RestaurantPreview> bookmarked_restaurants) {
        super(context, resource, bookmarked_restaurants);
        this.bookmarked_restaurants = bookmarked_restaurants;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.favourite_layout, null);
        }

        TextView name = (TextView) convertView.findViewById(R.id.restaurant_name);
        ImageView image = (ImageView) convertView.findViewById(R.id.restaurant_image);

        RestaurantPreview p = bookmarked_restaurants.get(position);
        name.setText(p.getRestaurant_name());

        String pictureURL = p.getRestaurant_cover_firebase_URL();
        if (pictureURL !=null && !pictureURL.trim().equals("")) {
            Glide.with(context)
                    .load(p.getRestaurant_cover_firebase_URL()) //"http://nuuneoi.com/uploads/source/playstore/cover.jpg"
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(image);
        }

        return convertView;
    }

}