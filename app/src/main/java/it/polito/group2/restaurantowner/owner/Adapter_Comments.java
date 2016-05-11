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
import java.util.List;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.data.Review;

/**
 * Created by Alessio on 09/04/2016.
 */
public class Adapter_Comments extends RecyclerView.Adapter<Adapter_Comments.CommentViewHolder> {
    List<Review> reviews;
    Context context;

    Adapter_Comments(List<Review> reviews, Context context) {
        this.reviews = reviews;
        this.context = context;
    }
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView CommentUsername;
        TextView CommentDate;
        ImageView CommentPhoto;
        RatingBar CommentStars;
        TextView Comment;

        CommentViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            CommentUsername = (TextView) itemView.findViewById(R.id.comment_username);
            CommentDate = (TextView) itemView.findViewById(R.id.comment_date);
            CommentPhoto = (ImageView) itemView.findViewById(R.id.comment_photo);
            CommentStars = (RatingBar) itemView.findViewById(R.id.comment_stars);
            Comment = (TextView) itemView.findViewById(R.id.comment);
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_card_view, viewGroup, false);
        CommentViewHolder pvh = new CommentViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(CommentViewHolder CommentViewHolder, int i) {
        CommentViewHolder.CommentUsername.setText(reviews.get(i).getUserID());
        SimpleDateFormat format = new SimpleDateFormat("EEE dd MMM yyyy 'at' HH:mm");
        CommentViewHolder.CommentDate.setText(format.format(reviews.get(i).getDate().getTime()));
        CommentViewHolder.CommentStars.setRating(reviews.get(i).getStars_number());
        /*
        if(comments.get(i).userphoto!=null)
            CommentViewHolder.CommentPhoto.setImageResource(Integer.parseInt(comments.get(i).userphoto));
        */
        //CommentViewHolder.CommentPhoto.setImageURI(Uri.parse(comments.get(i).userphoto));
        //CommentViewHolder.Comment.setText(comments.get(i).comment);
        String comment_ell = reviews.get(i).getComment().substring(0, 7);
        //comment_ell.concat(this.context.getResources().getString(R.string.show_more));
        comment_ell = comment_ell + this.context.getResources().getString(R.string.show_more);
        CommentViewHolder.Comment.setText(comment_ell);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}
