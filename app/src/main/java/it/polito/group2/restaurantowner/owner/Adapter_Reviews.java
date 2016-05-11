package it.polito.group2.restaurantowner.owner;

import android.content.Context;
import android.support.v7.widget.CardView;
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

/**
 * Created by Filippo on 14/04/2016.
 */
public class Adapter_Reviews extends RecyclerView.Adapter<Adapter_Reviews.ReviewViewHolder> {

    ArrayList<Review> reviews;
    Context context;

    Adapter_Reviews(ArrayList<Review> reviews, Context context) {
        this.reviews = reviews;
        this.context = context;
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        TextView userName;
        TextView date;
        ImageView userPhoto;
        RatingBar stars;
        TextView comment;
        //ImageView replyBtn;
        //ImageView warningBtn;

        ReviewViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            userName = (TextView) itemView.findViewById(R.id.userName);
            date = (TextView) itemView.findViewById(R.id.date);
            userPhoto = (ImageView) itemView.findViewById(R.id.userPhoto);
            stars = (RatingBar) itemView.findViewById(R.id.stars);
            comment = (TextView) itemView.findViewById(R.id.comment);
            //replyBtn = (ImageView) itemView.findViewById(R.id.replyBtn);
            //warningBtn = (ImageView) itemView.findViewById(R.id.warningBtn);
        }

    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_reviews, viewGroup, false);
        ReviewViewHolder pvh = new ReviewViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder reviewViewHolder, final int i) {

        reviewViewHolder.userName.setText(reviews.get(i).getUserID());
        SimpleDateFormat format = new SimpleDateFormat("EEE dd MMM yyyy 'at' HH:mm");
        reviewViewHolder.date.setText(format.format(reviews.get(i).getDate().getTime()));
        reviewViewHolder.stars.setRating(reviews.get(i).getStars_number());

        if(reviews.get(i).getUserphoto()!=null)
            reviewViewHolder.userPhoto.setImageResource(Integer.parseInt(reviews.get(i).getUserphoto()));

        String comment_ell = reviews.get(i).getComment().substring(0, 7);
        comment_ell = comment_ell +  this.context.getResources().getString(R.string.show_more);
        reviewViewHolder.comment.setText(comment_ell);

        /*
        reviewViewHolder.replyBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendReply(comments.get(i).getCommentID());
            }
        });

        reviewViewHolder.warningBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendWarning(comments.get(i).getCommentID());
            }
        });
        */
    }

    /*
    public void sendReply(String commentID) {

    }

    public void sendWarning(String commentID) {
        //i don't know how to pass the comment id
    }
    */

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
