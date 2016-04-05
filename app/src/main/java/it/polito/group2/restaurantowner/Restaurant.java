package it.polito.group2.restaurantowner;

/**
 * Created by Daniele on 05/04/2016.
 */
public class Restaurant {
    private String name;
    private String rating;
    private String reservationNumber;
    private String reservedPercentage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReservationNumber() {
        return reservationNumber;
    }

    public void setReservationNumber(String reservationNumber) {
        this.reservationNumber = reservationNumber;
    }

    public String getReservedPercentage() {
        return reservedPercentage;
    }

    public void setReservedPercentage(String reservedPercentage) {
        this.reservedPercentage = reservedPercentage;
    }
}
