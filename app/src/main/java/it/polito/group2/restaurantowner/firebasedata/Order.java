package it.polito.group2.restaurantowner.firebasedata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Alessio on 16/05/2016.
 */
public class Order implements Serializable {

    private String order_id;
    private String restaurant_id;
    private String user_id;
    private String user_full_name;
    private Long order_date;
    private String order_notes;
    private HashMap<String, Meal> order_meals = new HashMap<>();
    private Double order_price;
    private boolean fidelity_applied = false;
    private Offer offer_applied = null;

    public Order(){

    }

    public void calendarToOrderDate(Calendar date) {
        setOrder_date(date.getTimeInMillis());
    }

    public Calendar orderDateToCalendar() {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(getOrder_date());
        return date;
    }

    public void addMeal(Meal meal) {
        order_meals.put(Integer.toString(meal.hashCode()), meal);
    }

    public void delMeal(Meal meal) {
        order_meals.remove(Integer.toString(meal.hashCode()));
    }

    public ArrayList<Meal> allMeals() {
        ArrayList<Meal> list = new ArrayList<>();
        list.addAll(order_meals.values());
        return list;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_full_name() {
        return user_full_name;
    }

    public void setUser_full_name(String user_full_name) {
        this.user_full_name = user_full_name;
    }

    public Long getOrder_date() {
        return order_date;
    }

    public void setOrder_date(Long order_date) {
        this.order_date = order_date;
    }

    public String getOrder_notes() {
        return order_notes;
    }

    public void setOrder_notes(String order_notes) {
        this.order_notes = order_notes;
    }

    public HashMap<String, Meal> getOrder_meals() {
        return order_meals;
    }

    public void setOrder_meals(HashMap<String, Meal> order_meals) {
        this.order_meals = order_meals;
    }

    public boolean getFidelity_applied() {
        return fidelity_applied;
    }

    public void setFidelity_applied(boolean fidelity_applied) {
        this.fidelity_applied = fidelity_applied;
    }

    public Double getOrder_price() {
        return order_price;
    }

    public void setOrder_price(Double order_price) {
        this.order_price = order_price;
    }

    public Offer getOffer_applied() {
        return offer_applied;
    }

    public void setOffer_applied(Offer offer_applied) {
        this.offer_applied = offer_applied;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (fidelity_applied != order.fidelity_applied) return false;
        if (order_id != null ? !order_id.equals(order.order_id) : order.order_id != null)
            return false;
        if (restaurant_id != null ? !restaurant_id.equals(order.restaurant_id) : order.restaurant_id != null)
            return false;
        if (user_id != null ? !user_id.equals(order.user_id) : order.user_id != null) return false;
        if (user_full_name != null ? !user_full_name.equals(order.user_full_name) : order.user_full_name != null)
            return false;
        if (order_date != null ? !order_date.equals(order.order_date) : order.order_date != null)
            return false;
        if (order_notes != null ? !order_notes.equals(order.order_notes) : order.order_notes != null)
            return false;
        if (order_meals != null ? !order_meals.equals(order.order_meals) : order.order_meals != null)
            return false;
        if (order_price != null ? !order_price.equals(order.order_price) : order.order_price != null)
            return false;
        return !(offer_applied != null ? !offer_applied.equals(order.offer_applied) : order.offer_applied != null);

    }

    @Override
    public int hashCode() {
        int result = order_id != null ? order_id.hashCode() : 0;
        result = 31 * result + (restaurant_id != null ? restaurant_id.hashCode() : 0);
        result = 31 * result + (user_id != null ? user_id.hashCode() : 0);
        result = 31 * result + (user_full_name != null ? user_full_name.hashCode() : 0);
        result = 31 * result + (order_date != null ? order_date.hashCode() : 0);
        result = 31 * result + (order_notes != null ? order_notes.hashCode() : 0);
        result = 31 * result + (order_meals != null ? order_meals.hashCode() : 0);
        result = 31 * result + (order_price != null ? order_price.hashCode() : 0);
        result = 31 * result + (fidelity_applied ? 1 : 0);
        result = 31 * result + (offer_applied != null ? offer_applied.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Order{" +
                "order_id='" + order_id + '\'' +
                ", restaurant_id='" + restaurant_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", user_full_name='" + user_full_name + '\'' +
                ", order_date=" + order_date +
                ", order_notes='" + order_notes + '\'' +
                ", order_meals=" + order_meals +
                ", order_price=" + order_price +
                ", fidelity_applied=" + fidelity_applied +
                '}';
    }
}
