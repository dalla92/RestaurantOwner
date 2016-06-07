package it.polito.group2.restaurantowner.user.restaurant_list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.JSONUtil;
import it.polito.group2.restaurantowner.data.Meal;
import it.polito.group2.restaurantowner.data.OpenTime;
import it.polito.group2.restaurantowner.data.TableReservation;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;

/**
 * Created by Daniele on 05/04/2016.
 */
public class UserRestaurantPreviewAdapter extends RecyclerView.Adapter<UserRestaurantPreviewAdapter.ViewHolder>{
    private List<Restaurant> mDataset;
    private static Context mContext;
    private static float PRICE_BOUNDARY_1 = 5;
    private static float PRICE_BOUNDARY_2 = 10;

    protected int filter(String category, String time, boolean price1, boolean price2, boolean price3, boolean price4) {
                //filter by category
                List<Restaurant> nResList = new ArrayList<Restaurant>();
            if(category.equals("0"))
                nResList = mDataset;

                for (Restaurant r : mDataset) {
                    if (r.getRestaurant_category().equals(category))
                        nResList.add(r);
                }
            //filter by time
            List<Restaurant> n2ResList = new ArrayList<Restaurant>();
            if(time!=null) {
                try {
                    String timeFormat = new String("HH:mm");
                    SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, Locale.US);
                    Date filterTime = sdf.parse(time);
                    ArrayList<OpenTime> otList = JSONUtil.readJSONOpenTimeList(mContext);
                    for(Restaurant r: nResList) {
                        Calendar calendar = Calendar.getInstance();
                        int day = calendar.get(Calendar.DAY_OF_WEEK);
                        for (OpenTime ot : otList) {
                            if (ot.getRestaurantId().equals(r.getRestaurant_id()) && ot.getDayOfWeek() == day) {
                                Date openTime = sdf.parse(ot.getOpenHour());
                                Date closeTime = sdf.parse(ot.getCloseHour());
                                if (openTime.before(filterTime) && closeTime.after(filterTime)) {
                                    n2ResList.add(r);
                                    break;
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            else
                n2ResList = nResList;
            //filter by price
            List<Restaurant> n3ResList = new ArrayList<Restaurant>();
            for(Restaurant r : n2ResList){
                if(price1 && r.getRestaurant_price_range()==1) {
                    n3ResList.add(r);
                    continue;
                }
                if(price2 && r.getRestaurant_price_range()==2) {
                    n3ResList.add(r);
                    continue;
                }
                if(price3 && r.getRestaurant_price_range()==3) {
                    n3ResList.add(r);
                    continue;
                }
                if(price4 && r.getRestaurant_price_range()==4) {
                    n3ResList.add(r);
                    continue;
                }
            }

            int size = mDataset.size();
            mDataset = n3ResList;
            notifyDataSetChanged();
            notifyItemRangeChanged(0,size);
            return 1;
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
        private ProgressBar progressBar;
        public Restaurant current;
        public int position;

        public ViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.imageViewRes);
            resName = (TextView) v.findViewById(R.id.textViewName);
            rating = (TextView) v.findViewById(R.id.textViewRating);
            reservationNumber = (TextView) v.findViewById(R.id.textViewReservationNumber);
            distance = (TextView) v.findViewById(R.id.textViewDistance);
            progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        }

        public void setData(Restaurant restaurant, int position){

            progressBar.setVisibility(View.VISIBLE);
            if(restaurant.getRestaurant_photo_firebase_URL() == null || restaurant.getRestaurant_photo_firebase_URL().equals("")) {
                Glide.with(mContext).load(R.drawable.no_image).into(this.image);
                progressBar.setVisibility(View.GONE);
            }
            else
                Glide
                        .with(mContext)
                        .load(restaurant.getRestaurant_photo_firebase_URL())
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(this.image);

            /*
            SharedPreferences userDetails = mContext.getSharedPreferences("userdetails", mContext.MODE_PRIVATE);
            if(userDetails != null) {
                if (userDetails.getString(obj.getPhotoUri(), null) != null) {
                    Uri photouri = Uri.parse(userDetails.getString(obj.getPhotoUri(), null));
                    if (photouri != null)
                        this.image.setImageURI(photouri);
                }
            }*/

            //TODO remember to take out, just for testing purpose
            //restaurant.setPriceRange(String.valueOf(calculate_range(restaurant)));
            restaurant.setRestaurant_price_range(2);

            this.resName.setText(restaurant.getRestaurant_name());
            this.rating.setText(String.valueOf(restaurant.getRestaurant_rating()));

            //TODO remember to take out, just for testing purpose2
            /*
            String seats = String.valueOf(calculate_reservations_number(restaurant)) + "/" + restaurant.getTableNum();
            //this.reservationNumber.setText(seats);
            */
            this.reservationNumber.setText("20/100");

            //TODO calculate distance form current location
            this.distance.setText("23 KM");
            this.position = position;
            this.current = restaurant;
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

    public static int calculate_reservations_number(Restaurant r) {
        Calendar c = Calendar.getInstance();
        ArrayList<TableReservation> today_reservations = null;
        try{
            today_reservations = JSONUtil.readJSONTableResList(mContext, c, r.getRestaurant_id());
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        return today_reservations.size();
    }

    public static int calculate_range(Restaurant r){
        ArrayList<Meal> meals = null;
        try{
            meals = JSONUtil.readJSONMeList(mContext, r.getRestaurant_id());
        }
        catch(JSONException e){
            e.printStackTrace();
        }

        int meals_number = meals.size();

        if(meals_number==0)
            return 1;

        double meals_price_sum=0;
        for(Meal m : meals){
            meals_price_sum += m.getMeal_price();
        }

        double ratio=0;

        if(meals_price_sum==0)
            return 1;

        ratio = meals_price_sum/meals_number;

        if(ratio <= PRICE_BOUNDARY_1)
            return 1;
        if(ratio > PRICE_BOUNDARY_1 && ratio < PRICE_BOUNDARY_2)
            return 2;

        return 3;
    }


}
