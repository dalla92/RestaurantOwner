package it.polito.group2.restaurantowner.data;

import java.util.Calendar;

public class Offer {
    private String name, description, offerId, restaurantId;
    private Calendar from, to;
    private boolean lunch,dinner;

    public Offer(){

    }

    public Offer(String offerId, String restaurantId, String name, String description, Calendar from, Calendar to, boolean lunch, boolean dinner) {
        this.name = name;
        this.description = description;
        this.from = from;
        this.to = to;
        this.lunch = lunch;
        this.dinner = dinner;
        this.offerId = offerId;
        this.restaurantId = restaurantId;
    }



    public String getOfferId() {
        return offerId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public Calendar getFrom() {
        return from;
    }

    public Calendar getTo() {
        return to;
    }

    public boolean isLunch() {
        return lunch;
    }

    public boolean isDinner() {
        return dinner;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setFrom(Calendar from) {
        this.from = from;
    }

    public void setTo(Calendar to) {
        this.to = to;
    }

    public void setLunch(boolean lunch) {
        this.lunch = lunch;
    }

    public void setDinner(boolean dinner) {
        this.dinner = dinner;
    }
}
