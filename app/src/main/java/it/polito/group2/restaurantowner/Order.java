package it.polito.group2.restaurantowner;

import java.util.Date;

/**
 * Created by Filippo on 13/04/2016.
 */
public class Order {

    private Restaurant restObj;
    private User usrObj;
    private Date orderTime;

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }
}
