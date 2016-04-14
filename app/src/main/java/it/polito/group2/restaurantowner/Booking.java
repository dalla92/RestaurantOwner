package it.polito.group2.restaurantowner;

import java.util.Date;

/**
 * Created by Filippo on 12/04/2016.
 */
public class Booking {
    private Restaurant restObj;
    private User usrObj;
    private Date reservationTime;
    private int sits;

    public Restaurant getRestObj() {
        return restObj;
    }

    public void setRestObj(Restaurant restObj) {
        this.restObj = restObj;
    }

    public User getUsrObj() {
        return usrObj;
    }

    public void setUsrObj(User usrObj) {
        this.usrObj = usrObj;
    }

    public Date getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(Date reservationTime) {
        this.reservationTime = reservationTime;
    }

    public int getSits() {
        return sits;
    }

    public void setSits(int sits) {
        this.sits = sits;
    }
}
