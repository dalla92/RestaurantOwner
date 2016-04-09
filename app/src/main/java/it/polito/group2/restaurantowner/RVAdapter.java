package it.polito.group2.restaurantowner;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Alessio on 09/04/2016.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.CommentViewHolder>{

    List<Comment> comments;

    RVAdapter(List<Comment> comments){
        this.comments = comments;
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
            cv = (CardView)itemView.findViewById(R.id.cv);
            CommentUsername = (TextView)itemView.findViewById(R.id.comment_username);
            CommentDate = (TextView)itemView.findViewById(R.id.comment_date);
            CommentPhoto = (ImageView)itemView.findViewById(R.id.comment_photo);
            CommentStars = (RatingBar)itemView.findViewById(R.id.comment_stars);
            Comment = (TextView)itemView.findViewById(R.id.comment);
        }
    }


    @Override
    public int getItemCount() {
        return comments.size();
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_card_view, viewGroup, false);
        CommentViewHolder pvh = new CommentViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(CommentViewHolder CommentViewHolder, int i) {
        CommentViewHolder.CommentUsername.setText(comments.get(i).username);
        CommentViewHolder.CommentDate.setText(comments.get(i).date);
        CommentViewHolder.CommentStars.setRating((float) comments.get(i).stars_number);
        CommentViewHolder.CommentPhoto.setImageResource(comments.get(i).photoId);
        CommentViewHolder.Comment.setText(comments.get(i).comment);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}