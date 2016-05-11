package it.polito.group2.restaurantowner.user;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
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
import it.polito.group2.restaurantowner.data.Review;
import it.polito.group2.restaurantowner.owner.ItemTouchHelperAdapter;

public class MyReviewAdapter extends RecyclerView.Adapter<MyReviewAdapter.ReviewViewHolder> implements ItemTouchHelperAdapter {

    private ArrayList<Review> reviews;
    private Context context;

    public MyReviewAdapter(ArrayList<Review> reviews, Context context) {
        this.reviews = reviews;
        this.context = context;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
    }

    @Override
    public void onItemDismiss(final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Confirmation!");
        alert.setMessage("Are you sure you want to delete the review?\nThis operation cannot be undone!");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeItem(position);
                dialog.dismiss();

            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        alert.show();
    }

    public void removeItem(int position){
        reviews.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, reviews.size());
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
        holder.username.setText(reviews.get(position).getUserID());
        holder.stars.setRating(reviews.get(position).getStars_number());
        SimpleDateFormat format = new SimpleDateFormat("EEE dd MMM yyyy 'at' HH:mm");
        holder.date.setText(format.format(reviews.get(position).getDate().getTime()));

        //TODO get the user picture with the UserID
        holder.picture.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.blank_profile_thumb));

        if(reviews.get(position).getComment().equals(""))
            holder.comment.setVisibility(View.GONE);
        else
            holder.comment.setText(reviews.get(position).getComment());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

}
