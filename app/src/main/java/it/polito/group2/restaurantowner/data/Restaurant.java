package it.polito.group2.restaurantowner.data;

import java.io.Serializable;

/**
 * Created by Daniele on 05/04/2016.
 */
public class Restaurant implements Serializable{
    private String name;
    private String restaurantId;
    private String userId;
    private String photoUri;
    private String address;
    private String phoneNum;
    private String category;
    private boolean fidelity;
    private boolean tableReservation;
    private boolean takeAway;
    private String tableNum;
    private String ordersPerHour;
    private String squaredMeters;
    private String closestMetro;
    private String closestBus;
    private String rating;
    private String reservationNumber;
    private String reservedPercentage;
    private String priceRange;

    @Override
    public String toString() {
        return "Restaurant{" +
                "name='" + name + '\'' +
                ", restaurantId='" + restaurantId + '\'' +
                ", userId='" + userId + '\'' +
                ", photoUri='" + photoUri + '\'' +
                ", address='" + address + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", category='" + category + '\'' +
                ", fidelity=" + fidelity +
                ", tableReservation=" + tableReservation +
                ", takeAway=" + takeAway +
                ", tableNum='" + tableNum + '\'' +
                ", ordersPerHour='" + ordersPerHour + '\'' +
                ", squaredMeters='" + squaredMeters + '\'' +
                ", closestMetro='" + closestMetro + '\'' +
                ", closestBus='" + closestBus + '\'' +
                ", rating='" + rating + '\'' +
                ", reservationNumber='" + reservationNumber + '\'' +
                ", reservedPercentage='" + reservedPercentage + '\'' +
                '}';
    }

    public Restaurant(){ }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isFidelity() {
        return fidelity;
    }

    public void setFidelity(boolean fidelity) {
        this.fidelity = fidelity;
    }

    public boolean isTableReservation() {
        return tableReservation;
    }

    public void setTableReservation(boolean tableReservation) {
        this.tableReservation = tableReservation;
    }

    public boolean isTakeAway() {
        return takeAway;
    }

    public void setTakeAway(boolean takeAway) {
        this.takeAway = takeAway;
    }

    public String getTableNum() {
        return tableNum;
    }

    public void setTableNum(String tableNum) {
        this.tableNum = tableNum;
    }

    public String getOrdersPerHour() {
        return ordersPerHour;
    }

    public void setOrdersPerHour(String ordersPerHour) {
        this.ordersPerHour = ordersPerHour;
    }

    public String getSquaredMeters() {
        return squaredMeters;
    }

    public void setSquaredMeters(String squaredMeters) {
        this.squaredMeters = squaredMeters;
    }

    public String getClosestMetro() {
        return closestMetro;
    }

    public void setClosestMetro(String closestMetro) {
        this.closestMetro = closestMetro;
    }

    public String getClosestBus() {
        return closestBus;
    }

    public void setClosestBus(String closestBus) {
        this.closestBus = closestBus;
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

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }
}
