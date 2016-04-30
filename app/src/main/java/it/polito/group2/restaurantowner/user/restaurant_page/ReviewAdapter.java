package it.polito.group2.restaurantowner.user.restaurant_page;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import it.polito.group2.restaurantowner.R;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private ArrayList<Review> reviews;
    private Context context;

    public ReviewAdapter(ArrayList<Review> reviews, Context context) {
        this.reviews = reviews;
        this.context = context;
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder{
        public TextView username, date, comment;
        public ImageView picture;
        public RatingBar stars;

        public ReviewViewHolder(View view){
            super(view);
            username = (TextView) itemView.findViewById(R.id.user_review_username);
            date = (TextView) itemView.findViewById(R.id.user_review_date);
            picture = (ImageView) itemView.findViewById(R.id.user_review_picture);
            stars = (RatingBar) itemView.findViewById(R.id.user_review_stars);
            comment = (TextView) itemView.findViewById(R.id.user_review_comment);
        }
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_review, parent, false);
        return new ReviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.username.setText(reviews.get(position).getUsername());
        holder.comment.setText(reviews.get(position).getComment());
        SimpleDateFormat format = new SimpleDateFormat("EEE dd MMM yyyy 'at' HH:mm");
        Log.d("prova", format.format(reviews.get(position).getDate().getTime()));
        holder.date.setText(format.format(reviews.get(position).getDate().getTime()));
        Bitmap picture = reviews.get(position).getPicture();
        if(picture == null)
            holder.picture.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.blank_profile));
        else
            holder.picture.setImageBitmap(picture);

        //holder.stars.setRating(reviews.get(position).getStars_number());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

}
