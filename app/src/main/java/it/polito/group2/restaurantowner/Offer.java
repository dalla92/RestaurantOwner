package it.polito.group2.restaurantowner;

import java.util.Calendar;
import java.util.Date;

public class Offer {
    private String name, description;
    private Calendar from, to;
    private boolean lunch,dinner;

    public Offer(String name, String description, Calendar from, Calendar to, boolean lunch, boolean dinner) {
        this.name = name;
        this.description = description;
        this.from = from;
        this.to = to;
        this.lunch = lunch;
        this.dinner = dinner;
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
}
