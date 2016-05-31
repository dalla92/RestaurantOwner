package it.polito.group2.restaurantowner.user.restaurant_page;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.Review;
import it.polito.group2.restaurantowner.gallery.GalleryViewItem;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>  {

    private ArrayList<Review> reviews;
    private Context context;

    public ReviewAdapter(ArrayList<Review> reviews, Context context) {
        this.reviews = reviews;
        this.context = context;
    }

    public void addReview(Review review){
        this.reviews.add(0, review);
        notifyItemInserted(0);
    }
    public void setReviewData(ArrayList<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }


    public class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
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
            comment.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v instanceof TextView) {
                TextView tv = (TextView) v;
                int collapsedMaxLines = 2;
                ObjectAnimator animation = ObjectAnimator.ofInt(tv, "maxLines", tv.getMaxLines() == collapsedMaxLines? 200 : collapsedMaxLines);
                animation.setDuration(200).start();
            }
        }

    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_review, parent, false);
        return new ReviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.username.setText(reviews.get(position).getUser_full_name());
        holder.stars.setRating(reviews.get(position).getReview_rating());
        SimpleDateFormat format = new SimpleDateFormat("EEE dd MMM yyyy 'at' HH:mm");
        holder.date.setText(format.format(reviews.get(position).getReview_date().getTime()));

        //TODO get the user picture with the UserID
        holder.picture.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.blank_profile_thumb));

        if(reviews.get(position).getReview_comment().equals(""))
            holder.comment.setVisibility(View.GONE);
        else
            holder.comment.setText(reviews.get(position).getReview_comment());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

}
