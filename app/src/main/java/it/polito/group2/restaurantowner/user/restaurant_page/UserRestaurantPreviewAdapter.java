package it.polito.group2.restaurantowner.user.restaurant_page;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.Restaurant;

/**
 * Created by Daniele on 05/04/2016.
 */
public class UserRestaurantPreviewAdapter extends RecyclerView.Adapter<UserRestaurantPreviewAdapter.ViewHolder> implements Filterable {
    private List<Restaurant> mDataset;
    private static Context mContext;
    private CategoryFilter categoryFilter;

    @Override
    public Filter getFilter() {
        if (categoryFilter == null)
            categoryFilter = new CategoryFilter(this);

        return categoryFilter;
    }

    public class CategoryFilter extends Filter{

        private UserRestaurantPreviewAdapter pAdapter;

        public CategoryFilter(UserRestaurantPreviewAdapter pAd){
            pAdapter = pAd;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = mDataset;
                results.count = mDataset.size();
            }
            else {
// We perform filtering operation
                List<Restaurant> nResList = new ArrayList<Restaurant>();

                for (Restaurant r : mDataset) {
                    if (r.getCategory().equals(constraint.toString()))
                        nResList.add(r);
                }

                results.values = nResList;
                results.count = nResList.size();

            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Now we have to inform the adapter about the new list filtered
                int size = mDataset.size();
                mDataset = (List<Restaurant>) results.values;
                notifyDataSetChanged();
            notifyItemRangeChanged(0,size);
        }
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder  {
        // each data item is just a string in this case
        public ImageView image;
        public TextView resName;
        public TextView rating;
        public TextView reservationNumber;
        public TextView distance;
        public Restaurant current;
        public int position;

        public ViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.imageViewRes);
            resName = (TextView) v.findViewById(R.id.textViewName);
            rating = (TextView) v.findViewById(R.id.textViewRating);
            reservationNumber = (TextView) v.findViewById(R.id.textViewReservationNumber);
            distance = (TextView) v.findViewById(R.id.textViewDistance);
        }
        public void setData(Restaurant obj, int position){
           /* TODO if(obj.getPhotoUri()!="") {
                Uri imageUri = Uri.parse(obj.getPhotoUri());
                InputStream imageStream = null;
                try {
                    imageStream = mContext.getContentResolver().openInputStream(imageUri);
                    this.image.setImageBitmap(BitmapFactory.decodeStream(imageStream));
                } catch (FileNotFoundException e) {
                    // Handle the error
                } finally {
                    if (imageStream != null) {
                        try {
                            imageStream.close();
                        } catch (IOException e) {
                            // Ignore the exception
                        }
                    }
                }
            }
            */
            SharedPreferences userDetails = mContext.getSharedPreferences("userdetails", mContext.MODE_PRIVATE);
            if(userDetails != null) {
                if (userDetails.getString(obj.getRestaurantId(), null) != null) {
                    Uri photouri = Uri.parse(userDetails.getString(obj.getRestaurantId(), null));
                    if (photouri != null)
                        this.image.setImageURI(photouri);
                }
            }
            this.resName.setText(obj.getName());
            this.rating.setText(obj.getRating());
            this.reservationNumber.setText(obj.getReservationNumber());
            //TODO calculate distance form current location
            this.distance.setText("23 KM");
            this.position = position;
            this.current = obj;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public UserRestaurantPreviewAdapter(List<Restaurant> myDataset, Context myContext) {
        mDataset = myDataset;
        mContext = myContext;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public UserRestaurantPreviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_restaurant_preview, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Restaurant current = mDataset.get(position);
        holder.setData(current, position);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
    public void addItem(int position, Restaurant res){
        mDataset.add(position,res);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, mDataset.size());
    }



}
