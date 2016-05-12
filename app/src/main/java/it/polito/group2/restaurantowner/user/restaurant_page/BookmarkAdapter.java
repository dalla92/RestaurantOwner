package it.polito.group2.restaurantowner.user.restaurant_page;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.Bookmark;
import it.polito.group2.restaurantowner.data.Restaurant;

/**
 * Created by Alessio on 12/05/2016.
 */
public class BookmarkAdapter extends ArrayAdapter<Restaurant> {

    private ArrayList<Restaurant> bookmarked_restaurants;

    public BookmarkAdapter(Context context, int resource, ArrayList<Restaurant> bookmarked_restaurants) {
        super(context, resource, bookmarked_restaurants);
        this.bookmarked_restaurants = bookmarked_restaurants;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.bookmark_layout, null);
        }

        Restaurant p = bookmarked_restaurants.get(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.restaurant_name);
            ImageView tt2 = (ImageView) v.findViewById(R.id.restaurant_image);
            if (tt1 != null && p.getName()!=null) {
                tt1.setText(p.getName());
            }
            if (tt2 != null && p.getPhotoUri()!=null) {
                tt2.setImageURI(Uri.parse(p.getPhotoUri()));
            }
        }

        return v;
    }

}