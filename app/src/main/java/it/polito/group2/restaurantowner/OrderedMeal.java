package it.polito.group2.restaurantowner;


public class OrderedMeal {
    private String meal_name, takeAwayReservationId;
    private int quantity;

    public OrderedMeal(String meal_name, int quantity, String takeAwayReservationId) {
        this.meal_name = meal_name;
        this.quantity = quantity;
        this.takeAwayReservationId = takeAwayReservationId;
    }

    public String getTakeAwayReservationId() {
        return takeAwayReservationId;
    }

    public String getMeal_name() {
        return meal_name;
    }

    public int getQuantity() {
        return quantity;
    }
}
