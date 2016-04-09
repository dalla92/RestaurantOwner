package it.polito.group2.restaurantowner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alessio on 09/04/2016.
 */
public class Comment {
        String username;
        String date;
        double stars_number;
        String comment;
        int photoId;

        Comment(String username, String date, double stars_number, int photoId, String comment) {
            this.username = username;
            this.date = date;
            this.photoId = photoId;
            this.stars_number=stars_number;
            this.comment=comment;
        }


}
