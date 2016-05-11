package it.polito.group2.restaurantowner.data;


public class OrderedMeal {
    private String meal_name, takeAwayReservationId;
    private int quantity;

    public OrderedMeal() {
    }

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

    public void setMeal_name(String meal_name) {
        this.meal_name = meal_name;
    }


    public void setTakeAwayReservationId(String takeAwayReservationId) {
        this.takeAwayReservationId = takeAwayReservationId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
